package vn.nqtlk8.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class GlobalTrackingFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Sinh Trace-ID duy nhất cho request
        String traceId = UUID.randomUUID().toString();

        return ReactiveSecurityContextHolder.getContext()
                .filter(c -> c.getAuthentication() != null && c.getAuthentication().getPrincipal() instanceof Jwt)
                .map(c -> (Jwt) c.getAuthentication().getPrincipal())
                .map(jwt -> jwt.getClaimAsString("sub")) // 'sub' là trường mặc định chứa User ID trong Keycloak
                .defaultIfEmpty("anonymous")
                .flatMap(userId -> {
                    // Biến đổi Request: Gắn thêm 2 Header quan trọng
                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-Trace-Id", traceId)
                            .header("X-User-Id", userId)
                            .build();

                    // Lưu TraceId vào Exchange attribute để LoggingFilter lát nữa dùng
                    exchange.getAttributes().put("TraceId", traceId);

                    // Cho phép Request đi tiếp xuống các Microservice
                    return chain.filter(exchange.mutate().request(mutatedRequest).build());
                });
    }

    @Override
    public int getOrder() {
        // Thứ tự ưu tiên cao (-1) để đảm bảo Header được gắn trước khi forward
        return -1;
    }
}