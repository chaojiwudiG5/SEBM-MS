# 基础镜像
# 国内服务器会被墙
#FROM openjdk:17-jdk-alpine
FROM registry.cn-hangzhou.aliyuncs.com/openjdk/openjdk:17-jdk-alpine


# 设置工作目录
WORKDIR /app

# 复制 Jar 包
ADD target/sebm-user-service-1.0-SNAPSHOT.jar .

# 暴露端口
EXPOSE 8081

# 启动命令
ENTRYPOINT ["java", "-jar", "/app/sebm-user-service-1.0-SNAPSHOT.jar","--spring.profiles.active=prod"]
