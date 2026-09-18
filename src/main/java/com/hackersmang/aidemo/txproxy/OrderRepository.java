package com.hackersmang.aidemo.txproxy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("txOrderRepository")
public interface OrderRepository extends JpaRepository<Order, Long> {
}
