package com.codigo.ms_order.aggregates.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderRedisDTO {
    private String id;
    private String customerId;
    private String status;
    private String total;
    private String createdAt;
    private String updatedAt;
}
