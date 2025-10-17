# 基础镜像
FROM registry.cn-hangzhou.aliyuncs.com/openjdk/openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制 Jar 包
ADD target/sebm-maintenance-service-1.0-SNAPSHOT.jar .

# 暴露端口
EXPOSE 8084

# 启动命令
ENTRYPOINT ["java", "-jar", "/app/sebm-maintenance-service-1.0-SNAPSHOT.jar","--spring.profiles.active=prod"]
