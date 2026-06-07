# ==========================================
# GIAI ĐOẠN 1: BUILD (Đóng gói ứng dụng)
# ==========================================
# Sử dụng image Maven chính thức chứa sẵn Java 17
FROM maven:3.9.6-eclipse-temurin-17 AS builder

# Thiết lập thư mục làm việc bên trong container
WORKDIR /app

# Copy file pom.xml vào trước để tải dependencies
# Việc này giúp tận dụng Docker Cache, nếu code thay đổi nhưng pom.xml không đổi thì không cần tải lại thư viện
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy toàn bộ source code vào container
COPY src ./src

# Build project thành file .jar, bỏ qua bước chạy Test để tăng tốc độ build
RUN mvn clean package -DskipTests

# ==========================================
# GIAI ĐOẠN 2: RUN (Chạy ứng dụng)
# ==========================================
# Sử dụng image JRE siêu nhẹ (Alpine) dành cho Java 17 để chạy file jar
FROM eclipse-temurin:17-jre-alpine

# Thiết lập thư mục làm việc
WORKDIR /app

# Copy file .jar đã được build thành công từ Giai đoạn 1 (builder) sang Giai đoạn 2
# Tên file jar thực tế trong thư mục target có thể khác, ta dùng *.jar để lấy tự động và đổi tên thành gateway.jar
COPY --from=builder /app/target/*.jar gateway.jar

# Khai báo port mà API Gateway sẽ chạy (theo application.properties bạn đã cấu hình là 8080)
EXPOSE 8080

# Lệnh khởi động ứng dụng Spring Boot khi Container chạy lên
ENTRYPOINT ["java", "-jar", "gateway.jar"]