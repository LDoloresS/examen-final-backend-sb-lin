package com.codigo.ms_order.aggregates.request;

import com.codigo.ms_order.entity.CustomerEntity;
import com.codigo.ms_order.entity.OrderItemEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Double total;
    private CustomerEntity customer;
    private List<OrderItemEntity> orderItems;
}
