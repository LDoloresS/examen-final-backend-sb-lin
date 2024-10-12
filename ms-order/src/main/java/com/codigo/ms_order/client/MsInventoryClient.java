package com.codigo.ms_order.client;

import com.codigo.ms_order.aggregates.request.InventoryRequest;
import com.codigo.ms_order.aggregates.response.BaseResponse;
import com.codigo.ms_order.aggregates.response.InventoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ms-inventory")
public interface MsInventoryClient {
    @GetMapping("/api/ms-inventory/v1/inventory/{productId}")
    BaseResponse<InventoryResponse> buscarInventoryProductId(@PathVariable("productId") Long productId);

    @PostMapping("/api/ms-inventory/v1/inventory/{productId}")
    BaseResponse<InventoryResponse> reducirInventory(@PathVariable("productId") Long productId,
                                                     @RequestBody InventoryRequest inventoryRequest);
}
