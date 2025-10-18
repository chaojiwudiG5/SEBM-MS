# ===== 第二阶段：运行镜像 =====
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制第一阶段生成的 Jar
ADD ./target/sebm-borrow-service-1.0-SNAPSHOT.jar ./sebm-borrow-service.jar

# 暴露端口
EXPOSE 8082

# 启动命令
ENTRYPOINT ["java", "-jar", "/app/sebm-borrow-service.jar", "--spring.profiles.active=prod"]
