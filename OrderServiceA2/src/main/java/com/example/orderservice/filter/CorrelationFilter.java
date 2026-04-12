package com.example.orderservice.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;

import java.io.IOException;
import java.util.UUID;

// CorrelationFilter is a filter that adds a correlation ID to the request and logs it.
public class CorrelationFilter implements Filter {

    private static final String HEADER_NAME = "X-Correlation-Id";

    // This is the filter method that is called by the servlet container.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String correlationId = httpRequest.getHeader(HEADER_NAME);

        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
