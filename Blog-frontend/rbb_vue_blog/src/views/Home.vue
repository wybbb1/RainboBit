<template>
  <div class="home-page">
    <div v-if="loading" class="loading">
      <p>加载中...</p>
    </div>
    <div v-else class="article-list">
      <div v-for="article in articles" :key="article.id" class="article-item">
        <div class="article-content">
          <h2 class="article-title">
            <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
          </h2>
          <p class="article-summary">{{ article.summary }}</p>
          <div class="article-meta">
            <span class="publish-date">
              发布时间: {{ formatDate(article.createTime) }}
            </span>
            <span class="separator">|</span>
            <span class="category">
              分类: 
              <router-link :to="`/categories/${article.categoryId}`" class="category-link">
                {{ article.categoryName }}
              </router-link>
            </span>
            <span class="separator">|</span>
            <span class="view-count">
              浏览: {{ article.viewCount }}
            </span>
            <span class="separator">|</span>
            <div class="tags" v-if="getArticleTags(Number(article.id)).length > 0">
              <router-link 
                v-for="tag in getArticleTags(Number(article.id))" 
                :key="tag.id" 
                :to="`/tags/${tag.name}`"
                class="tag-link"
              >
                #{{ tag.name }}
              </router-link>
            </div>
          </div>
        </div>
        <div v-if="article.thumbnail" class="article-thumbnail">
          <img :src="article.thumbnail" :alt="article.title" />
        </div>
      </div>
      
      <PageForm 
        v-if="!loading && articles.length > 0"
        :currentPage="currentPage"
        :totalPages="Math.ceil(total / pageSize)"
        @page-change="handlePageChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue';
import { articleApi } from '@/api/article';
import type { Article, Tag } from '@/types';
import { tagApi } from '@/api';
import PageForm from '@/components/PageForm.vue';

const articles = ref<Article[]>([]);
const tags = ref<Tag[]>([]);
const loading = ref(false);
// 分页相关变量
const currentPage = ref(1);
const pageSize = ref(5);
const total = ref(0);

// 获取文章标签
const getArticleTags = (articleId: number): Tag[] => {
  // 根据文章ID找到对应的文章
  const article = articles.value.find(a => Number(a.id) === articleId);
  if (!article || !article.tagIds || article.tagIds.length === 0) {
    return [];
  }
  
  // 根据标签ID从所有标签中筛选出对应的标签
  return tags.value.filter(tag => article.tagIds.includes(tag.id));
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

// 加载文章列表
const loadArticles = async (page: number = currentPage.value) => {
  try {
    loading.value = true;
    const response = await articleApi.getArticleList({
      page: page,
      pageSize: pageSize.value,
      status: '0' // 只获取已发布的文章
    });
    articles.value = response.rows || [];
    total.value = Number(response.total) || 0;
    currentPage.value = page;
  } catch (error) {
    console.error('加载文章列表失败:', error);
  } finally {
    loading.value = false;
  }
};

// 分页改变事件
const handlePageChange = (page: number) => {
  loadArticles(page);
  // 滚动到页面顶部
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

onMounted(async () => {
  await Promise.all([
    loadArticles(),
    loadTags()
  ]);
});

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;

.article-item {
  padding: 30px;
  width: 100%;
  border-right: 2px solid transparent;
  border-bottom: 2px solid transparent;
  border-radius: $border-radius;
  background-color: rgba(255, 255, 255, 0.02);
  margin: 0 auto 30px auto; // 居中显示
  transition: all $transition-speed ease;
  box-shadow: 0 4px 15px $shadow-color;
  display: flex;
  align-items: flex-start;
  gap: 20px;

  &:hover {
    transform: translateY(-5px);
    border-color: $primary-color;
    box-shadow: 0 8px 25px rgba($primary-color, 0.3);
  }
}

.article-content {
  flex: 1;
  min-width: 0; // 防止flex项目溢出
}

.article-thumbnail {
  flex-shrink: 0;
  width: 200px;
  height: 150px;
  border-radius: 8px;
  overflow: hidden;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;

    &:hover {
      transform: scale(1.05);
    }
  }

  @media (max-width: 768px) {
    width: 120px;
    height: 90px;
  }
}

.article-title a {
  font-size: 1.8rem;
  color: $text-color;
  transition: color 0.3s;

  &:hover {
    color: $primary-color;
  }
}

.article-summary {
  color: $text-color-secondary;
  margin-top: 10px;
}

.article-meta {
  margin-top: 20px;
  font-size: 0.9rem;
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
    text-decoration: underline;
    font-weight: 500;
    margin-left: 4px;

    &:hover {
      color: $secondary-color;
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
</style>