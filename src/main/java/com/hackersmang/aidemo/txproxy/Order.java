package com.hackersmang.aidemo.txproxy;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// "order" is a reserved word in H2/SQL (ORDER BY) — must not use the
// default table name, or table creation and every query break.
@Entity
@Table(name = "txproxy_orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private double amount;

    protected Order() {
        // for JPA
    }

    public Order(String customerName, double amount) {
        this.customerName = customerName;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getAmount() {
        return amount;
    }
}
