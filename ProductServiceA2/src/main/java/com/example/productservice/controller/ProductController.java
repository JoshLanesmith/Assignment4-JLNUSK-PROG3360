package com.example.productservice.controller;

import com.example.productservice.model.Product;
import com.example.productservice.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    public Optional<Product> getProduct(@PathVariable int id){

        log.info("Fetching product with ID: {}", id);

        Optional<Product> product = productRepository.findById(id);

        if (product.isEmpty()) {
            log.warn("Product not found for ID: {}", id);
        }

        return product;
    }

    @PostMapping("")
    public boolean insertProduct(@RequestBody Product product){
        try{
            productRepository.save(product);
        }
        catch(Exception e){
            return false;
        }
        return true;
    }

    // Delete product from favouriteProducts List based on id
    @DeleteMapping("/{id}")
    public boolean deleteProduct(@PathVariable Integer id){
        try{
            Optional<Product> toDelete = productRepository.findById(id);
            toDelete.ifPresent(product -> productRepository.delete(product));

            return true;
        }
        catch (Exception e){
            return false;
        }
    }

    @GetMapping("/premium")
    public List<Product> getPremiumPricedProducts(){
        List<Product> products = productRepository.findAll();

//        if(featureFlagService.isPremiumPricingEnabled()){
//            List<Product> listOfPremium = new ArrayList<>();
//            for (Product product : products) {
//                product.setPrice(product.getPrice() * 0.9f);
//                listOfPremium.add(product);
//            }
//            return listOfPremium;
//        }
        return products;
    }
}
