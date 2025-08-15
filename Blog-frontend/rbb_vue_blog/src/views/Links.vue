<template>
  <div class="links-page">
    <h1>友情链接</h1>
    <div v-if="loading" class="loading">
      <p>加载中...</p>
    </div>
    <div v-else class="link-list">
        <div v-for="link in links" :key="link.id" class="link-item">
            <a :href="formatLinkAddress(link.address)" target="_blank">
                <div class="link-content">
                    <div class="link-info">
                        <div class="link-name">{{ link.name }}</div>
                        <div class="link-desc">{{ link.description }}</div>
                    </div>
                    <div class="link-logo">
                        <img 
                            v-if="isValidLogo(link.logo)"
                            :src="link.logo" 
                            :alt="link.name"
                            @error="handleImageError(link)"
                            @load="handleImageLoad(link)"
                            referrerpolicy="no-referrer"
                        />
                        <div v-else class="default-logo">
                            {{ link.name.charAt(0).toUpperCase() }}
                        </div>
                    </div>
                </div>
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

// 格式化友链地址，确保是完整的URL
const formatLinkAddress = (address: string): string => {
  if (!address) return '#';
  
  // 如果已经是完整的URL（以http://或https://开头），直接返回
  if (address.startsWith('http://') || address.startsWith('https://')) {
    return address;
  }
  
  // 否则添加https://前缀
  return `https://${address}`;
};

// 检查logo是否有效
const isValidLogo = (logo: string | null): boolean => {
  return logo !== null && logo !== undefined && logo.trim() !== '';
};

// 处理图片加载错误
const handleImageError = (link: Link) => {
  console.error(`友链 "${link.name}" 的Logo加载失败:`, link.logo);
};

// 处理图片加载成功
const handleImageLoad = (link: Link) => {
  console.log(`友链 "${link.name}" 的Logo加载成功:`, link.logo);
};

// 加载友链列表
const loadLinks = async () => {
  try {
    loading.value = true;
    const linkList = await linkApi.getLinkList();
    links.value = linkList || [];
    // 添加调试信息
    console.log('友链数据:', links.value);
    links.value.forEach(link => {
      const logoStatus = isValidLogo(link.logo) ? '有效' : '无效或为空';
      console.log(`友链: ${link.name}, Logo: ${link.logo} (${logoStatus})`);
    });
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

.link-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 16px;
}

.link-info {
    flex: 1;
    min-width: 0; // 防止flex子元素溢出
}

.link-logo {
    flex-shrink: 0; // 防止logo被压缩
    
    img {
        width: 48px;
        height: 48px;
        border-radius: 6px;

        @media (max-width: 480px) {
          width: 40px;
          height: 40px;
        }
    }
    
    .default-logo {
        width: 48px;
        height: 48px;
        border-radius: 6px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        color: white;
        display: flex;
        align-items: center;
        justify-content: center;
        font-weight: bold;
        font-size: 18px;

        @media (max-width: 480px) {
          width: 40px;
          height: 40px;
          font-size: 16px;
        }
    }
}

.link-name {
    font-size: 1.2rem;
    font-weight: bold;
    color: $text-color;
    margin-bottom: 4px;

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