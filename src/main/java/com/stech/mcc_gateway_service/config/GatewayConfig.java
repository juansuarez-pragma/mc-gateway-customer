package com.stech.mcc_gateway_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Value("${microservices.customer-service-url}")
    private String customerServiceUrl;
    @Value("${microservices.account-service-url}")
    private String accountServiceUrl;
    @Value("${microservices.credit-disbursement-service-url}")
    private String creditDisbursementServiceUrl;

    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("customer-service", r -> r.path("/customers/**")
                        .uri(customerServiceUrl))
                .route("account-service", r -> r.path("/accounts/**")
                        .uri(accountServiceUrl))
                .route("credit-disbursement-service", r -> r.path("/credit-disbursements/**")
                        .uri(creditDisbursementServiceUrl))
                .route("swagger-customers", r -> r.path("/v3/api-docs/customers")
                        .filters(f -> f
                                .rewritePath("/v3/api-docs/customers", "/v3/api-docs"))
                        .uri(customerServiceUrl))
                .route("swagger-accounts", r -> r.path("/v3/api-docs/accounts")
                        .filters(f -> f
                                .rewritePath("/v3/api-docs/accounts", "/v3/api-docs"))
                        .uri(accountServiceUrl))
                .route("swagger-credit-disbursements", r -> r.path("/v3/api-docs/credit-disbursements")
                        .filters(f -> f
                                .rewritePath("/v3/api-docs/credit-disbursements", "/v3/api-docs"))
                        .uri(creditDisbursementServiceUrl))
                .build();
    }
}
