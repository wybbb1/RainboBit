<template>
  <div class="categories-page">
    <h1>文章分类</h1>
    <div v-if="loading" class="loading">
      <p>加载中...</p>
    </div>
    <div v-else class="categories-container">
      <div class="category-stats">
        <div class="stats-card">
          <h3>总分类数</h3>
          <span class="stats-number">{{ categories.length }}</span>
        </div>
        <div class="stats-card stats-card-hidden-mobile">
          <h3>总文章数</h3>
          <span class="stats-number">{{ articles.length }}</span>
        </div>
        <div class="stats-card stats-card-hidden-mobile">
          <h3>总标签数</h3>
          <span class="stats-number">{{ tags.length }}</span>
        </div>
      </div>

      <div class="category-grid">
        <div v-for="category in categories" :key="category.id" class="category-card">
          <router-link :to="`/categories/${category.id}`">
            <div class="category-header">
              <h3 class="category-name">{{ category.name }}</h3>
              <span class="article-count">{{ getCategoryArticleCount(Number(category.id)) }} 篇文章</span>
            </div>
            <div class="category-description" v-if="category.description">
              {{ category.description }}
            </div>
            <div class="category-meta">
              <span class="create-time">创建于: {{ formatDate(category.createTime) }}</span>
              <span class="status" :class="getStatusClass(category.status)">
                {{ getStatusText(category.status) }}
              </span>
            </div>
            <div class="category-progress">
              <div 
                class="progress-bar" 
                :style="{ width: getProgressWidth(Number(category.id)) + '%' }"
              ></div>
            </div>
          </router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { categoryApi } from '@/api/category';
import { articleApi } from '@/api/article';
import { tagApi } from '@/api/tag';
import type { Category, Article, Tag } from '@/types';
import { article } from '@/api';

const categories = ref<Category[]>([]);
const articles = ref<Article[]>([]);
const tags = ref<Tag[]>([]);
const loading = ref(false);

// 获取分类对应的文章数量
const getCategoryArticleCount = (categoryId: number) => {
  return articles.value.filter(article => 
    Number(article.categoryId) === categoryId
  ).length;
};

// 获取进度条宽度（基于文章数量的相对比例）
const getProgressWidth = (categoryId: number) => {
  const maxCount = Math.max(...categories.value.map(cat => getCategoryArticleCount(Number(cat.id))));
  const currentCount = getCategoryArticleCount(categoryId);
  return maxCount > 0 ? (currentCount / maxCount) * 100 : 0;
};

// 加载所有数据
const loadData = async () => {
  try {
    loading.value = true;
    
    // 并行加载数据
    const [categoryList, articleResponse, tagList] = await Promise.all([
      categoryApi.getCategoryList(),
      articleApi.getArticleList({ page: 1, pageSize: 1000, status: '0' }),
      tagApi.getTagList()
    ]);
    
    categories.value = categoryList || [];
    articles.value = articleResponse.rows || [];
    tags.value = tagList || [];
  } catch (error) {
    console.error('加载数据失败:', error);
  } finally {
    loading.value = false;
  }
};

// 状态相关函数
const getStatusText = (status: string) => {
  const statusMap = {
    '0': '正常',
    '1': '禁用'
  };
  return statusMap[status as keyof typeof statusMap] || '未知';
};

const getStatusClass = (status: string) => {
  const classMap = {
    '0': 'status-normal',
    '1': 'status-disabled'
  };
  return classMap[status as keyof typeof classMap] || 'status-unknown';
};

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

onMounted(() => {
  loadData();
});
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;
@use '@/assets/styles/mixins' as mixins;

.categories-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;

  @media (max-width: 768px) {
    padding: 0 15px;
  }

  @media (max-width: 480px) {
    padding: 0 10px;
  }
}

.category-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 40px;

  // 大屏幕显示三个卡片
  @media (min-width: 769px) {
    grid-template-columns: repeat(3, 1fr);
    gap: 24px;
  }

  // 小屏幕只显示第一个卡片
  @media (max-width: 768px) {
    grid-template-columns: 1fr;
    gap: 15px;

    .stats-card-hidden-mobile {
      display: none;
    }
  }
}

.stats-card {
  @include mixins.card-style;
  @include mixins.card-padding;
  text-align: center;

  h3 {
    color: $text-color-secondary;
    margin-bottom: 15px;
    font-size: 1rem;
  }

  .stats-number {
    font-size: 2.5rem;
    font-weight: bold;
    color: $primary-color;

    @media (max-width: 768px) {
      font-size: 2rem;
    }
  }
}

.category-grid {
  display: grid;
  gap: 20px;
  margin-bottom: 60px;

  // 超大屏幕：尽量一行显示所有分类，如果太多则换行
  @media (min-width: 1400px) {
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 24px;
  }

  // 大屏幕
  @media (min-width: 1024px) and (max-width: 1399px) {
    grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
    gap: 22px;
  }

  // 中等屏幕
  @media (min-width: 640px) and (max-width: 1023px) {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;
  }

  // 手机横屏
  @media (max-width: 899px) {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
  }
}

.category-card {
  @include mixins.card-style;

  a {
    display: block;
    @include mixins.card-padding;
    color: inherit;
    text-decoration: none;
  }
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;

  @media (max-width: 480px) {
    flex-direction: column;
    gap: 10px;
  }

  .category-name {
    font-size: 1.4rem;
    font-weight: bold;
    color: $primary-color;
    margin: 0;

    @media (max-width: 768px) {
      font-size: 1.2rem;
    }
  }

  .article-count {
    background: rgba($primary-color, 0.1);
    color: $primary-color;
    padding: 4px 12px;
    border-radius: 16px;
    font-size: 0.9rem;
    white-space: nowrap;

    @media (max-width: 480px) {
      align-self: flex-start;
    }
  }
}

.category-description {
  color: $text-color-secondary;
  line-height: 1.6;
  margin-bottom: 20px;
  font-size: 0.95rem;
}

.category-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  font-size: 0.85rem;

  @media (max-width: 480px) {
    font-size: 0.80rem;
  }

  .create-time {
    color: $text-color-secondary;
  }

  .status {
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 0.8rem;
    text-align: center;

    &.status-normal {
      background-color: rgba(0, 200, 0, 0.1);
      color: #00c800;
    }

    &.status-disabled {
      background-color: rgba(255, 0, 0, 0.1);
      color: #ff0000;
    }
  }
}

.category-progress {
  width: 100%;
  height: 4px;
  background: rgba($primary-color, 0.1);
  border-radius: 2px;
  overflow: hidden;

  .progress-bar {
    height: 100%;
    background: linear-gradient(90deg, $primary-color, $secondary-color);
    transition: width 0.3s ease;
    border-radius: 2px;
  }
}
</style>