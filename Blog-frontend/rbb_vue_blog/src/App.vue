<template>
  <div id="app">
    <Header />
    <main class="container">
      <router-view />
    </main>
    <Footer />
  </div>
</template>

<script setup lang="ts">
import Header from './components/Header.vue';
import Footer from './components/Footer.vue';
</script>

<style lang="scss">
@import '@/assets/styles/_variables.scss';
@import '@/assets/styles/main.scss';

#app {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

main.container {
  flex: 1;
  width: 100%;
  padding-top: $content-distance;
  padding-bottom: $footer-height;
}
</style>
