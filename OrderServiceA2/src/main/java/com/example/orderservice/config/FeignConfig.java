package com.example.orderservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// FeignConfig is a configuration class that adds a correlation ID to the request headers.
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor correlationIdInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                String correlationId = MDC.get("correlationId");

                if (correlationId != null) {
                    template.header("X-Correlation-Id", correlationId);
                }
            }
        };
    }
}