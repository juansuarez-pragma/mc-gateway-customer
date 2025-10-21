package com.stech.mcc_gateway_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final String[] FREE_RESOURCES_URLS = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
            "/swagger-resources/**", "/api-docs/**", "/webjars/**"
    };

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(
                        exchanges -> exchanges
                                .pathMatchers(FREE_RESOURCES_URLS).permitAll()
                                .anyExchange().authenticated()
                )
                .oauth2ResourceServer(server -> server.jwt(jwt -> {
                }))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .build();
    }
}
