<template>
  <div class="archives-page">
    <h1>归档</h1>
    <div v-if="loading" class="loading">
      <p>加载中...</p>
    </div>
    <div v-else>
      <div v-for="(group, year) in groupedArticles" :key="year" class="archive-group">
        <h2 class="year">{{ year }}</h2>
        <ul>
          <li v-for="article in group" :key="article.id" class="archive-item">
            <router-link :to="`/article/${article.id}`">
              <span class="date">{{ formatDate(article.createTime) }}</span>
              <span class="title">{{ article.title }}</span>
              <span class="view-count">{{ article.viewCount }} 次浏览</span>
            </router-link>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from 'vue';
import { articleApi } from '@/api/article';
import type { Article } from '@/types';

const articles = ref<Article[]>([]);
const loading = ref(false);

// 加载所有文章
const loadArticles = async () => {
  try {
    loading.value = true;
    const response = await articleApi.getArticleList({
      page: 1,
      pageSize: 1000, // 获取所有文章用于归档
      status: '0' // 只获取已发布的文章
    });
    articles.value = response.rows || [];
  } catch (error) {
    console.error('加载文章列表失败:', error);
  } finally {
    loading.value = false;
  }
};

const groupedArticles = computed(() => {
  const groups: { [key: string]: Article[] } = {};
  articles.value
    .sort((a, b) => new Date(b.createTime).getTime() - new Date(a.createTime).getTime())
    .forEach(article => {
      const year = new Date(article.createTime).getFullYear().toString();
      if (!groups[year]) {
        groups[year] = [];
      }
      groups[year].push(article);
    });
  return groups;
});

const formatDate = (dateString: string) => {
  const date = new Date(dateString);
  return `${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
};

onMounted(() => {
  loadArticles();
});
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;

.archive-group {
  margin-bottom: 40px;
}

.year {
  font-size: 2rem;
  margin-bottom: 20px;
  color: $primary-color;
}

.archive-item {
  list-style: none;
  margin-bottom: 15px;

  a {
    display: flex;
    align-items: center;
    color: $text-color;
    transition: color 0.3s;

    &:hover {
      color: $primary-color;
    }
  }

  .date {
    width: 60px;
    color: $text-color-secondary;
    font-size: 0.9rem;
  }

  .title {
    flex: 1;
    margin-left: 15px;
  }

  .view-count {
    font-size: 0.8rem;
    color: $text-color-secondary;
    margin-left: 10px;
  }
}
</style>