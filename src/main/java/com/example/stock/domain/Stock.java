package com.example.stock.domain;

import javax.persistence.*;

@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long quantity;
    @Version
    private Long version;

    public Stock() {

    }

    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void decreaseStock(Long cnt) {
        if (this.quantity - cnt < 0) {
            throw new RuntimeException("out of stock");
        }

        this.quantity -= cnt;
    }
}
