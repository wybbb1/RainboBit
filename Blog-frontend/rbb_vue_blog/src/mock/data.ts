// src/mock/data.ts
import type { Article, Tag, Link, Category } from '@/types';

export const categories: Category[] = [
  {
    id: 1,
    name: '前端开发',
    pid: -1,
    description: '前端技术相关文章',
    status: '0',
    createBy: 1,
    createTime: '2025-07-01T00:00:00Z',
    updateBy: 1,
    updateTime: '2025-07-01T00:00:00Z',
    delFlag: 0,
    refer_cnt: 2
  },
  {
    id: 2,
    name: '后端开发',
    pid: -1,
    description: '后端技术相关文章',
    status: '0',
    createBy: 1,
    createTime: '2025-07-01T00:00:00Z',
    updateBy: 1,
    updateTime: '2025-07-01T00:00:00Z',
    delFlag: 0,
    refer_cnt: 1
  }
];

export const tags: Tag[] = [
  {
    id: 1,
    name: 'Vue',
    createBy: 1,
    createTime: '2025-07-01T00:00:00Z',
    updateBy: 1,
    updateTime: '2025-07-01T00:00:00Z',
    delFlag: 0,
    remark: 'Vue.js 框架相关标签'
  },
  {
    id: 2,
    name: 'TypeScript',
    createBy: 1,
    createTime: '2025-07-01T00:00:00Z',
    updateBy: 1,
    updateTime: '2025-07-01T00:00:00Z',
    delFlag: 0,
    remark: 'TypeScript 相关标签'
  },
  {
    id: 3,
    name: 'JavaScript',
    createBy: 1,
    createTime: '2025-07-01T00:00:00Z',
    updateBy: 1,
    updateTime: '2025-07-01T00:00:00Z',
    delFlag: 0,
    remark: 'JavaScript 相关标签'
  },
  {
    id: 4,
    name: 'Node.js',
    createBy: 1,
    createTime: '2025-07-01T00:00:00Z',
    updateBy: 1,
    updateTime: '2025-07-01T00:00:00Z',
    delFlag: 0,
    remark: 'Node.js 相关标签'
  }
];

export const articles: Article[] = [
  {
    id: 1,
    title: '深入理解 Vue 3 Composition API',
    content: '## 深入理解 Vue 3 Composition API\n\nComposition API 是 Vue 3 的核心特性之一，它提供了一种更灵活、可组合的方式来组织组件逻辑。与 Options API 不同，Composition API 允许我们将相关的逻辑组织在一起，而不是按选项（data, methods, computed）分散开。\n\n### 主要优势\n\n- **更好的逻辑复用**: 轻松地将逻辑提取到可复用的组合函数中。\n- **更灵活的代码组织**: 将功能相关的代码放在一起，提高可读性和可维护性。\n- **更好的类型推断**: 对 TypeScript 更友好，提供更完善的类型推断。',
    summary: 'Composition API 是 Vue 3 的核心特性之一，它提供了一种更灵活、可组合的方式来组织组件逻辑...',
    categoryId: 1,
    tagIds: [1],
    thumbnail: '',
    isTop: '0',
    status: '0',
    viewCount: 156,
    isComment: '1',
    createBy: 1,
    createTime: '2025-08-06T08:00:00Z',
    updateBy: 1,
    updateTime: '2025-08-06T08:00:00Z',
    delFlag: 0
  },
  {
    id: 2,
    title: '使用 TypeScript 构建健壮的应用',
    content: '## 使用 TypeScript 构建健壮的应用\n\nTypeScript 作为 JavaScript 的超集，为我们带来了静态类型检查，从而在开发阶段就能发现潜在的错误。这对于构建大型、复杂的应用至关重要。\n\n### 核心概念\n\n- **类型注解**: `let name: string = "Tom";`\n- **接口**: `interface Person { name: string; age: number; }`\n- **泛型**: `function identity<T>(arg: T): T { return arg; }`',
    summary: 'TypeScript 作为 JavaScript 的超集，为我们带来了静态类型检查，从而在开发阶段就能发现潜在的错误...',
    categoryId: 1,
    tagIds: [2],
    thumbnail: '',
    isTop: '0',
    status: '0',
    viewCount: 98,
    isComment: '1',
    createBy: 1,
    createTime: '2025-08-05T10:30:00Z',
    updateBy: 1,
    updateTime: '2025-08-05T10:30:00Z',
    delFlag: 0
  },
  {
    id: 3,
    title: 'Node.js 异步编程实践',
    content: '## Node.js 异步编程实践\n\nNode.js 的非阻塞 I/O 和事件驱动模型使其在处理高并发场景时表现出色。本文将探讨几种常见的异步编程模式。\n\n- **回调函数**\n- **Promise**\n- **Async/Await**',
    summary: 'Node.js 的非阻塞 I/O 和事件驱动模型使其在处理高并发场景时表现出色。本文将探讨几种常见的异步编程模式...',
    categoryId: 2,
    tagIds: [3],
    thumbnail: '',
    isTop: '0',
    status: '0',
    viewCount: 67,
    isComment: '1',
    createBy: 1,
    createTime: '2025-08-04T14:15:00Z',
    updateBy: 1,
    updateTime: '2025-08-04T14:15:00Z',
    delFlag: 0
  }
];

export const links: Link[] = [
  {
    id: 1,
    name: 'Vue.js',
    logo: '',
    description: '渐进式 JavaScript 框架',
    address: 'https://vuejs.org/',
    status: '0',
    createBy: 1,
    createTime: '2025-07-01T00:00:00Z',
    updateBy: 1,
    updateTime: '2025-07-01T00:00:00Z',
    delFlag: 0
  },
  {
    id: 2,
    name: 'TypeScript',
    logo: '',
    description: 'JavaScript 的超集',
    address: 'https://www.typescriptlang.org/',
    status: '0',
    createBy: 1,
    createTime: '2025-07-01T00:00:00Z',
    updateBy: 1,
    updateTime: '2025-07-01T00:00:00Z',
    delFlag: 0
  },
  {
    id: 3,
    name: 'Vite',
    logo: '',
    description: '下一代前端开发与构建工具',
    address: 'https://vitejs.dev/',
    status: '0',
    createBy: 1,
    createTime: '2025-07-01T00:00:00Z',
    updateBy: 1,
    updateTime: '2025-07-01T00:00:00Z',
    delFlag: 0
  }
];

// 为了向后兼容，创建一个获取文章标签的函数
export const getArticleTags = (articleId: number): Tag[] => {
  const tagMapping: { [key: number]: number[] } = {
    1: [1, 2], // Vue, TypeScript
    2: [2, 3], // TypeScript, JavaScript
    3: [4]     // Node.js
  };
  
  const tagIds = tagMapping[articleId] || [];
  return tags.filter(tag => tagIds.includes(Number(tag.id)));
};

// 获取分类名称的函数
export const getCategoryName = (categoryId: number): string => {
  const category = categories.find(c => c.id === categoryId);
  return category?.name || '未分类';
};
