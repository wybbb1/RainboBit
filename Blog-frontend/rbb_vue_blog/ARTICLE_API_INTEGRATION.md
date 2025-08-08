# 文章接口集成说明

## 概述

本文档说明了如何使用博客系统的文章接口，包括接口定义、类型声明和组件使用示例。

## 接口信息

### 基础接口
- **接口地址**: `http://localhost:7777/article/articleList`
- **请求方法**: GET
- **接口描述**: 获取文章列表

### 请求参数

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | number | 否 | 1 | 页码 |
| pageSize | number | 否 | 10 | 每页数量 |
| status | number | 否 | 0 | 状态（0:已发布, 1:草稿） |
| categoryId | number | 否 | - | 分类ID |
| tagId | number | 否 | - | 标签ID |

### 响应数据结构

```json
{
  "code": "200",
  "msg": "操作成功",
  "data": {
    "total": "3",
    "rows": [
      {
        "id": "1",
        "title": "SpringSecurity从入门到精通",
        "summary": "Spring Security+JWT实现项目级前端分离认证授权",
        "thumbnail": "https://sg-blog-oss.oss-cn-beijing.aliyuncs.com/2022/01/31/948597e164614902ab1662ba8452e106.png",
        "categoryId": "1",
        "categoryName": "java",
        "viewCount": "113",
        "createTime": "2022-01-24 07:20:11"
      }
    ]
  }
}
```

## 文件结构

### 1. 类型定义 (`src/types/index.ts`)

定义了 `Article` 接口，包含文章的所有字段：

```typescript
export interface Article {
  id: number | string;
  title: string;
  summary: string;
  thumbnail: string;
  categoryId: number | string;
  categoryName?: string;
  viewCount: number | string;
  createTime: string;
  // ... 其他字段
}
```

### 2. API 接口 (`src/api/article.ts`)

封装了文章相关的 API 调用：

```typescript
export const articleApi = {
  getArticleList(params: ArticleQuery): Promise<PageResult<Article>>,
  getArticleDetail(id: number | string): Promise<Article>,
  updateViewCount(id: number | string): Promise<void>,
  searchArticles(keyword: string, params: PageQuery): Promise<PageResult<Article>>
}
```

### 3. 配置文件 (`src/config/api.ts`)

包含 API 配置、状态码定义等：

```typescript
export const API_CONFIG = {
  development: {
    baseURL: 'http://localhost:7777',
    timeout: 10000,
  }
}
```

### 4. 请求工具 (`src/utils/request.ts`)

封装了 axios 请求，处理请求/响应拦截、错误处理等。

### 5. 业务 Hook (`src/composables/useArticleList.ts`)

封装了文章列表的业务逻辑，包括：
- 分页管理
- 状态管理
- 筛选功能
- 加载更多

### 6. 组件示例

#### 简单列表组件 (`src/views/ArticleListSimple.vue`)
- 网格布局
- 分页器
- 筛选功能
- 响应式设计

#### 详细列表组件 (`src/views/ArticleList.vue`)
- 卡片布局
- 图片懒加载
- 错误处理

## 使用示例

### 基础用法

```vue
<template>
  <div>
    <div v-for="article in articles" :key="article.id">
      <h3>{{ article.title }}</h3>
      <p>{{ article.summary }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { articleApi } from '@/api/article'

const articles = ref([])

const loadArticles = async () => {
  try {
    const result = await articleApi.getArticleList({
      page: 1,
      pageSize: 10,
      status: 0
    })
    articles.value = result.rows
  } catch (error) {
    console.error('加载失败:', error)
  }
}

onMounted(loadArticles)
</script>
```

### 使用 Hook

```vue
<script setup lang="ts">
import { useArticleList } from '@/composables/useArticleList'

const {
  articles,
  loading,
  total,
  fetchArticles,
  setCategory,
  setStatus
} = useArticleList()

// 组件挂载时自动加载
onMounted(fetchArticles)
</script>
```

## 测试方法

### 1. 浏览器测试

访问测试页面：`http://localhost:3000/api-test.html`

### 2. 命令行测试

```bash
# 获取文章列表
curl "http://localhost:7777/article/articleList?page=1&pageSize=10&status=0"

# 获取文章详情
curl "http://localhost:7777/article/1"

# 更新浏览量
curl -X PUT "http://localhost:7777/article/updateViewCount/1"
```

### 3. 开发者工具

1. 打开浏览器开发者工具
2. 切换到 Network 标签
3. 访问包含文章列表的页面
4. 查看 `articleList` 请求的详细信息

## 常见问题

### 1. CORS 跨域问题

如果遇到跨域问题，需要在后端配置 CORS 或在前端配置代理：

```typescript
// vite.config.ts
export default defineConfig({
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:7777',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
```

### 2. 数据类型不匹配

接口返回的某些字段可能是字符串类型（如 `id`, `viewCount`, `total`），在类型定义中已经兼容了字符串和数字类型。

### 3. 图片加载失败

组件中已包含图片错误处理逻辑，会显示默认占位图。

## 扩展功能

### 1. 无限滚动

```vue
<script setup lang="ts">
const { loadMore, hasMore } = useArticleList()

const handleScroll = () => {
  const { scrollTop, scrollHeight, clientHeight } = document.documentElement
  if (scrollTop + clientHeight >= scrollHeight - 100 && hasMore.value) {
    loadMore()
  }
}

onMounted(() => {
  window.addEventListener('scroll', handleScroll)
})
</script>
```

### 2. 搜索功能

```typescript
const searchArticles = async (keyword: string) => {
  const result = await articleApi.searchArticles(keyword, {
    page: 1,
    pageSize: 20
  })
  return result
}
```

### 3. 缓存策略

可以使用 Vue 的 `keep-alive` 或状态管理库来缓存文章列表数据。

## 性能优化

1. **懒加载图片**: 使用 `Intersection Observer` API
2. **虚拟滚动**: 对于大量数据使用虚拟滚动组件
3. **分页优化**: 合理设置页面大小
4. **缓存策略**: 缓存已加载的数据
5. **预加载**: 预加载下一页数据

## 总结

本集成方案提供了完整的文章接口调用解决方案，包括：

- ✅ 完整的 TypeScript 类型支持
- ✅ 统一的错误处理机制
- ✅ 可复用的业务逻辑 Hook
- ✅ 响应式组件示例
- ✅ 详细的测试方法
- ✅ 性能优化建议

可以根据实际项目需求进行定制和扩展。
