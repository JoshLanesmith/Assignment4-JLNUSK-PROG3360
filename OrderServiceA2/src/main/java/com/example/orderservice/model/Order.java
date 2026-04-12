package com.example.orderservice.model;


import jakarta.persistence.*;

@Entity
@Table(name="orders")
public class Order {

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private int productId;
    private int quantity;
    private float totalPrice;
    private String status;

}