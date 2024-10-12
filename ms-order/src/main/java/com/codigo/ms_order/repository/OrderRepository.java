package com.codigo.ms_order.repository;

import com.codigo.ms_order.entity.OrderEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderEntity, Long> {
    Optional<OrderEntity> findByIdAndStatus(Long id, String status);

    List<OrderEntity> findAllBy(Pageable pageable);
}
