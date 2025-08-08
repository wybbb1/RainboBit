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
    </div>
  </header>
</template>

<script setup lang="ts">
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

  .main-nav a {
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
</style>
