package com.codigo.ms_order.service;

import com.codigo.ms_order.aggregates.request.OrderRequest;
import com.codigo.ms_order.aggregates.response.BaseResponse;
import com.codigo.ms_order.entity.OrderEntity;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface OrderService {
    ResponseEntity<BaseResponse<OrderEntity>> save(OrderRequest orderRequest);

    ResponseEntity<BaseResponse<List<OrderEntity>>> getOrders(int page, int size);

    ResponseEntity<BaseResponse<OrderEntity>> updateStatus(Long id);

    ResponseEntity<BaseResponse<List<OrderEntity>>> findAll();

    ResponseEntity<BaseResponse<OrderEntity>> findById(Long id);
}
