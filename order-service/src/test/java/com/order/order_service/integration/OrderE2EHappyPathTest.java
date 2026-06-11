package com.order.order_service.integration;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderE2EHappyPathTest {

    // Trỏ tới API Gateway
    private static final String GATEWAY_ORDER_URL = "http://localhost:8080/v1/orders";

    // Trỏ tới Keycloak để xin Token
    private static final String KEYCLOAK_TOKEN_URL = "http://localhost:8081/realms/food-delivery-realm/protocol/openid-connect/token";
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/order_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "rootpassword";

    private static KafkaProducer<String, String> producer;

    @BeforeAll
    static void setup() {
        Properties props = new Properties();
        // Trỏ tới Kafka Broker đang chạy qua Docker Compose
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producer = new KafkaProducer<>(props);
    }

    @Test
    void testFullHappyPathWithDriverMock() throws Exception {
        // ==========================================
        // Bước 1: Tạo đơn hàng (Đóng vai Client qua REST API)
        // Sử dụng Voucher GIAM20K đã có sẵn trong init.sql (Min spend: 50k)
        // ==========================================
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Lấy Token từ Keycloak
        System.out.println("🔐 Đang đăng nhập Keycloak để lấy Token...");
        String token = getAccessToken();
        String userId = extractUserIdFromToken(token);
        System.out.println("✅ Đã lấy Token thành công. User ID (sub): " + userId);

        // Header gửi vào API Gateway (Chỉ cần Token, KHÔNG CẦN X-User-Id / X-Trace-Id)
        headers.setBearerAuth(token);

        String requestBody = """
                {
                  "restaurant_id": "REST_01",
                  "voucher_code": "GIAM20K",
                  "total_amount": 60000.00,
                  "latitude": 10.762622,
                  "longitude": 106.660172,
                  "items": [
                    {
                      "item_id": "ITEM_01",
                      "name": "Phở bò",
                      "quantity": 1,
                      "price": 60000.00
                    }
                  ]
                }
                """;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(GATEWAY_ORDER_URL, request, String.class);

        assertEquals(200, response.getStatusCodeValue(), "API tạo order phải trả về 200 OK");
        String orderId = response.getBody();
        assertNotNull(orderId);

        System.out.println("✅ [1] Đã tạo đơn hàng thành công qua REST API. Order ID: " + orderId);

        // ==========================================
        // Bước 2: Chờ trạng thái WAITING_FOR_DRIVER
        // Voucher service sẽ nhận ORDER_CREATED và bắn lại VOUCHER_ACCEPTED
        // ==========================================
        System.out.println("⏳ Chờ Voucher Service xử lý logic...");
        await().atMost(15, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).until(() -> {
            String status = getOrderStatus(orderId);
            return "WAITING_FOR_DRIVER".equals(status);
        });
        System.out.println("✅ [2] Đơn hàng đã được áp dụng Voucher thành công và chuyển sang WAITING_FOR_DRIVER");

        // ==========================================
        // Bước 3: Đóng vai Driver Service -> Publish driver.assigned
        // ==========================================
        System.out.println("🚀 [3] Mock Driver Service: Tài xế nhận đơn. Gửi Kafka Event...");
        String driverAssignedEvent = String.format(
                "{\"order_id\":\"%s\", \"restaurant_id\":\"REST_01\", \"driver_name\":\"Tài xế E2E Test\"}", orderId);

        // Sinh một Trace ID mới cho ngữ cảnh Driver (Mô phỏng 1 tiến trình riêng biệt)
        String driverTraceId = UUID.randomUUID().toString();

        ProducerRecord<String, String> record1 = new ProducerRecord<>("driver.assigned", orderId, driverAssignedEvent);
        record1.headers().add("X-Trace-Id", driverTraceId.getBytes(StandardCharsets.UTF_8));
        record1.headers().add("X-User-Id", userId.getBytes(StandardCharsets.UTF_8));
        // Add __TypeId__ header to satisfy Spring Kafka's JsonDeserializer in
        // order-service
        record1.headers().add("__TypeId__",
                "com.order.order_service.dto.event.DriverAssignedEvent".getBytes(StandardCharsets.UTF_8));
        producer.send(record1);

        // Chờ Order Service cập nhật trạng thái
        await().atMost(10, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).until(() -> {
            return "DRIVER_ASSIGNED".equals(getOrderStatus(orderId));
        });
        System.out.println("✅ [3] Order Service đã nhận event và chuyển trạng thái sang DRIVER_ASSIGNED");

        // ==========================================
        // Bước 4: Đóng vai Driver Service -> Publish delivery.completed
        // ==========================================
        System.out.println("🛵 [4] Mock Driver Service: Giao hàng xong. Gửi Kafka Event...");
        String deliveryCompletedEvent = String.format("{\"order_id\":\"%s\"}", orderId);
        ProducerRecord<String, String> record2 = new ProducerRecord<>("delivery.completed", orderId,
                deliveryCompletedEvent);
        record2.headers().add("X-Trace-Id", driverTraceId.getBytes(StandardCharsets.UTF_8));
        record2.headers().add("X-User-Id", userId.getBytes(StandardCharsets.UTF_8));
        // Add __TypeId__ header to satisfy Spring Kafka's JsonDeserializer in
        // order-service
        record2.headers().add("__TypeId__",
                "com.order.order_service.dto.event.DeliveryCompletedEvent".getBytes(StandardCharsets.UTF_8));
        producer.send(record2);

        // Chờ Order Service cập nhật trạng thái
        await().atMost(10, TimeUnit.SECONDS).pollInterval(1, TimeUnit.SECONDS).until(() -> {
            return "COMPLETED".equals(getOrderStatus(orderId));
        });
        System.out.println("✅ [4] Order Service đã chuyển trạng thái sang COMPLETED. Luồng End-to-End thành công!");
    }

    private String getOrderStatus(String orderId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = conn.prepareStatement("SELECT status FROM orders WHERE id = ?")) {
            stmt.setString(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        } catch (Exception e) {
            System.err.println("Database fetch error: " + e.getMessage());
        }
        return null;
    }

    private String getAccessToken() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        // Vui lòng điều chỉnh theo cấu hình thật trên Keycloak của bạn:
        map.add("client_id", "api-gateway-client");
        map.add("client_secret", "TzvDIlurSaA5HPIBUgd61JBsmxlGo03r");
        map.add("username", "testuser");
        map.add("password", "testuser");
        map.add("grant_type", "password");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(KEYCLOAK_TOKEN_URL, request, String.class);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(response.getBody());
        return root.path("access_token").asText();
    }

    private String extractUserIdFromToken(String token) throws Exception {
        String[] chunks = token.split("\\.");
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(chunks[1]));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(payload);
        return root.path("sub").asText();
    }
}
