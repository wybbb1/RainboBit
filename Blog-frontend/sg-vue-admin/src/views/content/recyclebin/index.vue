<template>
  <div class="app-container">
    <!-- 顶部导航标签 -->
    <el-tabs v-model="activeTab" @tab-click="handleTabClick">
      <el-tab-pane label="文章回收站" name="articles">
        <articles-recycle />
      </el-tab-pane>
      <el-tab-pane label="友链回收站" name="links">
        <links-recycle />
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import ArticlesRecycle from './components/ArticlesRecycle'
import LinksRecycle from './components/LinksRecycle'

export default {
  name: 'ContentRecycleBin',
  components: {
    ArticlesRecycle,
    LinksRecycle
  },
  data() {
    return {
      activeTab: 'articles'
    }
  },
  methods: {
    handleTabClick(tab) {
      this.activeTab = tab.name
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
}
</style>
