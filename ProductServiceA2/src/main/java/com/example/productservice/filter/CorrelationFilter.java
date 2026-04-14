package com.example.productservice.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

// CorrelationFilter is a filter that adds a correlation ID to the request and logs it.
@Component
public class CorrelationFilter implements Filter {

    private static final String HEADER_NAME = "X-Correlation-Id";
    private static final Logger log = LoggerFactory.getLogger(CorrelationFilter.class);

    // This is the filter method that is called by the servlet container.
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String correlationId = httpRequest.getHeader(HEADER_NAME);
        String b3 = httpRequest.getHeader("b3");
        String xB3TraceId = httpRequest.getHeader("X-B3-TraceId");
        String xB3SpanId = httpRequest.getHeader("X-B3-SpanId");
        String traceparent = httpRequest.getHeader("traceparent");

        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        MDC.put("correlationId", correlationId);

        if (httpRequest.getRequestURI().startsWith("/api/products")) {
            log.info(
                    "incoming_trace_headers path={} correlationId={} b3={} xB3TraceId={} xB3SpanId={} traceparent={}",
                    httpRequest.getRequestURI(),
                    correlationId,
                    b3,
                    xB3TraceId,
                    xB3SpanId,
                    traceparent
            );
        }

        try {
            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
