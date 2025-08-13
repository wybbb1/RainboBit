<template>
  <div class="category-tree-node" :class="`level-${level}`">
    <div class="node-content">
      <router-link :to="`/categories/${category.id}`" class="category-link">
        <span class="category-name">{{ category.name }}</span>
        <span class="article-count">{{ getCategoryArticleCount(Number(category.id)) }}</span>
      </router-link>
    </div>
    <div v-if="category.children && category.children.length > 0" class="children">
      <CategoryTreeNode 
        v-for="child in category.children" 
        :key="child.id"
        :category="child"
        :level="level + 1"
        :articles="articles"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import type { Category, Article } from '@/types';

// 添加组件名称声明以支持递归调用
defineOptions({
  name: 'CategoryTreeNode'
});

interface Props {
  category: Category & { children?: Category[] };
  level: number;
  articles?: Article[]; // 可选的文章数据
}

const props = defineProps<Props>();

// 获取分类对应的文章数?
const getCategoryArticleCount = (categoryId: number) => {
  if (!props.articles) return 0;
  return props.articles.filter(article => 
    article.categoryId === categoryId && 
    article.status === '0' && 
    article.delFlag === 0
  ).length;
};
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;

.category-tree-node {
  margin-bottom: 8px;

  &.level-0 {
    .node-content {
      font-weight: bold;
      
      .category-link {
        color: $primary-color;
      }
    }
  }

  &.level-1 {
    margin-left: 20px;
    
    .node-content::before {
      content: '├─ ';
      color: $text-color-secondary;
    }
  }

  &.level-2 {
    margin-left: 40px;
    
    .node-content::before {
      content: '└─ ';
      color: $text-color-secondary;
    }
  }
}

.node-content {
  display: flex;
  align-items: center;
  padding: 8px 0;
}

.category-link {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
  color: $text-color;
  text-decoration: none;
  transition: color 0.3s ease;

  &:hover {
    color: $primary-color;
  }
}

.category-name {
  flex: 1;
}

.article-count {
  background: rgba($primary-color, 0.1);
  color: $primary-color;
  padding: 2px 8px;
  border-radius: 12px;
  font-size: 0.8rem;
  margin-left: 10px;
}

.children {
  border-left: 1px solid rgba($border-color, 0.5);
  margin-left: 10px;
  padding-left: 10px;
}
</style>
