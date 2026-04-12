package com.example.orderservice.config;

import com.example.orderservice.filter.CorrelationFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// CorrelationFilter is a filter that adds a correlation ID to the request and logs it.
@Configuration
public class CorrelationConfig {

    @Bean
    public FilterRegistrationBean<CorrelationFilter> correlationFilter() {
        FilterRegistrationBean<CorrelationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CorrelationFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}