# 使用 amazoncorretto:21 作为基础镜像
FROM amazoncorretto:21

# 安装 OpenJDK 21
RUN apk add --no-cache openjdk21-jre
    
# 设置环境变量
ENV JAVA_HOME=/usr/lib/jvm/java-21-openjdk
ENV PATH=$JAVA_HOME/bin:$PATH

# 创建应用目录
RUN mkdir -p /app
WORKDIR /app

# 复制应用 JAR 文件到容器
COPY RainBoBit-1.0.0.jar RainBoBit-1.0.0.jar

# 暴露端口
EXPOSE 7777

# 运行命令
ENTRYPOINT ["java","-jar","/app/RainBoBit-1.0.0.jar"]