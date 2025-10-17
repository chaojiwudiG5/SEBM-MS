# ===== 第一阶段：Maven 构建 Jar =====
FROM maven:3.9.2-eclipse-temurin-17 AS build

# 设置工作目录
WORKDIR /app

# 先复制父 POM 和子模块 POM
COPY ../pom.xml ./pom.xml
COPY pom.xml ./sebm-gateway-service/pom.xml

# 安装父 POM
RUN mvn install -f pom.xml -DskipTests

# 下载子模块依赖
RUN mvn dependency:go-offline -f ./sebm-gateway-service/pom.xml

# 复制源码
COPY ./src ./sebm-gateway-service/src

# 打包 Jar（跳过测试加快构建）
RUN mvn clean package -DskipTests -f ./sebm-gateway-service/pom.xml

# ===== 第二阶段：运行镜像 =====
FROM swr.cn-north-4.myhuaweicloud.com/ddn-k8s/docker.io/openjdk:17-jdk-alpine

# 设置工作目录
WORKDIR /app

# 复制第一阶段生成的 Jar
COPY --from=build /app/sebm-gateway-service/target/sebm-gateway-service-1.0-SNAPSHOT.jar ./sebm-gateway-service.jar

# 暴露端口
EXPOSE 8090

# 启动命令
ENTRYPOINT ["java", "-jar", "/app/sebm-gateway-service.jar", "--spring.profiles.active=prod"]
