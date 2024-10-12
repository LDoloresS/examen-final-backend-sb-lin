package com.codigo.ms_inventory.service;

import com.codigo.ms_inventory.aggregates.request.InventoryRequest;
import com.codigo.ms_inventory.aggregates.response.BaseResponse;
import com.codigo.ms_inventory.entity.InventoryEntity;
import org.springframework.http.ResponseEntity;

public interface InventoryService {
    ResponseEntity<BaseResponse<InventoryEntity>> buscarInventoryProductId(Long productId);

    ResponseEntity<BaseResponse<InventoryEntity>> reducirInventory(Long id, InventoryRequest inventoryRequest);
}
