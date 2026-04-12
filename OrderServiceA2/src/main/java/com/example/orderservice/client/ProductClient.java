package com.example.orderservice.client;


import com.example.orderservice.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient( name = "ProductService", url = "http://product-service:8080", configuration = com.example.orderservice.config.FeignConfig.class)
public interface ProductClient {

    @GetMapping("/api/products/{id}")
    Product getProduct(@PathVariable("id") int id);

}