# RainboBit

RainboBit 是一个全栈个人博客系统，包含文章管理、分类标签、评论互动、友链管理等功能，提供前台博客展示与后台管理面板。

## 技术栈

### 后端

- **框架**: Spring Boot 3.5.3 / Java 21
- **安全**: Spring Security + JWT
- **数据库**: MySQL 8.0 + MyBatis-Plus 3.5.5
- **缓存**: Redis 7 (Lettuce)
- **搜索引擎**: Elasticsearch
- **文件存储**: R2 OSS (S3 兼容)
- **AI 集成**: LangChain4j (OpenAI / DeepSeek)
- **邮件**: SMTP (QQ 邮箱)
- **API 文档**: SpringDoc OpenAPI 3

### 前端

| | 管理后台 (sg-vue-admin) | 博客前台 (rbb_vue_blog) |
|---|---|---|
| **框架** | Vue 2.6 | Vue 3.5 |
| **UI 库** | Element UI 2.13 | Element Plus 2.10 |
| **状态管理** | Vuex 3 | Pinia 3 |
| **路由** | Vue Router 3 | Vue Router 4 |
| **构建工具** | Vue CLI | Vite 7 |

### 基础设施

- **容器化**: Docker + Docker Compose
- **反向代理**: Nginx
- **SSL**: 支持 HTTPS

## 功能特性

- **文章管理** — 支持 Markdown 编辑与渲染、代码高亮、文章置顶、草稿/发布状态、浏览量统计
- **分类与标签** — 层级分类体系、多标签关联
- **评论系统** — 支持嵌套回复
- **友链管理** — 含审批工作流
- **用户与权限** — 多角色 RBAC 权限控制、JWT 认证
- **文件管理** — OSS 云存储集成
- **数据看板** — 后台统计面板
- **AI 功能** — 基于 LLM 的智能特性
- **回收站** — 软删除内容恢复
- **邮件通知** — SMTP 邮件发送

## 项目结构

```
RainboBit/
├── Blog-backend/
│   └── RainBoBit/
│       ├── src/main/java/com/wybbb/rainbobit/
│       │   ├── controller/
│       │   │   ├── admin/          # 后台管理接口
│       │   │   └── blog/           # 前台博客接口
│       │   ├── service/            # 业务逻辑层
│       │   ├── mapper/             # 数据访问层 (MyBatis-Plus)
│       │   ├── common/
│       │   │   ├── config/         # 配置类 (MP, OSS, Security, OpenAPI)
│       │   │   ├── constants/      # 常量定义
│       │   │   ├── prop/           # 配置属性 (JWT, OSS)
│       │   │   └── utils/          # 工具类 (JWT 等)
│       │   └── pojo/
│       │       ├── dto/            # 数据传输对象
│       │       ├── entity/         # 数据库实体
│       │       └── vo/             # 视图对象
│       └── src/main/resources/
│           ├── application.yml     # 应用配置
│           └── sql/                # 数据库建表脚本
├── Blog-frontend/
│   ├── sg-vue-admin/              # 管理后台 (Vue 2)
│   └── rbb_vue_blog/              # 博客前台 (Vue 3)
├── docker-compose.yml
├── dockerfile
├── .env.example
└── README.md
```

## API 概览

### 前台接口 (Blog)

| 路径 | 说明 |
|---|---|
| `GET /article/articleList` | 文章列表 |
| `GET /article/{id}` | 文章详情 (含浏览量 +1) |
| `GET /category/*` | 分类浏览 |
| `GET /tag/*` | 标签浏览 |
| `POST /comment/*` | 评论与回复 |
| `GET /link/*` | 友链列表 |

### 后台接口 (Admin)

| 路径 | 说明 |
|---|---|
| `/content/article/*` | 文章增删改查 |
| `/content/category/*` | 分类管理 |
| `/content/tag/*` | 标签管理 |
| `/content/link/*` | 友链管理 (含审批) |
| `/system/user/*` | 用户管理 |
| `/system/role/*` | 角色管理 |
| `/system/menu/*` | 菜单管理 |
| `/file/*` | 文件上传 |

## 数据库表

| 表名 | 说明 |
|---|---|
| `rbb_article` | 文章 |
| `rbb_category` | 分类 (支持层级) |
| `rbb_tag` | 标签 |
| `rbb_article_tag` | 文章-标签关联 (多对多) |
| `rbb_comment` | 评论 (支持嵌套回复) |
| `rbb_link` | 友链 |
| `sys_user` | 用户 |
| `sys_role` | 角色 |
| `sys_menu` | 菜单 |
| `sys_role_menu` | 角色-菜单关联 |
| `sys_user_role` | 用户-角色关联 |

## 快速开始

### 环境要求

- Java 21+
- Node.js 16+
- Maven 3.8+
- Docker & Docker Compose

### 1. 配置环境变量

```bash
cp .env.example .env
```

编辑 `.env` 文件，填写以下配置：

```env
# 数据库
MYSQL_ROOT_PASSWORD=<your_password>
MYSQL_DATABASE=rainbobit
MYSQL_PORT=3306

# Redis
REDIS_PASSWORD=<your_password>
REDIS_PORT=6379

# 后端端口
BACKEND_PORT=7777

# 前端端口
FRONTEND_BLOG_PORT=3000
FRONTEND_ADMIN_PORT=3001

# 邮箱
SENDER_EMAIL=<your_email>
SENDER_PASSWORD=<your_smtp_password>

# OSS 对象存储
OSS_ENDPOINT=<your_endpoint>
OSS_ACCESS_KEY_ID=<your_key>
OSS_ACCESS_KEY_SECRET=<your_secret>
OSS_BUCKET_NAME=<your_bucket>
OSS_DOMAIN=<your_domain>
OSS_REGION=<your_region>

# JWT
JWT_SECRET_KEY=<your_secret_key>
```

### 2. Docker 一键部署

```bash
docker compose up -d
```

服务启动后：
- 博客前台: `http://localhost:3000`
- 管理后台: `http://localhost:3001`
- 后端 API: `http://localhost:7777`

### 3. 本地开发

**后端:**

```bash
cd Blog-backend/RainBoBit
mvn spring-boot:run
```

**管理后台:**

```bash
cd Blog-frontend/sg-vue-admin
npm install
npm run dev
```

**博客前台:**

```bash
cd Blog-frontend/rbb_vue_blog
npm install
npm run dev
```

## Docker 部署结构

```
┌──────────────────────────────────────────────────┐
│                    Nginx                          │
│         (反向代理 + SSL + 静态资源)                │
├────────────┬────────────┬────────────────────────┤
│  :3000     │  :3001     │  :7777                 │
│  博客前台   │  管理后台   │  Spring Boot API       │
├────────────┴────────────┴────────────────────────┤
│            MySQL 8.0    │    Redis 7              │
└──────────────────────────────────────────────────┘
```

## License

本项目仅供学习交流使用。
