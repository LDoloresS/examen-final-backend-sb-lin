package com.codigo.ms_inventory.service.impl;

import com.codigo.ms_inventory.aggregates.DTO.InventoryRedisDTO;
import com.codigo.ms_inventory.aggregates.constants.Constants;
import com.codigo.ms_inventory.aggregates.request.InventoryRequest;
import com.codigo.ms_inventory.aggregates.response.BaseResponse;
import com.codigo.ms_inventory.util.Utils;
import com.codigo.ms_inventory.entity.InventoryEntity;
import com.codigo.ms_inventory.repository.InventoryRepository;
import com.codigo.ms_inventory.service.InventoryService;
import com.codigo.ms_inventory.redis.RedisService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final RedisService redisService;

    public InventoryServiceImpl(InventoryRepository inventoryRepository, RedisService redisService) {
        this.inventoryRepository = inventoryRepository;
        this.redisService = redisService;
    }

    @Override
    public ResponseEntity<BaseResponse<InventoryEntity>> buscarInventoryProductId(Long productId) {
        BaseResponse<InventoryEntity> baseResponse = new BaseResponse<>();
        Optional<InventoryEntity> inventoryDB = executionBuscarInventoryProduct(productId);
        if (inventoryDB.isPresent()) {
            baseResponse.setCode(Constants.OK_PRODUCT_CODE);
            baseResponse.setMessage(Constants.OK_PRODUCT_MESS);
            baseResponse.setObjeto(inventoryDB);
        } else {
            baseResponse.setCode(Constants.ERROR_PRODUCT_CODE);
            baseResponse.setMessage(Constants.ERROR_PRODUCT_MESS);
            baseResponse.setObjeto(Optional.empty());
        }
        return ResponseEntity.ok(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<InventoryEntity>> reducirInventory(Long id, InventoryRequest inventoryRequest) {
        BaseResponse<InventoryEntity> baseResponse = new BaseResponse<>();
        Optional<InventoryEntity> inventoryDB = inventoryRepository.findById(id);
        if (inventoryDB.isPresent()) {
            inventoryDB.get().setQuantity(inventoryDB.get().getQuantity() - inventoryRequest.getQuantity());
            delFromRedis(id);
            saveToRedis(inventoryDB.get());
            baseResponse.setCode(Constants.OK_PRODUCT_CODE);
            baseResponse.setMessage(Constants.OK_PRODUCT_MESS);
            baseResponse.setObjeto(Optional.of(inventoryRepository.save(inventoryDB.get())));
        } else {
            baseResponse.setCode(Constants.ERROR_PRODUCT_CODE);
            baseResponse.setMessage(Constants.ERROR_PRODUCT_MESS);
            baseResponse.setObjeto(Optional.empty());
        }
        return ResponseEntity.ok(baseResponse);
    }

    private Optional<InventoryEntity> executionBuscarInventoryProduct(Long productId) {
        String redisInfo = null;
        InventoryEntity inventory;
        try {
            redisInfo = redisService.getValueByKey(Constants.REDIS_KEY_API_INVENTORY + productId);
        } catch (Exception e) {
            log.info("Error al Recuperar el Inventario desde Redis: ", e.getMessage());
        }
        if (Objects.nonNull(redisInfo)) {
            InventoryRedisDTO dato = Utils.convertirDesdeString(redisInfo, InventoryRedisDTO.class);
            inventory = new InventoryEntity();
            inventory.setProductId(Long.valueOf(dato.getProductId()));
            inventory.setQuantity(Long.valueOf(dato.getQuantity()));
        } else {
            inventory = inventoryRepository.findById(productId).get();
            saveToRedis(inventory);
        }
        return Optional.of(inventory);
    }

    private void saveToRedis(InventoryEntity inventoryEntity) {
        try {
            String dataForRedis = Utils.convertirAString(inventoryEntity);
            redisService.saveKeyValue(Constants.REDIS_KEY_API_INVENTORY + inventoryEntity.getProductId(),
                    dataForRedis, Constants.REDIS_EXP);
        } catch (Exception e) {
            log.info("Error al Guardar el Inventario en Redis: ", e.getMessage());
        }
    }

    private void delFromRedis(Long id) {
        try {
            redisService.deleteByKey(Constants.REDIS_KEY_API_INVENTORY + id);
        } catch (Exception e) {
            log.info("Error al Eliminar el Inventario en Redis: ", e.getMessage());
        }
    }
}
