package com.hackersmang.aidemo.nplusone;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "np1_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    // Lazy by default for @OneToMany — this is the normal, recommended
    // setting. The bug in this demo isn't the laziness itself, it's how
    // the collection gets accessed afterwards (see OrderService).
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    protected Order() {
        // for JPA
    }

    public Order(String customerName) {
        this.customerName = customerName;
    }

    public void addItem(String product, double price) {
        OrderItem item = new OrderItem(this, product, price);
        items.add(item);
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<OrderItem> getItems() {
        return items;
    }
}
