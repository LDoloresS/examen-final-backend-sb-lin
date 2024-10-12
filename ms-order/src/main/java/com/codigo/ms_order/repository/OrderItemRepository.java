package com.codigo.ms_order.repository;

import com.codigo.ms_order.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItemEntity, Long> {
    Iterable<? extends OrderItemEntity> findByOrderId(Long orderId);
}
