# 基础镜像
FROM openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制 Jar 包
COPY target/sebm-notification-service-1.0-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8085

# 启动命令
ENTRYPOINT ["java", "-jar", "app.jar"]

