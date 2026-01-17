FROM openjdk:17-jdk-slim

LABEL maintainer="JSite Team"
LABEL description="JSite Admin Backend Service"

WORKDIR /app

# 复制jar包
COPY target/jsite-admin-1.0.0.jar app.jar

# JVM参数
ENV JAVA_OPTS="-Xms512m -Xmx1024m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE="prod"

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar --spring.profiles.active=$SPRING_PROFILES_ACTIVE"]
