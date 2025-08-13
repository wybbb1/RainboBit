<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <div class="header-left">
        <h2>数据监控台</h2>
        <p>实时监控博客数据统计</p>
      </div>
      <div class="header-right">
        <el-button 
          type="primary" 
          icon="el-icon-refresh" 
          @click="refreshData"
          :loading="loading.statistics"
        >
          刷新数据
        </el-button>
      </div>
    </div>
    
    <!-- 数据卡片 -->
    <div class="data-cards" v-loading="loading.statistics">
      <div class="data-card">
        <div class="card-icon article-icon">📝</div>
        <div class="card-content">
          <h3>{{ statistics.articleCount || 0 }}</h3>
          <p>文章总数</p>
          <span class="trend up" v-if="statistics.newArticles > 0">+{{ statistics.newArticles }} 本月新增</span>
          <span class="trend" v-else>{{ statistics.newArticles || 0 }} 本月新增</span>
        </div>
      </div>
      
      <div class="data-card">
        <div class="card-icon category-icon">📁</div>
        <div class="card-content">
          <h3>{{ statistics.categoryCount || 0 }}</h3>
          <p>分类数量</p>
          <span class="trend">共{{ statistics.tagCount || 0 }}个标签</span>
        </div>
      </div>
      
      <div class="data-card">
        <div class="card-icon view-icon">👁️</div>
        <div class="card-content">
          <h3>{{ statistics.totalViews || 0 }}</h3>
          <p>总浏览量</p>
          <span class="trend up" v-if="statistics.todayViews > 0">+{{ statistics.todayViews }} 今日浏览</span>
          <span class="trend" v-else>{{ statistics.todayViews || 0 }} 今日浏览</span>
        </div>
      </div>
      
      <div class="data-card">
        <div class="card-icon comment-icon">💬</div>
        <div class="card-content">
          <h3>{{ statistics.commentCount || 0 }}</h3>
          <p>评论总数</p>
          <span class="trend up" v-if="statistics.newComments > 0">+{{ statistics.newComments }} 新评论</span>
          <span class="trend" v-else>{{ statistics.newComments || 0 }} 新评论</span>
        </div>
      </div>
    </div>

    <!-- 图表区域 -->
    <div class="chart-section">
      <div class="chart-container">
        <h3>文章分类分布</h3>
        <div class="category-list" v-loading="loading.category">
          <div v-for="category in categoryData" :key="category.id" class="category-item">
            <span class="category-name">{{ category.name }}</span>
            <div class="category-bar">
              <div class="bar-fill" :style="{ width: category.percentage + '%' }"></div>
            </div>
            <span class="category-count">{{ category.count }}篇</span>
          </div>
          <div v-if="categoryData.length === 0" class="empty-data">
            <i class="el-icon-folder"></i>
            <p>暂无分类数据</p>
          </div>
        </div>
      </div>
      
      <div class="chart-container">
        <h3>标签分布统计</h3>
        <div class="tag-list" v-loading="loading.tags">
          <div v-for="tag in tagData" :key="tag.id" class="tag-item">
            <span class="tag-name">{{ tag.name }}</span>
            <div class="tag-bar">
              <div class="bar-fill" :style="{ width: tag.percentage + '%' }"></div>
            </div>
            <span class="tag-count">{{ tag.count }}次</span>
          </div>
          <div v-if="tagData.length === 0" class="empty-data">
            <i class="el-icon-price-tag"></i>
            <p>暂无标签数据</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 最新数据 -->
    <div class="recent-data">
      <div class="recent-articles" v-loading="loading.articles">
        <h3>最新文章</h3>
        <ul v-if="recentArticles.length > 0">
          <li v-for="article in recentArticles" :key="article.id">
            <div class="article-info">
              <span class="article-title" :title="article.title">{{ article.title }}</span>
              <div class="article-meta">
                <span class="article-views">{{ article.viewCount || 0 }}次浏览</span>
                <span class="article-date">{{ formatDate(article.createTime) }}</span>
              </div>
            </div>
          </li>
        </ul>
        <div v-else class="empty-data">
          <i class="el-icon-document"></i>
          <p>暂无文章数据</p>
        </div>
      </div>
      
      <div class="recent-comments" v-loading="loading.comments">
        <h3>最新评论</h3>
        <ul v-if="recentComments.length > 0">
          <li v-for="comment in recentComments" :key="comment.id">
            <div class="comment-info">
              <span class="comment-user">{{ comment.username }}</span>
              <span class="comment-content" :title="comment.content">{{ comment.content }}</span>
              <span class="comment-article">文章ID: {{ comment.articleId }}</span>
            </div>
          </li>
        </ul>
        <div v-else class="empty-data">
          <i class="el-icon-chat-line-square"></i>
          <p>暂无评论数据</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import {
  getDashboardStatistics,
  getRecentArticles,
  getRecentComments,
  getArticleList,
  getCategoryList,
  getTagList
} from '@/api/dashboard'

export default {
  name: 'Dashboard',
  data() {
    return {
      loading: {
        statistics: false,
        category: false,
        tags: false,
        articles: false,
        comments: false
      },
      statistics: {
        articleCount: 0,
        categoryCount: 0,
        tagCount: 0,
        totalViews: 0,
        todayViews: 0,
        commentCount: 0,
        newArticles: 0,
        newComments: 0
      },
      categoryData: [],
      tagData: [],
      recentArticles: [],
      recentComments: []
    }
  },
  computed: {
    ...mapGetters([
      'name'
    ])
  },
  mounted() {
    this.loadDashboardData()
  },
  methods: {
    // 加载仪表盘数据
    async loadDashboardData() {
      try {
        // 并行加载各种数据
        await Promise.all([
          this.loadStatistics(),
          this.loadRecentArticles(),
          this.loadRecentComments(),
          this.loadCategoryData(),
          this.loadTagData()
        ])
      } catch (error) {
        this.$message.error('加载仪表盘数据失败')
        console.error('Dashboard data loading error:', error)
      }
    },

    // 加载统计数据
    async loadStatistics() {
      this.loading.statistics = true
      try {
        console.log('开始加载统计数据...')
        const data = await getDashboardStatistics()
        console.log('统计数据响应:', data)
        
        // 响应拦截器已经返回了 res.data.data，所以这里直接是统计数据对象
        this.statistics = {
          articleCount: data.articleCount || 0,
          categoryCount: data.categoryCount || 0,
          tagCount: data.tagCount || 0,
          totalViews: data.totalViews || 0,
          todayViews: data.todayViews || 0,
          commentCount: data.commentCount || 0,
          newArticles: data.newArticles || 0,
          newComments: data.newComments || 0
        }
        console.log('统计数据加载成功:', this.statistics)
      } catch (error) {
        console.error('加载统计数据失败:', error)
        this.$message.error('加载统计数据失败')
        // 使用默认值确保界面正常显示
        this.statistics = {
          articleCount: 0,
          categoryCount: 0,
          tagCount: 0,
          totalViews: 0,
          todayViews: 0,
          commentCount: 0,
          newArticles: 0,
          newComments: 0
        }
      } finally {
        this.loading.statistics = false
      }
    },

    // 加载图表数据（使用模拟数据）
    async loadChartData() {
      this.loading.chart = true
      try {
        // 使用模拟数据
        this.chartData = [120, 190, 300, 500, 200, 300, 450]
        this.chartLabels = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
        
        // 延迟一点时间确保DOM已渲染
        this.$nextTick(() => {
          this.initChart()
        })
      } catch (error) {
        console.error('加载图表数据失败:', error)
      } finally {
        this.loading.chart = false
      }
    },

    // 加载最新文章
    async loadRecentArticles() {
      this.loading.articles = true
      try {
        const articles = await getRecentArticles(5)
        console.log('最新文章响应:', articles)
        
        // 响应拦截器已经返回了 res.data.data，所以这里直接是文章数组
        if (Array.isArray(articles)) {
          this.recentArticles = articles.map(article => ({
            id: article.id,
            title: article.title,
            summary: article.summary || '',
            thumbnail: article.thumbnail || '',
            categoryId: article.categoryId,
            tagIds: article.tagIds || [],
            viewCount: article.viewCount || 0,
            createTime: article.createTime
          }))
          console.log('最新文章加载成功:', this.recentArticles)
        } else {
          console.warn('最新文章数据不是数组格式:', articles)
          this.recentArticles = []
        }
      } catch (error) {
        console.error('加载最新文章失败:', error)
        this.$message.error('加载最新文章失败')
        this.recentArticles = []
      } finally {
        this.loading.articles = false
      }
    },

    // 加载最新评论
    async loadRecentComments() {
      this.loading.comments = true
      try {
        const comments = await getRecentComments(5)
        console.log('最新评论响应:', comments)
        
        // 响应拦截器已经返回了 res.data.data，所以这里直接是评论数组
        if (Array.isArray(comments)) {
          this.recentComments = comments.map(comment => ({
            id: comment.id,
            articleId: comment.articleId,
            content: comment.content || '暂无内容',
            username: comment.username || '匿名用户'
          }))
          console.log('最新评论加载成功:', this.recentComments)
        } else {
          console.warn('最新评论数据不是数组格式:', comments)
          this.recentComments = []
        }
      } catch (error) {
        console.error('加载最新评论失败:', error)
        this.$message.error('加载最新评论失败')
        this.recentComments = []
      } finally {
        this.loading.comments = false
      }
    },

    // 加载分类数据（使用真实接口数据）
    async loadCategoryData() {
      this.loading.category = true
      try {
        // 并行获取分类列表和文章列表
        const [categories, articles] = await Promise.all([
          getCategoryList(),
          getArticleList()
        ])
        
        console.log('分类列表响应:', categories)
        console.log('文章列表响应:', articles)
        
        if (Array.isArray(categories) && articles) {
          // 统计每个分类下的文章数量
          const categoryStats = {}
          // 处理不同的文章数据格式
          const articleList = Array.isArray(articles) ? articles : (articles.rows || articles.records || [])
          
          console.log('处理后的文章列表:', articleList)
          
          // 初始化分类统计
          categories.forEach(category => {
            categoryStats[category.id] = {
              id: category.id,
              name: category.name,
              count: 0
            }
          })
          
          // 统计文章数量
          articleList.forEach(article => {
            if (article.categoryId && categoryStats[article.categoryId]) {
              categoryStats[article.categoryId].count++
            }
          })
          
          // 计算百分比并生成最终数据
          const totalArticles = articleList.length
          this.categoryData = Object.values(categoryStats)
            .filter(category => category.count > 0) // 只显示有文章的分类
            .map(category => ({
              ...category,
              percentage: totalArticles > 0 ? Math.round((category.count / totalArticles) * 100) : 0
            }))
            .sort((a, b) => b.count - a.count) // 按文章数量降序排列
          
          console.log('分类数据加载成功:', this.categoryData)
        } else {
          console.warn('分类或文章数据格式不正确')
          this.categoryData = []
        }
      } catch (error) {
        console.error('加载分类数据失败:', error)
        this.$message.error('加载分类数据失败')
        this.categoryData = []
      } finally {
        this.loading.category = false
      }
    },

    // 加载标签数据（使用真实接口数据）
    async loadTagData() {
      this.loading.tags = true
      try {
        // 并行获取标签列表和文章列表
        const [tags, articles] = await Promise.all([
          getTagList(),
          getArticleList()
        ])
        
        console.log('标签列表响应:', tags)
        console.log('文章列表响应（标签统计）:', articles)
        
        if (Array.isArray(tags) && articles) {
          // 统计每个标签的使用次数
          const tagStats = {}
          // 处理不同的文章数据格式
          const articleList = Array.isArray(articles) ? articles : (articles.rows || articles.records || [])
          
          // 初始化标签统计
          tags.forEach(tag => {
            tagStats[tag.id] = {
              id: tag.id,
              name: tag.name,
              count: 0
            }
          })
          
          // 统计标签使用次数
          articleList.forEach(article => {
            if (article.tagIds && Array.isArray(article.tagIds)) {
              article.tagIds.forEach(tagId => {
                if (tagStats[tagId]) {
                  tagStats[tagId].count++
                }
              })
            }
          })
          
          // 计算百分比并生成最终数据
          const maxCount = Math.max(...Object.values(tagStats).map(tag => tag.count))
          this.tagData = Object.values(tagStats)
            .filter(tag => tag.count > 0) // 只显示被使用的标签
            .map(tag => ({
              ...tag,
              percentage: maxCount > 0 ? Math.round((tag.count / maxCount) * 100) : 0
            }))
            .sort((a, b) => b.count - a.count) // 按使用次数降序排列
            .slice(0, 10) // 只显示前10个最常用的标签
          
          console.log('标签数据加载成功:', this.tagData)
        } else {
          console.warn('标签或文章数据格式不正确')
          this.tagData = []
        }
      } catch (error) {
        console.error('加载标签数据失败:', error)
        this.$message.error('加载标签数据失败')
        this.tagData = []
      } finally {
        this.loading.tags = false
      }
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) return '刚刚' // 1分钟内
      if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前` // 1小时内
      if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前` // 1天内
      
      return date.toLocaleDateString('zh-CN')
    },

    // 刷新数据
    refreshData() {
      console.log('刷新数据被触发')
      this.loadDashboardData()
    }
  }
}
</script>

<style lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
  }
  
  &-header {
    margin-bottom: 30px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .header-left {
      h2 {
        margin: 0 0 5px 0;
        color: #303133;
        font-size: 24px;
      }
      
      p {
        margin: 0;
        color: #909399;
        font-size: 14px;
      }
    }
    
    .header-right {
      .el-button {
        .el-icon-refresh {
          margin-right: 5px;
        }
      }
    }
  }
}

.data-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.data-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  transition: transform 0.2s;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  }
  
  .card-icon {
    font-size: 32px;
    margin-right: 15px;
    width: 50px;
    height: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    
    &.article-icon {
      background: rgba(64, 158, 255, 0.1);
    }
    
    &.category-icon {
      background: rgba(103, 194, 58, 0.1);
    }
    
    &.view-icon {
      background: rgba(255, 193, 7, 0.1);
    }
    
    &.comment-icon {
      background: rgba(245, 108, 108, 0.1);
    }
  }
  
  .card-content {
    h3 {
      margin: 0 0 5px 0;
      font-size: 28px;
      font-weight: bold;
      color: #303133;
    }
    
    p {
      margin: 0 0 5px 0;
      color: #909399;
      font-size: 14px;
    }
    
    .trend {
      font-size: 12px;
      color: #909399;
      
      &.up {
        color: #67c23a;
      }
    }
  }
}

.chart-section {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}

.chart-container {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  
  h3 {
    margin: 0 0 20px 0;
    color: #303133;
    font-size: 16px;
  }
}

.category-list {
  .category-item {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    
    .category-name {
      width: 80px;
      font-size: 12px;
      color: #606266;
    }
    
    .category-bar {
      flex: 1;
      height: 8px;
      background: #f5f7fa;
      border-radius: 4px;
      margin: 0 10px;
      overflow: hidden;
      
      .bar-fill {
        height: 100%;
        background: linear-gradient(90deg, #409eff, #67c23a);
        transition: width 0.3s;
      }
    }
    
    .category-count {
      font-size: 12px;
      color: #909399;
      min-width: 30px;
      text-align: right;
    }
  }
}

.tag-list {
  .tag-item {
    display: flex;
    align-items: center;
    margin-bottom: 15px;
    
    .tag-name {
      width: 80px;
      font-size: 12px;
      color: #606266;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
    
    .tag-bar {
      flex: 1;
      height: 8px;
      background: #f5f7fa;
      border-radius: 4px;
      margin: 0 10px;
      overflow: hidden;
      
      .bar-fill {
        height: 100%;
        background: linear-gradient(90deg, #e6a23c, #f56c6c);
        transition: width 0.3s;
      }
    }
    
    .tag-count {
      font-size: 12px;
      color: #909399;
      min-width: 30px;
      text-align: right;
    }
  }
}

.recent-data {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.recent-articles, .recent-comments {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  
  h3 {
    margin: 0 0 15px 0;
    color: #303133;
    font-size: 16px;
  }
  
  ul {
    list-style: none;
    padding: 0;
    margin: 0;
  }
  
  li {
    padding: 10px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
  }
}

.recent-articles {
  li {
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .article-info {
      .article-title {
        color: #303133;
        font-size: 14px;
        font-weight: 500;
        display: block;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        cursor: pointer;
        
        &:hover {
          color: #409eff;
        }
      }
      
      .article-meta {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-size: 12px;
        color: #909399;
        
        .article-views {
          background: #f0f9ff;
          color: #409eff;
          padding: 2px 8px;
          border-radius: 12px;
          font-size: 11px;
        }
        
        .article-date {
          color: #909399;
        }
      }
    }
  }
}

.recent-comments {
  li {
    padding: 15px 0;
    border-bottom: 1px solid #f0f0f0;
    
    &:last-child {
      border-bottom: none;
    }
    
    .comment-info {
      .comment-user {
        color: #409eff;
        font-size: 12px;
        font-weight: bold;
        display: block;
        margin-bottom: 8px;
      }
      
      .comment-content {
        color: #606266;
        font-size: 14px;
        display: block;
        margin-bottom: 8px;
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        line-height: 1.4;
      }
      
      .comment-article {
        color: #909399;
        font-size: 11px;
        background: #f5f7fa;
        padding: 2px 6px;
        border-radius: 10px;
        display: inline-block;
      }
    }
  }
}

.empty-data {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
  
  i {
    font-size: 48px;
    margin-bottom: 15px;
    display: block;
    color: #dcdfe6;
  }
  
  p {
    margin: 0;
    font-size: 14px;
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
    
    .header-right {
      align-self: flex-end;
    }
  }
  
  .chart-section {
    grid-template-columns: 1fr;
  }
  
  .recent-data {
    grid-template-columns: 1fr;
  }
  
  .data-cards {
    grid-template-columns: 1fr;
  }
}
</style>
