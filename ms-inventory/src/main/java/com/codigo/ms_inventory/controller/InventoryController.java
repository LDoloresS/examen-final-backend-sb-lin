package com.codigo.ms_inventory.controller;

import com.codigo.ms_inventory.aggregates.constants.Constants;
import com.codigo.ms_inventory.aggregates.request.InventoryRequest;
import com.codigo.ms_inventory.aggregates.response.BaseResponse;
import com.codigo.ms_inventory.entity.InventoryEntity;
import com.codigo.ms_inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ms-inventory/v1")
@RequiredArgsConstructor
public class InventoryController {
    private final InventoryService inventoryService;

    @GetMapping("/inventory/{productId}")
    public ResponseEntity<BaseResponse<InventoryEntity>> buscarInventoryProductId(@PathVariable("productId") Long productId) {
        BaseResponse<InventoryEntity> response = inventoryService.buscarInventoryProductId(productId).getBody();
        if (response.getCode().equals(Constants.OK_PRODUCT_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/inventory/{productId}")
    public ResponseEntity<BaseResponse<InventoryEntity>> reducirInventory(@PathVariable("productId") Long productId, @RequestBody InventoryRequest inventoryRequest) {
        BaseResponse<InventoryEntity> response = inventoryService.reducirInventory(productId, inventoryRequest).getBody();
        if (response.getCode().equals(Constants.OK_PRODUCT_CODE)) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
