<template>
  <div class="tag-detail-page">
    <h1>标签: {{ tagName }}</h1>
    <div v-if="loading" class="loading">
      <p>加载中...</p>
    </div>
    <div v-else>
      <div class="article-list">
        <div v-for="article in articles" :key="article.id" class="article-item">
          <h2 class="article-title">
            <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
          </h2>
          <p class="article-summary">{{ article.summary }}</p>
          <div class="article-meta">
            <span class="publish-date">{{ formatDate(article.createTime) }}</span>
            <span class="category">
              分类: 
              <router-link :to="`/categories/${article.categoryId}`" class="category-link">
                {{ getCategoryName(article.categoryId) }}
              </router-link>
            </span>
            <span class="view-count">浏览: {{ article.viewCount }}</span>
          </div>
        </div>
      </div>
      <div v-if="articles.length === 0" class="no-articles">
        <p>暂无相关文章</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import { articleApi } from '@/api/article';
import { categoryApi } from '@/api/category';
import type { Article, Category, Tag } from '@/types';

const route = useRoute();
const tagName = ref('');
const articles = ref<Article[]>([]);
const categories = ref<Category[]>([]);
const loading = ref(false);

// 获取分类名称
const getCategoryName = (categoryId: number): string => {
  const category = categories.value.find(c => c.id === categoryId);
  return category?.name || '未分类';
};

// 加载文章列表，根据标签名称筛选
const loadArticlesByTag = async (name: string) => {
  try {
    loading.value = true;
    const response = await articleApi.getArticleList({
      page: 1,
      pageSize: 1000,
      tagName: name, 
      status: '0'
    });
    articles.value = response.rows || [];
    // 假设后端返回的文章已经是按时间降序排列的，如果不是，可以在这里手动排序
    // articles.value.sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime());
  } catch (error) {
    console.error(`加载标签 "${name}" 的文章失败:`, error);
    articles.value = [];
  } finally {
    loading.value = false;
  }
};

// 加载分类列表
const loadCategories = async () => {
  try {
    const categoryList = await categoryApi.getCategoryList();
    categories.value = categoryList || [];
  } catch (error) {
    console.error('加载分类列表失败:', error);
  }
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

onMounted(async () => {
  tagName.value = route.params.name as string;
  await Promise.all([
    loadArticlesByTag(tagName.value),
    loadCategories()
  ]);
});

// 使用 watch 监听路由参数变化，当标签名改变时重新加载数据
watch(
  () => route.params.name,
  (newTagName, oldTagName) => {
    if (newTagName && newTagName !== oldTagName) {
      tagName.value = newTagName as string;
      loadArticlesByTag(newTagName as string);
    }
  }
);
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;

.article-item {
  padding: 30px 0;
  border-bottom: 1px solid $border-color;

  &:first-child {
    padding-top: 0;
  }

  &:last-child {
    border-bottom: none;
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
  line-height: 1.6;
}

.article-meta {
  margin-top: 20px;
  font-size: 0.9rem;
  color: $text-color-secondary;

  span {
    margin-right: 15px;
  }

  .category-link {
    color: $primary-color;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
}

.no-articles {
  text-align: center;
  padding: 60px 0;
  color: $text-color-secondary;
  
  p {
    font-size: 1.2rem;
  }
}
</style>