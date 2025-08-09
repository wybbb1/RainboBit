<template>
  <header class="site-header">
    <div class="container">
      <div class="logo">
        <router-link to="/">RainBot</router-link>
      </div>
      <nav class="main-nav">
        <router-link to="/">首页</router-link>
        <router-link to="/categories">分类</router-link>
        <router-link to="/archives">归档</router-link>
        <router-link to="/links">友链</router-link>
        <router-link to="/about">关于</router-link>
      </nav>
      <div class="user-actions">
        <template v-if="userStore.isLoggedIn">
          <div class="user-info">
            <CustomSelect
              v-model="selectedUserAction"
              :options="userMenuOptions"
              :placeholder="userStore.userInfo?.user.nickName || '用户'"
              @change="handleUserMenuChange"
              class="user-menu-select"
            >
              <template #trigger>
                <div class="user-trigger-content">
                  <img 
                    v-if="userStore.userInfo?.user.avatar" 
                    :src="userStore.userInfo.user.avatar"
                    :alt="userStore.userInfo.user.nickName"
                    class="user-avatar"
                  />
                  <span v-else class="avatar-placeholder">
                    {{ userStore.userInfo?.user.nickName?.charAt(0)?.toUpperCase() || 'U' }}
                  </span>
                  <span class="welcome-text">{{ userStore.userInfo?.user.nickName || '用户' }}</span>
                  <span class="dropdown-arrow">▼</span>
                </div>
              </template>
            </CustomSelect>
          </div>
        </template>
        <template v-else>
          <div class="auth-links">
            <router-link to="/login" class="auth-link">登录</router-link>
            <router-link to="/register" class="auth-link">注册</router-link>
          </div>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import CustomSelect from './CustomSelect.vue'

const userStore = useUserStore()
const router = useRouter()

// 用户菜单选项
const userMenuOptions = computed(() => [
  { value: 'profile', label: '个人资料', icon: '' },
  { value: 'logout', label: '登出', icon: '' }
])

// 当前选中的菜单项（用于CustomSelect）
const selectedUserAction = ref('')

/**
 * 处理用户菜单选择
 */
const handleUserMenuChange = (value: string | number) => {
  switch (value) {
    case 'profile':
      router.push('/profile')
      break
    case 'logout':
      handleLogout()
      break
  }
  // 重置选择
  selectedUserAction.value = ''
}

/**
 * 处理登出
 */
const handleLogout = async () => {
  if (!confirm('确定要登出吗？')) {
    return
  }
  
  try {
    await userStore.logout()
    // 如果当前在需要登录的页面，跳转到首页
    if (router.currentRoute.value.meta.requiresAuth) {
      router.push('/')
    }
  } catch (error) {
    // 即使后端登出失败，也清除本地状态
    userStore.clearUserInfo()
    router.push('/')
  }
}
</script>

<style lang="scss" scoped>
@import '@/assets/styles/_variables.scss';

.site-header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: $header-height;
  background-color: rgba($background-color, 0.8);
  backdrop-filter: blur(10px);
  border-bottom: 1px solid $border-color;
  z-index: 1000;

  .container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    height: 100%;
  }

  .logo a {
    font-size: 1.5rem;
    font-weight: bold;
    color: $text-color;
  }

  .main-nav {
    display: flex;
    align-items: center;
    
    a {
      position: relative;
      margin-left: 25px;
      color: $text-color-secondary;
      transition: color $transition-speed;
      padding-bottom: 5px;

      @media (max-width: 600px) {
        margin-left: 15px;
        font-size: 0.9rem;
      }

      &:after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        width: 0;
        height: 2px;
        background-image: linear-gradient(to right, $primary-color, $secondary-color);
        transition: all $transition-speed ease-in-out;
        transform: translateX(-50%);
      }

      &:hover,
      &.router-link-exact-active {
        color: $text-color;
      }

      &:hover:after,
      &.router-link-exact-active:after {
        width: 100%;
      }
    }
  }

  .user-actions {
    display: flex;
    align-items: center;

    .user-info {
      .user-menu-select {
        min-width: 180px;

        // 重写CustomSelect的样式，使其与Header保持一致
        :deep(.select-trigger) {
          background: rgba($background-color, 0.8);
          border: none;
          backdrop-filter: blur(10px);
          
          &:hover {
            background: rgba($background-color, 0.9);
            border: none;
          }

          &.active {
            border: none;
            background: rgba($background-color, 0.9);
          }
        }

        .user-trigger-content {
          display: flex;
          align-items: center;
          gap: 10px;
          width: 100%;

          .user-avatar {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid rgba(255, 255, 255, 0.2);
            transition: border-color 0.3s ease;
          }

          .avatar-placeholder {
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: linear-gradient(135deg, $primary-color, $secondary-color);
            color: white;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 14px;
            font-weight: bold;
            box-shadow: 0 2px 8px rgba($primary-color, 0.3);
            flex-shrink: 0;
          }

          .welcome-text {
            color: $text-color;
            font-size: 0.9rem;
            font-weight: 500;
            transition: color 0.3s ease;
            flex: 1;
            text-align: left;
          }

          .dropdown-arrow {
            color: $text-color-secondary;
            font-size: 0.8rem;
            transition: all 0.3s ease;
            flex-shrink: 0;
          }
        }

        // 当CustomSelect处于活跃状态时的样式
        :deep(.select-trigger.active) {
          .user-trigger-content {
            .user-avatar {
              border-color: rgba($primary-color, 0.5);
            }
            
            .welcome-text {
              color: $primary-color;
            }
            
            .dropdown-arrow {
              color: $primary-color;
              transform: rotate(180deg);
            }
          }
        }

        // 悬停效果
        :deep(.select-trigger:hover) {
          .user-trigger-content {
            .user-avatar {
              border-color: rgba($primary-color, 0.5);
            }
            
            .welcome-text {
              color: $primary-color;
            }
            
            .dropdown-arrow {
              color: $primary-color;
            }
          }
        }

        // 调整下拉菜单的背景色，使其更接近Header的色调
        :deep(.select-dropdown) {
          background: rgba($background-color, 0.95);
          backdrop-filter: blur(15px);
          border: 1px solid rgba(255, 255, 255, 0.1);
        }
      }
    }

    .auth-links {
      display: flex;
      align-items: center;
      gap: 15px;

      .auth-link {
        padding: 6px 12px;
        color: $text-color-secondary;
        border: 1px solid $border-color;
        border-radius: 4px;
        transition: all $transition-speed;
        font-size: 0.85rem;
        text-decoration: none;

        &:hover {
          background-color: $primary-color;
          color: white;
          border-color: $primary-color;
        }

        &:last-child {
          background-color: $primary-color;
          color: white;
          border-color: $primary-color;

          &:hover {
            background-color: darken($primary-color, 10%);
          }
        }
      }
    }
  }

  @media (max-width: 768px) {
    .container {
      flex-wrap: wrap;
      padding: 0 15px;
    }

    .user-actions {
      .user-info .welcome-text {
        display: none;
      }
    }
  }
}
</style>
