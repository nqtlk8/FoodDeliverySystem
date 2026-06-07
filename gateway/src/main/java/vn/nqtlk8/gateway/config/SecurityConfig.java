package vn.nqtlk8.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        // Cho phép gọi thoải mái các API đăng nhập, đăng ký
                        .pathMatchers("/v1/auth/**").permitAll()
                        // Bắt buộc phải có token hợp lệ với mọi API còn lại
                        .anyExchange().authenticated()
                )
                // Kích hoạt xác thực bằng JWT (tự động validate qua Keycloak)
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(org.springframework.security.config.Customizer.withDefaults()));

        return http.build();
    }
}