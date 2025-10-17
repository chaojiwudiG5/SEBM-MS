# 基础镜像
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制 Jar 包
ADD target/sebm-maintenance-service-1.0-SNAPSHOT.jar .

# 暴露端口
EXPOSE 8084

# 启动命令
ENTRYPOINT ["java", "-jar", "/app/sebm-maintenance-service-1.0-SNAPSHOT.jar","--spring.profiles.active=prod"]
