package com.esprit.articleservice.config;

import com.esprit.articleservice.exception.StockServiceException;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public ErrorDecoder errorDecoder() {
        return (methodKey, response) -> {
            if (response.status() >= 400 && response.status() <= 499) {
                return new StockServiceException(
                        "Client error when calling stock service: " + response.reason(),
                        "STOCK_CLIENT_ERROR"
                );
            }
            if (response.status() >= 500) {
                return new StockServiceException(
                        "Server error when calling stock service: " + response.reason(),
                        "STOCK_SERVER_ERROR"
                );
            }
            return new Exception("Error while calling stock service");
        };
    }

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // Add any common headers here
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Accept", "application/json");
        };
    }
}

