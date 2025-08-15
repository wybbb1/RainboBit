# 使用多阶段构建
# 第一阶段：构建应用
FROM node:20-alpine AS builder

# 设置工作目录
WORKDIR /app

# 复制package文件
COPY package*.json ./

# 安装依赖
RUN npm ci --only=production

# 复制源代码
COPY . .

# 构建应用
ARG VITE_API_BASE_URL=http://localhost:7777
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL

RUN npm run build

# 第二阶段：Nginx服务
FROM nginx:alpine

# 复制构建的静态文件
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制nginx配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

# 暴露端口
EXPOSE 80

# 启动nginx
CMD ["nginx", "-g", "daemon off;"]
