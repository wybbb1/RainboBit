<template>
  <div v-if="loading" class="loading">
    <p>加载中...</p>
  </div>
  <div class="article-detail-page" v-else-if="article">
    <h1 class="article-title">{{ article.title }}</h1>
    <div class="article-meta">
      <span class="publish-date">
        <i class="date-icon">📅</i>
        发布时间: {{ formatDate(article.createTime) }}
      </span>
      <span class="separator">|</span>
      <span class="category">
        <i class="category-icon">📂</i>
        分类: 
        <router-link :to="`/categories/${article.categoryId}`" class="category-link">
          {{ article.categoryName }}
        </router-link>
      </span>
      <span class="separator">|</span>
      <span class="view-count">
        <i class="view-icon">👀</i>
        浏览: {{ article.viewCount }}
      </span>
      <span class="separator">|</span>
      <div class="tags" v-if="getArticleTags(article.id).length > 0">
        <i class="tag-icon">🏷️</i>
        <router-link 
          v-for="tag in getArticleTags(article.id)" 
          :key="tag.id" 
          :to="`/tags/${tag.name}`"
          class="tag-link"
        >
          #{{ tag.name }}
        </router-link>
      </div>
    </div>
    <div class="article-content" v-html="renderedContent"></div>
    <Comments v-if="article.isComment === '0'" />
  </div>
  <div v-else class="not-found">
    <h1>文章未找到</h1>
    <p>抱歉，您要查找的文章不存在或已被删除。</p>
    <router-link to="/" class="back-link">返回首页</router-link>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import { useRoute } from 'vue-router';
import { articleApi } from '@/api/article';
import { tagApi } from '@/api/tag';
import type { Article, Tag } from '@/types';
import Comments from '@/components/Comments.vue';
import { marked } from 'marked';
import hljs from 'highlight.js';
import '@/assets/styles/highlight.scss';

const route = useRoute();
const article = ref<Article | null>(null);
const tags = ref<Tag[]>([]);
const loading = ref(false);

// 配置 marked 的渲染器和代码高亮
const renderer = new marked.Renderer();

// 自定义代码块渲染 - 使用新版本 marked 的 API
renderer.code = function({ text, lang }: { text: string; lang?: string }) {
  let highlightedCode: string;
  
  if (lang && hljs.getLanguage(lang)) {
    try {
      highlightedCode = hljs.highlight(text, { language: lang }).value;
    } catch (err) {
      console.error('Code highlighting error:', err);
      highlightedCode = text;
    }
  } else {
    try {
      highlightedCode = hljs.highlightAuto(text).value;
    } catch (err) {
      console.error('Auto highlighting error:', err);
      highlightedCode = text;
    }
  }
  
  const langClass = lang ? ` class="language-${lang}"` : '';
  return `<pre><code class="hljs"${langClass}>${highlightedCode}</code></pre>`;
};

// 自定义内联代码渲染
renderer.codespan = function({ text }: { text: string }) {
  return `<code class="inline-code">${text}</code>`;
};

// 配置 marked 选项
marked.setOptions({
  renderer: renderer,
  gfm: true,
  breaks: true
});

const renderedContent = computed(() => {
  if (article.value) {
    return marked(article.value.content);
  }
  return '';
});

// 获取文章标签
const getArticleTags = (articleId: number | string): Tag[] => {
  // 在文章详情页，我们已经有了当前文章的数据
  if (!article.value || !article.value.tagIds || article.value.tagIds.length === 0) {
    return [];
  }
  
  // 根据标签ID从所有标签中筛选出对应的标签
  return tags.value.filter(tag => article.value!.tagIds.includes(tag.id));
};

// 加载文章详情
const loadArticle = async (id: number | string) => {
  try {
    loading.value = true;
    const articleData = await articleApi.getArticleDetail(id);
    article.value = articleData;
    
    // 更新浏览量
    try {
      await articleApi.updateViewCount(id);
    } catch (error) {
      console.error('更新浏览量失败', error);
    }
  } catch (error) {
    console.error('加载文章详情失败:', error);
    article.value = null;
  } finally {
    loading.value = false;
  }
};

// 获取所有标签
const loadTags = async () => {
  try {
    const response = await tagApi.getTagList();
    tags.value = response || [];
  } catch (error) {
    console.error('加载标签列表失败:', error);
  }
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

onMounted(async () => {
  const articleId = route.params.id as string;

  // 并行加载数据
  await Promise.all([
    loadArticle(articleId),
    loadTags()
  ]);
});
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;

.article-title {
  font-size: 2.5rem;
  margin-bottom: 20px;
}

.article-meta {
  margin-bottom: 40px;
  color: $text-color-secondary;
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 12px;

  @media (max-width: 768px) {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  span {
    display: flex;
    align-items: center;
    gap: 4px;
  }

  .separator {
    color: $border-color;
    font-weight: normal;
    
    @media (max-width: 768px) {
      display: none;
    }
  }

  .publish-date, .category, .view-count {
    padding: 4px 8px;
    background: rgba(255, 255, 255, 0.05);
    border-radius: 4px;
    transition: background-color 0.3s ease;

    &:hover {
      background: rgba($primary-color, 0.1);
    }
  }

  .category-link {
    color: $text-color-secondary;
    text-decoration: none;
    font-weight: 500;
    margin-left: 4px;

    &:hover {
      color: $secondary-color;
      text-decoration: underline;
    }
  }

  .tags {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 4px;
    flex-wrap: wrap;

    @media (max-width: 768px) {
      margin-top: 0;
    }
  }

  .tag-link {
    color: $text-color-secondary;
    text-decoration: none;
    padding: 2px 6px;
    background: rgba($secondary-color, 0.1);
    border-radius: 12px;
    font-size: 0.8rem;
    transition: all 0.3s ease;

    &:hover {
      color: $secondary-color;
      background: rgba($secondary-color, 0.2);
      transform: translateY(-1px);
    }
  }

  // 图标样式
  .date-icon, .category-icon, .view-icon, .tag-icon {
    font-size: 0.8rem;
    opacity: 0.8;
  }
}

.article-content {
  // `v-html` 渲染的内容，需要使用 `:deep()` 选择器来穿透作用域
  :deep(h2) {
    font-size: 1.8rem;
    margin-top: 40px;
    margin-bottom: 20px;
    border-bottom: 1px solid $border-color;
    padding-bottom: 10px;
  }

  :deep(h3) {
    font-size: 1.5rem;
    margin-top: 30px;
    margin-bottom: 15px;
  }

  :deep(p) {
    margin-bottom: 20px;
    line-height: 1.8;
  }

  :deep(pre) {
    margin-bottom: 20px;
    border-radius: 8px;
    overflow-x: auto;
    background: #282c34 !important;

    code {
      padding: 16px;
      display: block;
      line-height: 1.5;
      font-size: 0.9rem;
      
      @media (max-width: 768px) {
        font-size: 0.8rem;
        padding: 12px;
      }
    }
  }

  :deep(code) {
    font-family: $font-family-monospace;
    
    &.inline-code {
      background: rgba(255, 255, 255, 0.1);
      padding: 2px 6px;
      border-radius: 4px;
      font-size: 0.9rem;
      color: #e06c75;
      border: 1px solid rgba(255, 255, 255, 0.1);
    }
  }

  // 图片样式限制
  :deep(img) {
    max-width: 100%;
    height: auto;
    display: block;
    margin: 20px auto;
    border-radius: 8px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;

    &:hover {
      transform: scale(1.02);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    }

    // 针对小屏幕的特殊处理
    @media (max-width: 768px) {
      margin: 15px auto;
      border-radius: 6px;
      
      &:hover {
        transform: none; // 移动端不进行缩放
      }
    }

    // 如果图片在段落中，调整间距
    p & {
      margin: 15px auto;
    }

    // 如果图片很小（可能是表情或图标），不进行居中
    &[width], &[style*="width"] {
      &[width^="1"], &[width^="2"], &[width^="3"] {
        display: inline;
        margin: 0 2px;
        border-radius: 4px;
        box-shadow: none;
        
        &:hover {
          transform: none;
        }
      }
    }
  }
}
</style>