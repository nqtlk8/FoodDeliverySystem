# ==========================================
# Giai đoạn 1: Build (Tạo file Fat JAR)
# ==========================================
FROM maven:3.9.4-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy pom.xml và tải thư viện trước để tận dụng cache của Docker
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code và build
COPY src ./src
# Build file JAR. Nhờ cấu hình finalName, file tạo ra luôn là voucher-service.jar
RUN mvn clean package -Dmaven.test.skip=true

# ==========================================
# Giai đoạn 2: Runtime (Chạy ứng dụng)
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

EXPOSE 8080

# CHỈ COPY ĐÚNG FILE FAT JAR (Đã chứa đủ thư viện Jackson, Kafka...)
COPY --from=builder /app/target/voucher-service.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]