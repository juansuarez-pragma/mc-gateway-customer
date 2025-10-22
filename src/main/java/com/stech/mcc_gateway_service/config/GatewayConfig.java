package com.stech.mcc_gateway_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
public class GatewayConfig {

    @Value("${microservices.customer-service-url}")
    private String customerServiceUrl;
    @Value("${microservices.account-service-url}")
    private String accountServiceUrl;
    @Value("${microservices.credit-disbursement-service-url}")
    private String creditDisbursementServiceUrl;

    @Value("${gateway-retry.attempts}")
    private int retryAttempts;

    private Set<String> getAll5xxStatusCodes() {
        return IntStream.rangeClosed(500, 599)
                .mapToObj(String::valueOf)
                .collect(Collectors.toSet());
    }

    @Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
        Set<String> statusCodes5xx = getAll5xxStatusCodes();
        return builder.routes()
                .route("customer-service", r -> r.path("/customers/**")
                        .filters(f -> f
                                .circuitBreaker(cb -> cb.setName("customerServiceCircuitBreaker")
                                        .setFallbackUri("forward://fallback/customers"))
                                .retry(retryConfig -> retryConfig.setRetries(retryAttempts)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR)))
                        .uri(customerServiceUrl))
                .route("account-service", r -> r.path("/accounts/**")
                        .filters(f -> f
                                .circuitBreaker(cb -> cb.setName("accountServiceCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/accounts")
                                        .setStatusCodes(statusCodes5xx)
                                )
                                .retry(retryConfig -> retryConfig.setRetries(retryAttempts)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))
                        )
                        .uri(accountServiceUrl))
                .route("credit-disbursement-service", r -> r.path("/credit-disbursements/**")
                        .filters(f -> f
                                .circuitBreaker(cb -> cb.setName("creditDisbursementCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/credit-disbursements"))
                                .retry(retryConfig -> retryConfig.setRetries(retryAttempts)
                                        .setStatuses(HttpStatus.INTERNAL_SERVER_ERROR))
                        )
                        .uri(creditDisbursementServiceUrl))
                .route("swagger-customers", r -> r.path("/v3/api-docs/customers")
                        .filters(f -> f
                                .rewritePath("/v3/api-docs/customers", "/v3/api-docs")
                                .circuitBreaker(cb -> cb.setName("swaggerCustomerCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/docs"))
                        )
                        .uri(customerServiceUrl))

                .route("swagger-accounts", r -> r.path("/v3/api-docs/accounts")
                        .filters(f -> f
                                .rewritePath("/v3/api-docs/accounts", "/v3/api-docs")
                                .circuitBreaker(cb -> cb.setName("swaggerAccountCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/docs"))
                        )
                        .uri(accountServiceUrl))
                .route("swagger-credit-disbursements", r -> r.path("/v3/api-docs/credit-disbursements")
                        .filters(f -> f
                                .rewritePath("/v3/api-docs/credit-disbursements", "/v3/api-docs")
                                .circuitBreaker(cb -> cb.setName("swaggerCreditDisbursementCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/docs"))
                        )
                        .uri(creditDisbursementServiceUrl))
                .build();

    }
}
