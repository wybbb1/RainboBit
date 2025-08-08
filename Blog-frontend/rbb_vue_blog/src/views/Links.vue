<template>
  <div class="links-page">
    <h1>友情链接</h1>
    <div v-if="loading" class="loading">
      <p>加载中...</p>
    </div>
    <div v-else class="link-list">
        <div v-for="link in links" :key="link.id" class="link-item">
            <a :href="link.address" target="_blank">
                <div class="link-logo" v-if="link.logo">
                    <img :src="link.logo" :alt="link.name" />
                </div>
                <div class="link-name">{{ link.name }}</div>
                <div class="link-desc">{{ link.description }}</div>
            </a>
        </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { linkApi } from '@/api/link';
import type { Link } from '@/types';

const links = ref<Link[]>([]);
const loading = ref(false);

// 加载友链列表
const loadLinks = async () => {
  try {
    loading.value = true;
    const linkList = await linkApi.getLinkList();
    links.value = linkList || [];
  } catch (error) {
    console.error('加载友链列表失败:', error);
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  loadLinks();
});
</script>

<style lang="scss" scoped>
@use '@/assets/styles/variables' as *;
@use '@/assets/styles/mixins' as mixins;

.links-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;

  @media (max-width: 768px) {
    padding: 0 15px;
  }

  @media (max-width: 480px) {
    padding: 0 10px;
  }

  h1 {
    margin-bottom: 30px;

    @media (max-width: 768px) {
      font-size: 2rem;
      margin-bottom: 20px;
    }
  }
}

.link-list {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 16px;

    @media (min-width: 480px) {
      grid-template-columns: repeat(3, 1fr);
      gap: 12px;
    }

    // 手机竖屏样式
    @media (min-width: 320px) and (max-width: 479px) {
      grid-template-columns: repeat(2, 1fr);
      gap: 10px;
    }

    // 超小屏幕样式
    @media (max-width: 319px) {
      grid-template-columns: 1fr;
      gap: 8px;
    }
}

.link-item {
  @include mixins.card-style;

  a {
    display: block;
    @include mixins.card-padding;
    color: inherit;
    text-decoration: none;
  }
}

.link-logo {
    margin-bottom: 10px;
    
    img {
        width: 32px;
        height: 32px;
        border-radius: 4px;

        @media (max-width: 480px) {
          width: 28px;
          height: 28px;
        }
    }
}

.link-name {
    font-size: 1.2rem;
    font-weight: bold;
    color: $text-color;
    margin-bottom: 8px;

    @media (max-width: 768px) {
      font-size: 1.1rem;
    }

    @media (max-width: 480px) {
      font-size: 1rem;
    }
}

.link-desc {
    font-size: 0.9rem;
    color: $text-color-secondary;
    margin-bottom: 8px;
    line-height: 1.4;

    @media (max-width: 480px) {
      font-size: 0.85rem;
    }
}

.link-status {
    font-size: 0.8rem;
    padding: 2px 8px;
    border-radius: 12px;
    display: inline-block;

    @media (max-width: 480px) {
      font-size: 0.75rem;
      padding: 1px 6px;
    }
    
    &.status-normal {
        background-color: rgba(0, 200, 0, 0.1);
        color: #00c800;
    }
    
    &.status-rejected {
        background-color: rgba(255, 0, 0, 0.1);
        color: #ff0000;
    }
    
    &.status-pending {
        background-color: rgba(255, 165, 0, 0.1);
        color: #ffa500;
    }
}
</style>