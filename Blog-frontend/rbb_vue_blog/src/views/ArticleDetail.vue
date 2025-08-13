<template>
  <div v-if="loading" class="loading">
    <p>加载中...</p>
  </div>
  <div class="article-detail-page" v-else-if="article">
    <div class="article-main">
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
            {{ category?.name || '加载中...' }}
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
    
    <!-- 右侧目录导航 -->
    <div class="article-sidebar" v-if="tocItems.length > 0">
      <div class="toc-container">
        <h3 class="toc-title">目录</h3>
        <nav class="toc-nav">
          <ul class="toc-list">
            <li 
              v-for="item in tocItems" 
              :key="item.id"
              :class="['toc-item', `toc-level-${item.level}`, { active: activeHeading === item.id }]"
            >
              <a 
                :href="`#${item.id}`" 
                @click="scrollToHeading(item.id, $event)"
                class="toc-link"
              >
                {{ item.text }}
              </a>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  </div>
  <div v-else class="not-found">
    <h1>文章未找到</h1>
    <p>抱歉，您要查找的文章不存在或已被删除。</p>
    <router-link to="/" class="back-link">返回首页</router-link>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, nextTick, onUnmounted } from 'vue';
import { useRoute } from 'vue-router';
import { articleApi } from '@/api/article';
import { tagApi } from '@/api/tag';
import { categoryApi } from '@/api/category';
import type { Article, Tag, Category } from '@/types';
import Comments from '@/components/Comments.vue';
import { marked } from 'marked';
import hljs from 'highlight.js';
import '@/assets/styles/highlight.scss';

// 目录项类型定义
interface TocItem {
  id: string;
  text: string;
  level: number;
}

const route = useRoute();
const article = ref<Article | null>(null);
const tags = ref<Tag[]>([]);
const category = ref<Category | null>(null);
const loading = ref(false);
const tocItems = ref<TocItem[]>([]);
const activeHeading = ref<string>('');

// 配置 marked 的渲染器和代码高亮
const renderer = new marked.Renderer();

// 自定义标题渲染器，添加 id 属性用于锚点定位
renderer.heading = function({ text, depth }: { text: string; depth: number }) {
  const id = text
    .toLowerCase()
    .replace(/[^\w\u4e00-\u9fa5]+/g, '-')
    .replace(/^-|-$/g, '');
  
  return `<h${depth} id="${id}">${text}</h${depth}>`;
};

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
    const content = marked(article.value.content);
    // 内容渲染后，解析目录
    nextTick(() => {
      extractTocFromContent();
      updateActiveHeading();
    });
    return content;
  }
  return '';
});

// 从内容中提取目录
const extractTocFromContent = () => {
  if (!article.value?.content) {
    tocItems.value = [];
    return;
  }

  const headings: TocItem[] = [];
  const headingRegex = /^(#{1,6})\s+(.+)$/gm;
  let match;

  while ((match = headingRegex.exec(article.value.content)) !== null) {
    const level = match[1].length;
    const text = match[2].trim();
    const id = text
      .toLowerCase()
      .replace(/[^\w\u4e00-\u9fa5]+/g, '-')
      .replace(/^-|-$/g, '');
    
    headings.push({
      id,
      text,
      level
    });
  }

  tocItems.value = headings;
};

// 滚动到指定标题
const scrollToHeading = (id: string, event: Event) => {
  event.preventDefault();
  const element = document.getElementById(id);
  if (element) {
    element.scrollIntoView({
      behavior: 'smooth',
      block: 'start'
    });
    activeHeading.value = id;
  }
};

// 监听滚动，更新当前激活的标题
const updateActiveHeading = () => {
  const headings = tocItems.value.map(item => document.getElementById(item.id)).filter(Boolean);
  if (headings.length === 0) return;

  const scrollTop = window.pageYOffset || document.documentElement.scrollTop;
  const windowHeight = window.innerHeight;
  
  // 找到当前视口中最靠近顶部的标题
  let currentHeading = headings[0];
  
  for (const heading of headings) {
    if (heading && heading.offsetTop <= scrollTop + 100) {
      currentHeading = heading;
    } else {
      break;
    }
  }
  
  if (currentHeading) {
    activeHeading.value = currentHeading.id;
  }
};

// 节流函数
const throttle = (func: Function, limit: number) => {
  let inThrottle: boolean;
  return function(this: any, ...args: any[]) {
    if (!inThrottle) {
      func.apply(this, args);
      inThrottle = true;
      setTimeout(() => inThrottle = false, limit);
    }
  };
};

const throttledUpdateActiveHeading = throttle(updateActiveHeading, 100);

// 获取文章标签
const getArticleTags = (articleId: number | string): Tag[] => {
  // 在文章详情页，我们已经有了当前文章的数据
  if (!article.value || !article.value.tagIds || article.value.tagIds.length === 0) {
    return [];
  }
  
  // 根据标签ID从所有标签中筛选出对应的标签
  // 处理ID类型不匹配的问题
  return tags.value.filter(tag => 
    article.value!.tagIds.some(tagId => 
      String(tag.id) === String(tagId) || tag.id === tagId
    )
  );
};

// 加载文章详情
const loadArticle = async (id: number | string) => {
  try {
    loading.value = true;
    const articleData = await articleApi.getArticleDetail(id);
    article.value = articleData;
    
    // 如果文章有分类ID，加载分类信息
    if (articleData.categoryId) {
      await loadCategory(articleData.categoryId);
    }
    
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

// 加载分类信息
const loadCategory = async (categoryId: number | string) => {
  if (!categoryId) {
    category.value = null;
    return;
  }
  
  try {
    const categoryData = await categoryApi.getCategoryDetail(categoryId);
    category.value = categoryData;
  } catch (error) {
    console.error('加载分类信息失败:', error);
    category.value = null;
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

  // 并行加载文章详情和标签列表
  // 分类信息会在文章加载完成后根据categoryId自动加载
  await Promise.all([
    loadArticle(articleId),
    loadTags()
  ]);

  // 添加滚动监听
  window.addEventListener('scroll', throttledUpdateActiveHeading);
});

onUnmounted(() => {
  // 移除滚动监听
  window.removeEventListener('scroll', throttledUpdateActiveHeading);
});
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;

.article-detail-page {
  display: flex;
  gap: 40px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;

  @media (min-width: 1201px) {
    margin-right: calc(50% - 700px + 120px);
  }

  @media (max-width: 1200px) {
    flex-direction: column;
    gap: 20px;
  }
}

.article-main {
  flex: 1;
  min-width: 0;
}

.article-sidebar {
  width: 240px;
  flex-shrink: 0;

  @media (max-width: 1200px) {
    width: 100%;
    order: -1;
  }
}

.toc-container {
  position: sticky;
  top: 80px;
  background: rgba(255, 255, 255, 0.01);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  padding: 16px;
  max-height: calc(100vh - 120px);
  overflow-y: auto;

  @media (max-width: 1200px) {
    position: static;
    max-height: none;
    overflow-y: visible;
    background: transparent;
    border: none;
    padding: 12px 0;
  }

  &::-webkit-scrollbar {
    width: 3px;
  }

  &::-webkit-scrollbar-track {
    background: transparent;
  }

  &::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.1);
    border-radius: 3px;
  }
}

.toc-title {
  font-size: 0.9rem;
  font-weight: 500;
  margin-bottom: 12px;
  color: $text-color;
  opacity: 0.8;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  padding-bottom: 6px;
}

.toc-nav {
  .toc-list {
    list-style: none;
    padding: 0;
    margin: 0;
  }

  .toc-item {
    margin-bottom: 2px;

    &.toc-level-1 {
      margin-left: 0;
      
      .toc-link {
        font-weight: 500;
        font-size: 0.85rem;
      }
    }

    &.toc-level-2 {
      margin-left: 12px;
      
      .toc-link {
        font-size: 0.8rem;
      }
    }

    &.toc-level-3 {
      margin-left: 24px;
      
      .toc-link {
        font-size: 0.75rem;
      }
    }

    &.toc-level-4,
    &.toc-level-5,
    &.toc-level-6 {
      margin-left: 36px;
      
      .toc-link {
        font-size: 0.7rem;
      }
    }

    &.active {
      .toc-link {
        color: $primary-color;
        border-left: 2px solid $primary-color;
        padding-left: 8px;
        margin-left: -2px;
      }
    }

    .toc-link {
      display: block;
      color: $text-color-secondary;
      text-decoration: none;
      padding: 4px 0;
      border-left: 2px solid transparent;
      transition: all 0.15s ease;
      line-height: 1.3;
      word-break: break-word;

      &:hover {
        color: $text-color;
        padding-left: 4px;
        margin-left: -2px;
      }
    }
  }
}

.article-title {
  font-size: 2.5rem;
  margin-bottom: 20px;

  @media (max-width: 768px) {
    font-size: 2rem;
  }
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
  :deep(h1), :deep(h2), :deep(h3), :deep(h4), :deep(h5), :deep(h6) {
    scroll-margin-top: 80px;
  }

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

    @media (max-width: 768px) {
      margin: 15px auto;
      border-radius: 6px;
      
      &:hover {
        transform: none;
      }
    }

    p & {
      margin: 15px auto;
    }

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

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
  
  p {
    font-size: 1.2rem;
    color: $text-color-secondary;
  }
}

.not-found {
  text-align: center;
  padding: 80px 20px;
  
  h1 {
    font-size: 2rem;
    margin-bottom: 20px;
    color: $text-color;
  }
  
  p {
    font-size: 1.1rem;
    color: $text-color-secondary;
    margin-bottom: 30px;
  }
  
  .back-link {
    display: inline-block;
    padding: 12px 24px;
    background: $primary-color;
    color: white;
    text-decoration: none;
    border-radius: 6px;
    transition: background-color 0.3s ease;
    
    &:hover {
      background: darken($primary-color, 10%);
    }
  }
}
</style>