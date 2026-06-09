# ==========================================
# Giai đoạn 1: Build (Tạo file Fat JAR)
# ==========================================
FROM maven:3.9.4-eclipse-temurin-17-alpine AS builder
WORKDIR /app

# Copy pom.xml và tải thư viện trước để tận dụng cache
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code và build (bỏ qua test để build nhanh)
COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

# ==========================================
# Giai đoạn 2: Runtime (Chạy ứng dụng)
# ==========================================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Giả sử module Order chạy ở port 8083 (để không trùng Voucher 8082, Restaurant 8084)
EXPOSE 8083

# Copy toàn bộ file .jar từ thư mục target
COPY --from=builder /app/target/*.jar ./

# Xóa bỏ file bị thiếu thư viện (.original) và đổi tên file Fat JAR duy nhất còn lại thành app.jar
RUN find . -name "*.original" -type f -delete && \
    mv *.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]