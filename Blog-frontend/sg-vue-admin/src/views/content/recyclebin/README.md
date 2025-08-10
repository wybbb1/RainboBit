# 回收站功能

## 功能概述
回收站功能用于管理被逻辑删除的内容，包括文章、标签和友链。管理员可以查看、恢复或永久删除这些内容。

## 功能特性
- **分类管理**: 通过Tab页分别管理文章、标签、友链回收站
- **搜索功能**: 支持按名称、时间范围等条件搜索
- **批量操作**: 支持批量恢复、批量永久删除
- **预览功能**: 文章和友链支持预览功能
- **清空回收站**: 一键清空所有已删除内容

## 文件结构
```
recyclebin/
├── index.vue                    # 主页面，包含Tab导航
├── components/
│   ├── ArticlesRecycle.vue      # 文章回收站组件
│   └── LinksRecycle.vue         # 友链回收站组件
└── README.md                    # 说明文档
```

## 路由配置
在 `src/router/index.js` 中已添加路由：
```javascript
{
  path: '/recyclebin',
  component: () => import('@/views/content/recyclebin/index'),
  hidden: true
}
```

## API接口
API接口文件位于 `src/api/content/recyclebin.js`，包含所有回收站相关的接口调用。

## 使用方法
1. 在菜单中添加回收站入口（需要后端菜单配置）
2. 访问 `/recyclebin` 路径即可使用回收站功能
3. 通过Tab切换不同类型的回收站
4. 使用搜索、批量操作等功能管理回收站内容

## 权限说明
- 需要相应的权限才能访问回收站
- 恢复和永久删除操作需要相应权限
- 清空回收站需要高级权限
