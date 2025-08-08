<template>
  <footer class="site-footer">
    <div class="container">
      <p>&copy; {{ new Date().getFullYear() }} RainBot. All rights reserved.</p>
    </div>
  </footer>
</template>

<script setup lang="ts">
</script>

<style lang="scss" scoped>
@import '@/assets/styles/_variables.scss';

.site-footer {
  height: $footer-height;
  background-color: $background-color;
  border-top: 1px solid $border-color;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: $text-color-secondary;
  font-size: 0.9rem;
}
</style>
