package com.example.orderservice.client;


import com.example.orderservice.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;


@FeignClient(
    name = "product-service",
    url = "${product.service.url:http://localhost:8080}"
)
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    Product getProduct(
            @PathVariable("id") int id,
            @RequestHeader(value = "X-Correlation-Id", required = false) String correlationId,
            @RequestHeader(value = "b3", required = false) String b3,
            @RequestHeader(value = "X-B3-TraceId", required = false) String xB3TraceId,
            @RequestHeader(value = "X-B3-SpanId", required = false) String xB3SpanId,
            @RequestHeader(value = "X-B3-Sampled", required = false) String xB3Sampled,
            @RequestHeader(value = "traceparent", required = false) String traceparent
    );

}