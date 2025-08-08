<template>
  <div v-if="loading" class="loading">
    <p>加载中...</p>
  </div>
  <div class="category-detail-page" v-else-if="category">
    <div class="category-header">
      <div class="breadcrumb">
        <router-link to="/categories">分类</router-link>
        <span class="separator">></span>
        <span class="current">{{ category.name }}</span>
      </div>
      
      <h1 class="category-title">{{ category.name }}</h1>
      <div class="category-info">
        <p v-if="category.description" class="category-description">
          {{ category.description }}
        </p>
        <div class="category-stats">
          <span class="stat-item">
            <strong>{{ filteredArticles.length }}</strong> 篇文章
          </span>
          <span class="stat-item">
            创建于: {{ formatDate(category.createTime) }}
          </span>
          <span class="stat-item" v-if="category.updateTime !== category.createTime">
            更新于: {{ formatDate(category.updateTime) }}
          </span>
        </div>
      </div>
    </div>

    <div class="articles-section">
      <div class="section-header">
        <h2>分类文章</h2>
        <div class="sort-options">
          <CustomSelect 
            v-model="sortBy"
            :options="sortOptions"
            @change="sortArticles"
          />
        </div>
      </div>

      <div v-if="filteredArticles.length > 0" class="article-list">
        <div v-for="article in sortedArticles" :key="article.id" class="article-item">
          <h3 class="article-title">
            <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
          </h3>
          <p class="article-summary">{{ article.summary }}</p>
          <div class="article-meta">
            <span class="publish-date">{{ formatDate(article.createTime) }}</span>
            <span class="view-count">{{ article.viewCount }} 次浏览</span>
            <span class="is-top" v-if="article.isTop === '1'">置顶</span>
          </div>
        </div>
      </div>

      <div v-else class="no-articles">
        <p>该分类下暂无文章</p>
      </div>
    </div>
  </div>
  
  <div v-else class="not-found">
    <h1>分类未找到</h1>
    <p>抱歉，您要查找的分类不存在或已被删除。</p>
    <router-link to="/categories" class="back-link">返回分类列表</router-link>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { useRoute } from 'vue-router';
import { categoryApi } from '@/api/category';
import { articleApi } from '@/api/article';
import type { Category, Article } from '@/types';
import CustomSelect from '@/components/CustomSelect.vue';

const route = useRoute();
const category = ref<Category | null>(null);
const articles = ref<Article[]>([]);
const allCategories = ref<Category[]>([]);
const sortBy = ref('time');
const loading = ref(false);

// 排序选项配置
const sortOptions = [
  { value: 'time', label: '按时间排序' },
  { value: 'views', label: '按浏览量排序' },
  { value: 'title', label: '按标题排序' }
];

// 获取当前分类
const categoryId = computed(() => Number(route.params.id));

// 获取子分类
const subcategories = computed(() => {
  if (!category.value) return [];
  return allCategories.value.filter(cat => 
    cat.pid === category.value!.id 
  );
});

// 获取分类下的文章
const filteredArticles = computed(() => {
  return articles.value.filter(article => 
    article.categoryId === category.value?.id
  );
});

// 排序后的文章
const sortedArticles = computed(() => {
  const articlesData = [...filteredArticles.value];
  
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

// 获取分类对应的文章数量
const getCategoryArticleCount = (categoryId: number) => {
  return articles.value.filter(article => 
    article.categoryId === categoryId
  ).length;
};

// 加载分类详情
const loadCategory = async (id: number) => {
  try {
    const categoryData = await categoryApi.getCategoryDetail(id);
    category.value = categoryData;
  } catch (error) {
    console.error('加载分类详情失败:', error);
    category.value = null;
  }
};

// 加载分类下的文章
const loadCategoryArticles = async (categoryId: number) => {
  try {
    const response = await articleApi.getArticleList({
      page: 1,
      pageSize: 1000, // 获取所有文章
      categoryId: categoryId,
      status: '0'
    });
    articles.value = response.rows || [];
  } catch (error) {
    console.error('加载分类文章失败:', error);
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
  try {
    loading.value = true;
    const id = categoryId.value;
    
    // 并行加载数据
    await Promise.all([
      loadCategory(id),
      loadCategoryArticles(id)
    ]);
  } finally {
    loading.value = false;
  }
});
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;

.category-header {
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

.category-title {
  font-size: 2.5rem;
  margin-bottom: 15px;
  color: $primary-color;
}

.category-info {
  background: rgba(255, 255, 255, 0.02);
  border-radius: 12px;
  padding: 25px;
  border: 1px solid $border-color;
}

.category-description {
  font-size: 1.1rem;
  line-height: 1.6;
  color: $text-color-secondary;
  margin-bottom: 20px;
}

.category-stats {
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

.subcategories-section {
  margin-bottom: 40px;
  
  h2 {
    margin-bottom: 20px;
    color: $text-color;
    border-bottom: 2px solid $primary-color;
    padding-bottom: 10px;

    @media (max-width: 768px) {
      font-size: 1.5rem;
    }
  }
}

.subcategories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;

  @media (max-width: 1200px) {
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 15px;
  }

  @media (max-width: 768px) {
    grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
    gap: 12px;
  }

  @media (max-width: 480px) {
    grid-template-columns: 1fr;
    gap: 10px;
  }
}

.subcategory-card {
  border: 1px solid $border-color;
  border-radius: 8px;
  transition: all 0.3s ease;
  
  &:hover {
    border-color: $primary-color;
    transform: translateY(-3px);
  }
  
  a {
    display: block;
    padding: 20px;
    color: inherit;
    text-decoration: none;

    @media (max-width: 768px) {
      padding: 15px;
    }
  }
  
  h3 {
    color: $primary-color;
    margin-bottom: 10px;

    @media (max-width: 768px) {
      font-size: 1.1rem;
    }
  }
  
  p {
    color: $text-color-secondary;
    line-height: 1.5;
    margin-bottom: 15px;
  }
  
  .article-count {
    background: rgba($primary-color, 0.1);
    color: $primary-color;
    padding: 4px 12px;
    border-radius: 16px;
    font-size: 0.9rem;
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

.not-found {
  text-align: center;
  padding: 80px 0;
  
  h1 {
    color: $text-color;
    margin-bottom: 20px;
  }
  
  p {
    color: $text-color-secondary;
    margin-bottom: 30px;
  }
  
  .back-link {
    display: inline-block;
    background: $primary-color;
    color: white;
    padding: 12px 24px;
    border-radius: 6px;
    text-decoration: none;
    transition: background 0.3s;
    
    &:hover {
      background: darken($primary-color, 10%);
    }
  }
}
</style>