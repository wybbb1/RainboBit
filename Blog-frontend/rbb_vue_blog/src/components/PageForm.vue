<template>
  <div v-if="totalPages > 1" class="pagination">
    <button 
      class="page-btn"
      @click="emit('page-change', currentPage - 1)"
      :disabled="currentPage <= 1"
    >
      <span>‹</span> 上一页
    </button>
    <div class="page-info">
      <span class="current-page">{{ currentPage }}</span>
      <span class="divider">/</span>
      <span class="total-pages">{{ totalPages }}</span>
    </div>
    <button 
      class="page-btn"
      @click="emit('page-change', currentPage + 1)"
      :disabled="currentPage >= totalPages"
    >
      下一页<span>›</span>
    </button>
  </div>
</template>

<script setup lang="ts">
interface Props {
  currentPage: number
  totalPages: number
}

interface Emits {
  (e: 'page-change', page: number): void
}

defineProps<Props>()
const emit = defineEmits<Emits>()
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 32px;
  padding: 16px;
}

.page-btn {
  padding: 8px 16px;
  background: rgba(255, 255, 255, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.12);
  color: $text-color;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
  font-weight: 400;
  font-size: 0.9rem;
  display: flex;
  align-items: center;
  gap: 4px;

  &:hover:not(:disabled) {
    background: rgba($primary-color, 0.1);
    border-color: $primary-color;
    color: $primary-color;
  }

  &:disabled {
    opacity: 0.4;
    cursor: not-allowed;
  }
}

.page-info {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 8px 12px;
  background: rgba(255, 255, 255, 0.04);
  border-radius: 4px;
  font-size: 0.9rem;

  .current-page {
    color: $primary-color;
    font-weight: 500;
  }

  .divider {
    color: $text-color-secondary;
    opacity: 0.6;
  }

  .total-pages {
    color: $text-color-secondary;
  }
}
</style>
