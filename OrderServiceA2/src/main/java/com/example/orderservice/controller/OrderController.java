package com.example.orderservice.controller;

import com.example.orderservice.client.ProductClient;
import com.example.orderservice.entity.Product;
import com.example.orderservice.model.Order;
import com.example.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
class OrderController {
    @Autowired
    OrderRepository orderRepository;

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final ProductClient productClient;

    OrderController(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Value("${app.version:unknown}")
    private String appVersion;

    @GetMapping("/version")
    public String getAppVersion() {
        return "Order Service running on version: "  + appVersion;
    }

    // Get all Orders from Order List
    @GetMapping("")
    public List<Order> getAllOrders(){
        return orderRepository.findAll();
    }

    // Get one Order from Order List based on id
    @GetMapping("/{id}")
    public Optional<Order> getOrder(@PathVariable int id){
        return orderRepository.findById(id);
    }

    @PostMapping("")
    public Order insertOrder(@RequestBody Order order){

        log.info("Received order request for productId: {}, quantity: {}",
                order.getProductId(), order.getQuantity());

        try {
            log.info("Calling product-service for productId: {}", order.getProductId());

            Product orderedProduct = productClient.getProduct(order.getProductId());

            if (orderedProduct == null) {
                log.warn("Product not found for productId: {}", order.getProductId());
                return null;
            }

            if (orderedProduct.getQuantity() < order.getQuantity()) {
                log.warn("Insufficient stock for productId: {}. Requested: {}, Available: {}",
                        order.getProductId(),
                        order.getQuantity(),
                          orderedProduct.getQuantity());
                return null;
            }

            float totalPrice = orderedProduct.getPrice() * order.getQuantity();

//            if (featureFlagService.isBulkDiscountEnabled() && order.getQuantity() > 5){
//                totalPrice = totalPrice - (totalPrice * 0.15f);
//
//                log.info("Bulk discount applied for orderId: {}, quantity: {}",
//                        order.getId(), order.getQuantity());
//            }

            order.setTotalPrice(totalPrice);
            Order savedOrder = orderRepository.save(order);

            log.info("Order created successfully with orderId: {}, totalPrice: {}",
                    savedOrder.getId(), savedOrder.getTotalPrice());

            return savedOrder;

        } catch (Exception ex) {
            log.error("Failed to call product-service for productId: {}", order.getProductId(), ex);
            throw ex;
        }
    }
}
