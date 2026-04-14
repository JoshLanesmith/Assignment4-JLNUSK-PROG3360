package com.example.productservice.controller;

import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
class ProductController {
    @Autowired
    ProductRepository productRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @Value("${app.version:unknown}")
    private String appVersion;

    @GetMapping("/version")
    public String getAppVersion() {
        return "Product Service running on version: "  + appVersion;
    }

    // Get all products from Product List
    @GetMapping("")
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    // Get one Product from favouriteProduct List based on id
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id){

        log.info("product_lookup_start productId={}", id);

        Optional<Product> product = productRepository.findById(id);

        if (product.isEmpty()) {
            log.warn("product_lookup_result productId={} outcome=NOT_FOUND", id);
            return ResponseEntity.notFound().build();
        }

        Product foundProduct = product.get();
        log.info(
                "product_lookup_result productId={} outcome=FOUND price={} quantity={}",
                foundProduct.getId(),
                foundProduct.getPrice(),
                foundProduct.getQuantity()
        );

        return ResponseEntity.ok(foundProduct);
    }

    @PostMapping("")
    public ResponseEntity<Product> insertProduct(@RequestBody Product product){
        try{
            Product savedProduct = productRepository.save(product);

            log.info(
                    "product_created productId={} name={} price={} quantity={}",
                    savedProduct.getId(),
                    savedProduct.getName(),
                    savedProduct.getPrice(),
                    savedProduct.getQuantity()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        }
        catch(Exception e){
            log.error(
                    "product_create_failed name={} price={} quantity={}",
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity(),
                    e
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete product from favouriteProducts List based on id
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id){
        try{
            Optional<Product> toDelete = productRepository.findById(id);

            if (toDelete.isEmpty()) {
                log.warn("product_delete_rejected productId={} reason=NOT_FOUND", id);
                return ResponseEntity.notFound().build();
            }

            productRepository.delete(toDelete.get());

            log.info("product_deleted productId={}", id);

            return ResponseEntity.noContent().build();

        }
        catch (Exception e){
            log.error("product_delete_failed productId={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/premium")
    public List<Product> getPremiumPricedProducts(){
        List<Product> products = productRepository.findAll();

        log.info("premium_products_requested returnedCount={}", products.size());
        return products;
    }
}
