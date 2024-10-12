package com.codigo.ms_inventory.aggregates.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryRedisDTO {
    private String productId;
    private String quantity;
}
