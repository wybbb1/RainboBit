<template>
  <div v-if="loading" class="loading">
    <p>加载中...</p>
  </div>
  <div class="tag-detail-page" v-else>
    <div class="tag-header">
      <div class="breadcrumb">
        <router-link to="/">首页</router-link>
        <span class="separator">></span>
        <span class="current">标签: {{ tagName }}</span>
      </div>
      
      <h1 class="tag-title">{{ tagName }}</h1>
      <div class="tag-info">
        <div class="tag-stats">
          <span class="stat-item">
            <strong>{{ articles.length }}</strong> 篇文章
          </span>
        </div>
      </div>
    </div>

    <div class="articles-section">
      <div class="section-header">
        <h2>标签文章</h2>
        <div class="sort-options">
          <CustomSelect 
            v-model="sortBy"
            :options="sortOptions"
            @change="sortArticles"
          />
        </div>
      </div>

      <div v-if="articles.length > 0" class="article-list">
        <div v-for="article in sortedArticles" :key="article.id" class="article-item">
          <h3 class="article-title">
            <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
          </h3>
          <p class="article-summary">{{ article.summary }}</p>
          <div class="article-meta">
            <span class="publish-date">{{ formatDate(article.createTime) }}</span>
            <span class="category">
              分类: 
              <router-link :to="`/categories/${article.categoryId}`" class="category-link">
                {{ getCategoryName(article.categoryId) }}
              </router-link>
            </span>
            <span class="view-count">{{ article.viewCount }} 次浏览</span>
            <span class="is-top" v-if="article.isTop === '1'">置顶</span>
          </div>
        </div>
      </div>

      <div v-else class="no-articles">
        <p>该标签下暂无文章</p>
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
import CustomSelect from '@/components/CustomSelect.vue';

const route = useRoute();
const tagName = ref('');
const articles = ref<Article[]>([]);
const categories = ref<Category[]>([]);
const loading = ref(false);
const sortBy = ref('time');

// 排序选项配置
const sortOptions = [
  { value: 'time', label: '按时间排序' },
  { value: 'views', label: '按浏览量排序' },
  { value: 'title', label: '按标题排序' }
];

// 排序后的文章
const sortedArticles = computed(() => {
  const articlesData = [...articles.value];
  
  switch (sortBy.value) {
    case 'time':
      return articlesData.sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime());
    case 'views':
      return articlesData.sort((a, b) => b.viewCount - a.viewCount);
    case 'title':
      return articlesData.sort((a, b) => a.title.localeCompare(b.title, 'zh-CN'));
    default:
      return articlesData;
  }
});

// 获取分类名称
const getCategoryName = (categoryId: number | string): string => {
  const category = categories.value.find(cat => 
    String(cat.id) === String(categoryId) || cat.id === categoryId
  );
  return category ? category.name : '未分类';
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

const sortArticles = () => {
  // 触发重新计算
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

.tag-header {
  margin-bottom: 40px;
}

.breadcrumb {
  margin-bottom: 20px;
  color: $text-color-secondary;
  
  a {
    color: $primary-color;
    text-decoration: none;
    
    &:hover {
      text-decoration: underline;
    }
  }
  
  .separator {
    margin: 0 10px;
  }
}

.tag-title {
  font-size: 2.5rem;
  margin-bottom: 15px;
  color: $primary-color;
}

.tag-info {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  padding: 25px;
  border: 1px solid $border-color;
}

.tag-stats {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
  
  .stat-item {
    color: $text-color-secondary;
    
    strong {
      color: $primary-color;
      font-size: 1.2rem;
    }
  }
}

.articles-section {
  .section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 30px;
    
    h2 {
      color: $text-color;
      border-bottom: 2px solid $primary-color;
      padding-bottom: 10px;
      margin: 0;
    }
  }
}

.article-item {
  padding: 25px 0;
  border-bottom: 1px solid $border-color;

  &:first-child {
    padding-top: 0;
  }

  &:last-child {
    border-bottom: none;
  }
}

.article-title a {
  font-size: 1.5rem;
  color: $text-color;
  text-decoration: none;
  transition: color 0.3s;

  &:hover {
    color: $primary-color;
  }
}

.article-summary {
  color: $text-color-secondary;
  line-height: 1.6;
  margin: 15px 0;
}

.article-meta {
  display: flex;
  gap: 20px;
  align-items: center;
  font-size: 0.9rem;
  color: $text-color-secondary;

  .category-link {
    color: $primary-color;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
  
  .is-top {
    background: rgba($primary-color, 0.1);
    color: $primary-color;
    padding: 2px 8px;
    border-radius: 12px;
    font-size: 0.8rem;
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

.loading {
  text-align: center;
  padding: 60px 0;
  color: $text-color-secondary;
  
  p {
    font-size: 1.2rem;
  }
}
</style>