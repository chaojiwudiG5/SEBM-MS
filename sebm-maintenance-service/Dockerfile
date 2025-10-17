# ===== 第一阶段：Maven 构建 Jar =====
FROM maven:3.9.2-eclipse-temurin-17 AS build

# 设置工作目录
WORKDIR /app

# 先复制 pom 文件，避免每次都下载依赖
COPY pom.xml .

# 下载依赖
RUN mvn dependency:go-offline

# 复制源码
COPY src ./src

# 打包 Jar（跳过测试加快构建）
RUN mvn clean package -DskipTests

# ===== 第二阶段：运行镜像 =====
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制第一阶段生成的 Jar
COPY --from=build /app/target/sebm-maintenance-service-1.0-SNAPSHOT.jar ./sebm-maintenance-service.jar

# 暴露端口
EXPOSE 8084

# 启动命令
ENTRYPOINT ["java", "-jar", "/app/sebm-maintenance-service.jar", "--spring.profiles.active=prod"]
