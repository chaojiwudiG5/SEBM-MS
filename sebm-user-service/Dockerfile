# ===== 第二阶段：运行镜像 =====
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:17-jdk-alpine

WORKDIR /app

# 复制目标子模块的 Jar
ADD ./target/sebm-user-service-1.0-SNAPSHOT.jar ./sebm-user-service.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/sebm-user-service.jar", "--spring.profiles.active=prod"]
