package com.codigo.ms_order.aggregates.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryResponse {
    private Long productId;
    private Long quantity;
}
