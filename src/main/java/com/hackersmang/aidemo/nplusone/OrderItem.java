package com.hackersmang.aidemo.nplusone;

import jakarta.persistence.*;

@Entity
@Table(name = "np1_order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private String product;
    private double price;

    protected OrderItem() {
        // for JPA
    }

    public OrderItem(Order order, String product, double price) {
        this.order = order;
        this.product = product;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public double getPrice() {
        return price;
    }
}
