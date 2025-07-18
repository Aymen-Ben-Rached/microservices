package com.esprit.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DynamicRoutingConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("dynamic-article-route", r -> r.path("/api/articles/**")
                        .filters(f -> f.rewritePath("/api/articles/(?<segment>.*)", "/articles/${segment}")
                                .addRequestHeader("X-API-Version", "v1"))
                        .uri("lb://article-service"))
                .route("dynamic-stock-route", r -> r.path("/api/stocks/**")
                        .filters(f -> f.rewritePath("/api/stocks/(?<segment>.*)", "/stocks/${segment}")
                                .addRequestHeader("X-API-Version", "v1"))
                        .uri("lb://stock-service"))
                .build();
    }
}
