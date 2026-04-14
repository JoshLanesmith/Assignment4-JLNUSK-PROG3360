package com.example.orderservice.controller;

import com.example.orderservice.client.ProductClient;
import com.example.orderservice.entity.Product;
import com.example.orderservice.model.Order;
import com.example.orderservice.repository.OrderRepository;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.MDC;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
class OrderController {
    @Autowired
    OrderRepository orderRepository;

    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    private final ProductClient productClient;
    private final Tracer tracer;

    private final Counter orderCounter;

    OrderController(ProductClient productClient, MeterRegistry registry, Tracer tracer) {
        this.productClient = productClient;
        this.tracer = tracer;

        this.orderCounter = Counter.builder("total_orders")
                .description("Total number of orders created")
                .register(registry);
    }

    @Value("${app.version:unknown}")
    private String appVersion;

    private static String toTraceParentTraceId(String traceId) {
        if (traceId == null) {
            return null;
        }
        if (traceId.length() >= 32) {
            return traceId;
        }
        return String.format("%32s", traceId).replace(' ', '0');
    }

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
    public ResponseEntity<Order> insertOrder(@RequestBody Order order){

        log.info("order_received productId={} quantity={}",
                order.getProductId(), order.getQuantity());

        try {
            long pricingStartTime = System.currentTimeMillis();
            log.info("product_lookup_start productId={}", order.getProductId());

            Span productLookupSpan = tracer.nextSpan().name("order.product-service.lookup").start();
            Product orderedProduct;

            try (Tracer.SpanInScope scope = tracer.withSpan(productLookupSpan)) {
                        String correlationId = MDC.get("correlationId");
                        Span currentSpan = tracer.currentSpan();
                        String traceId = currentSpan != null ? currentSpan.context().traceId() : null;
                        String spanId = currentSpan != null ? currentSpan.context().spanId() : null;
                        String b3 = (traceId != null && spanId != null) ? traceId + "-" + spanId + "-1" : null;
                        String traceparent = (traceId != null && spanId != null)
                            ? "00-" + toTraceParentTraceId(traceId) + "-" + spanId + "-01"
                            : null;

                        log.info("outgoing_trace_context productId={} traceId={} spanId={} b3={} traceparent={}",
                            order.getProductId(), traceId, spanId, b3, traceparent);

                        orderedProduct = productClient.getProduct(
                                order.getProductId(),
                                correlationId,
                                b3,
                                traceId,
                                spanId,
                            "1",
                            traceparent
                        );
            } catch (Exception ex) {
                productLookupSpan.error(ex);
                throw ex;
            } finally {
                productLookupSpan.end();
            }

            if (orderedProduct == null) {
                log.warn("order_rejected productId={} quantity={} reason=PRODUCT_NOT_FOUND",
                        order.getProductId(), order.getQuantity());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            log.info(
                    "product_lookup_result productId={} outcome=FOUND unitPrice={} availableQuantity={}",
                    orderedProduct.getId(),
                    orderedProduct.getPrice(),
                    orderedProduct.getQuantity()
            );

            if (orderedProduct.getQuantity() < order.getQuantity()) {
                log.warn("order_rejected productId={} requestedQuantity={} availableQuantity={} reason=INSUFFICIENT_STOCK",
                        order.getProductId(),
                        order.getQuantity(),
                        orderedProduct.getQuantity());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            float totalPrice = orderedProduct.getPrice() * order.getQuantity();

            log.info(
                    "pricing_decision productId={} quantity={} unitPrice={} discountApplied={} finalPrice={} lookupLatencyMs={}",
                    order.getProductId(),
                    order.getQuantity(),
                    orderedProduct.getPrice(),
                    false,
                    totalPrice,
                    System.currentTimeMillis() - pricingStartTime
            );

            order.setTotalPrice(totalPrice);
            Order savedOrder = orderRepository.save(order);
            orderCounter.increment();

            log.info(
                    "order_persisted orderId={} productId={} quantity={} totalPrice={}",
                    savedOrder.getId(),
                    savedOrder.getProductId(),
                    savedOrder.getQuantity(),
                    savedOrder.getTotalPrice()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);

        } catch (FeignException.NotFound ex) {
            log.warn(
                    "order_rejected productId={} quantity={} reason=PRODUCT_NOT_FOUND_DOWNSTREAM",
                    order.getProductId(),
                    order.getQuantity()
            );
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (FeignException ex) {
            log.error("downstream_call_failed service=product-service productId={}", order.getProductId(), ex);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();

        } catch (Exception ex) {
            log.error("order_creation_failed productId={} quantity={}", order.getProductId(), order.getQuantity(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
