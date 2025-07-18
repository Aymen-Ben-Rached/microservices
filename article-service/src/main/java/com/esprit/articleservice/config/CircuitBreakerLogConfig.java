package com.esprit.articleservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Slf4j
@Configuration
public class CircuitBreakerLogConfig {

    private final CircuitBreakerRegistry registry;

    public CircuitBreakerLogConfig(CircuitBreakerRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    public void initCircuitBreakerLogging() {
        CircuitBreaker circuitBreaker = registry.circuitBreaker("stock");

        circuitBreaker.getEventPublisher()
                .onStateTransition(event ->
                        log.warn("Circuit Breaker 'stock' changed state from {} to {}",
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState())
                )
                .onError(event ->
                        log.error("Circuit Breaker 'stock' error: {}", event.getThrowable().getMessage())
                );
    }
}
