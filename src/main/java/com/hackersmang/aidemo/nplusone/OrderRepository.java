package com.hackersmang.aidemo.nplusone;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("npOrderRepository")
public interface OrderRepository extends JpaRepository<Order, Long> {

    // --- FIX: one query instead of N+1. Swap OrderService over to call
    // this instead of findAll() to show the "after" state live. ---
    @Query("select distinct o from Order o join fetch o.items")
    List<Order> findAllWithItems();
}
