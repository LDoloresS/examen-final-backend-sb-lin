package com.codigo.ms_order.service.impl;

import com.codigo.ms_order.aggregates.DTO.OrderRedisDTO;
import com.codigo.ms_order.aggregates.request.InventoryRequest;
import com.codigo.ms_order.aggregates.request.OrderRequest;
import com.codigo.ms_order.aggregates.response.BaseResponse;
import com.codigo.ms_order.aggregates.response.InventoryResponse;
import com.codigo.ms_order.client.MsInventoryClient;
import com.codigo.ms_order.constants.Constants;
import com.codigo.ms_order.entity.CustomerEntity;
import com.codigo.ms_order.entity.OrderEntity;
import com.codigo.ms_order.entity.OrderItemEntity;
import com.codigo.ms_order.redis.RedisService;
import com.codigo.ms_order.repository.CustomerRepository;
import com.codigo.ms_order.repository.OrderItemRepository;
import com.codigo.ms_order.repository.OrderRepository;
import com.codigo.ms_order.service.OrderService;
import com.codigo.ms_order.util.AppUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Log4j2
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderItemRepository orderItemRepository;
    private final RedisService redisService;
    private final MsInventoryClient msInventoryClient;

    public OrderServiceImpl(OrderRepository orderRepository, CustomerRepository customerRepository, OrderItemRepository orderItemRepository, RedisService redisService, MsInventoryClient msInventoryClient) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderItemRepository = orderItemRepository;
        this.redisService = redisService;
        this.msInventoryClient = msInventoryClient;
    }

    @Transactional
    @Override
    public ResponseEntity<BaseResponse<OrderEntity>> save(OrderRequest orderRequest) {
        BaseResponse<OrderEntity> baseResponse = new BaseResponse<>();
        Timestamp auditsDate = new Timestamp(System.currentTimeMillis());

        // Verificación de si hay items en la orden
        if (AppUtil.isNotNullOrEmpty(Collections.singletonList(orderRequest.getOrderItems()))) {
            baseResponse.setCode(Constants.ERROR_ORDER_CODE);
            baseResponse.setMessage(Constants.ERROR_ORDER_MESS + " No tiene Detalles");
            baseResponse.setObjeto(Optional.empty());
            return ResponseEntity.ok(baseResponse);
        }

        // Verificar stock
        boolean outOfStock = orderRequest.getOrderItems().stream()
                .anyMatch(item -> !verificarStock(item));

        if (outOfStock) {
            baseResponse.setCode(Constants.ERROR_ORDER_CODE);
            baseResponse.setMessage(Constants.ERROR_ORDER_MESS + " No tiene stock suficiente");
            baseResponse.setObjeto(Optional.empty());
            return ResponseEntity.ok(baseResponse);
        }

        // Crear la orden
        OrderEntity order = new OrderEntity();
        order.setCustomer(orderRequest.getCustomer());
        order.setStatus(Constants.STATUS_PENDIENTE);
        order.setTotal(orderRequest.getTotal());
        order.setOrderItems(orderRequest.getOrderItems());
        order.setCreatedAt(auditsDate);
        order.setUpdatedAt(auditsDate);

        // Buscar el cliente y procesar la orden
        return customerRepository.findById(order.getCustomer().getId())
                .map(customer -> {
                    // Asignar la orden a los items
                    order.getOrderItems().forEach(item -> item.setOrder(order));

                    // Guardar la orden en el repositorio
                    OrderEntity savedOrder = orderRepository.save(order);
                    baseResponse.setCode(Constants.OK_ORDER_CODE);
                    baseResponse.setMessage(Constants.OK_ORDER_MESS);
                    baseResponse.setObjeto(Optional.of(savedOrder));

                    // Reducir el inventario
                    order.getOrderItems().forEach(item -> {
                        reducirInventory(item.getProduct().getId(), item.getQuantity());
                    });

                    // Guardar en Redis
                    saveToRedis(savedOrder);

                    return ResponseEntity.ok(baseResponse);
                })
                .orElseGet(() -> {
                    baseResponse.setCode(Constants.ERROR_ORDER_CODE);
                    baseResponse.setMessage(Constants.ERROR_ORDER_MESS + " Cliente no encontrado");
                    baseResponse.setObjeto(Optional.empty());
                    return ResponseEntity.ok(baseResponse);
                });
    }

    @Override
    public ResponseEntity<BaseResponse<List<OrderEntity>>> getOrders(int page, int size) {
        BaseResponse<List<OrderEntity>> baseResponse = new BaseResponse<>();

        Pageable pageable = PageRequest.of(page, size);

        Optional.of(orderRepository.findAllBy(pageable))
                .filter(orders -> !orders.isEmpty())
                .ifPresentOrElse(
                        orders -> {
                            baseResponse.setCode(Constants.OK_ORDER_CODE);
                            baseResponse.setMessage(Constants.OK_ORDER_MESS);
                            baseResponse.setObjeto(Optional.of(orders));
                        },
                        () -> {
                            baseResponse.setCode(Constants.ERROR_CODE_LIST_EMPTY);
                            baseResponse.setMessage(Constants.ERROR_MESS_LIST_EMPTY);
                            baseResponse.setObjeto(Optional.empty());
                        }
                );

        return ResponseEntity.ok(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<OrderEntity>> updateStatus(Long id) {
        BaseResponse<OrderEntity> baseResponse = new BaseResponse<>();

        getOrderSaved(id).ifPresentOrElse(
                orderUPD -> {
                    orderUPD.setStatus(Constants.STATUS_PROCESADO);
                    orderUPD.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
                    baseResponse.setCode(Constants.OK_ORDER_CODE);
                    baseResponse.setMessage(Constants.OK_ORDER_MESS);
                    baseResponse.setObjeto(Optional.of(orderRepository.save(orderUPD)));

                    delFromRedis(orderUPD.getId());
                },
                () -> {
                    baseResponse.setCode(Constants.ERROR_CODE_PROCESADO);
                    baseResponse.setMessage(Constants.ERROR_MESS_PROCESADO);
                    baseResponse.setObjeto(Optional.empty());
                }
        );
        return ResponseEntity.ok(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<List<OrderEntity>>> findAll() {
        BaseResponse<List<OrderEntity>> baseResponse = new BaseResponse<>();

        Optional.of(orderRepository.findAll())
                .filter(orders -> !orders.isEmpty())
                .ifPresentOrElse(
                        orders -> {
                            baseResponse.setCode(Constants.OK_ORDER_CODE);
                            baseResponse.setMessage(Constants.OK_ORDER_MESS);
                            baseResponse.setObjeto(Optional.of(orders));
                        },
                        () -> {
                            baseResponse.setCode(Constants.ERROR_CODE_LIST_EMPTY);
                            baseResponse.setMessage(Constants.ERROR_MESS_LIST_EMPTY);
                            baseResponse.setObjeto(Optional.empty());
                        }
                );

        return ResponseEntity.ok(baseResponse);
    }

    @Override
    public ResponseEntity<BaseResponse<OrderEntity>> findById(Long id) {
        BaseResponse<OrderEntity> baseResponse = new BaseResponse<>();

        orderRepository.findById(id).ifPresentOrElse(
                orderDB -> {
                    baseResponse.setCode(Constants.OK_ORDER_CODE);
                    baseResponse.setMessage(Constants.OK_ORDER_MESS);
                    baseResponse.setObjeto(Optional.of(orderDB));
                },
                () -> {
                    baseResponse.setCode(Constants.ERROR_ORDER_CODE);
                    baseResponse.setMessage(Constants.ERROR_ORDER_MESS);
                    baseResponse.setObjeto(Optional.empty());
                }
        );

        return ResponseEntity.ok(baseResponse);
    }

    private void saveToRedis(OrderEntity orderEntity) {
        Optional.ofNullable(orderEntity)
                .map(order -> {
                    OrderRedisDTO orderRedisDTO = new OrderRedisDTO();
                    orderRedisDTO.setId(order.getId().toString());
                    orderRedisDTO.setCustomerId(order.getCustomer().getId().toString());
                    orderRedisDTO.setStatus(order.getStatus());
                    orderRedisDTO.setTotal(order.getTotal().toString());
                    orderRedisDTO.setCreatedAt(order.getCreatedAt().toString());
                    orderRedisDTO.setUpdatedAt(order.getUpdatedAt().toString());
                    return orderRedisDTO;
                })
                .map(AppUtil::convertirAString)
                .ifPresent(dataForRedis -> {
                    try {
                        redisService.saveKeyValue(
                                Constants.REDIS_KEY_API_ORDER + orderEntity.getId(),
                                dataForRedis, Constants.REDIS_EXP);
                    } catch (Exception e) {
                        log.info("Error al Guardar el Pedido en Redis: ", e.getMessage());
                    }
                });
    }

    private void delFromRedis(Long id) {
        try {
            redisService.deleteByKey(Constants.REDIS_KEY_API_ORDER + id);
        } catch (Exception e) {
            log.info("Error al Eliminar el Pedido en Redis: ", e.getMessage());
        }
    }

    private Optional<OrderEntity> getOrderSaved(Long id) {
        String redisInfo = null;

        try {
            redisInfo = redisService.getValueByKey(Constants.REDIS_KEY_API_ORDER + id);
        } catch (Exception e) {
            log.info("Error al Recuperar el Pedido desde Redis: ", e.getMessage());
        }
        if (Objects.nonNull(redisInfo)) {
            return Optional.ofNullable(AppUtil.convertirDesdeString(redisInfo, OrderRedisDTO.class))
                    .map(dato -> {
                        CustomerEntity customer = new CustomerEntity();
                        customer.setId(Long.valueOf(dato.getCustomerId()));

                        OrderEntity order = new OrderEntity();
                        order.setId(Long.valueOf(dato.getId()));
                        order.setCustomer(customer);
                        order.setStatus(dato.getStatus());
                        order.setTotal(Double.valueOf(dato.getTotal()));
                        order.setCreatedAt(Timestamp.valueOf(dato.getCreatedAt()));
                        order.setUpdatedAt(Timestamp.valueOf(dato.getUpdatedAt()));

                        return order;
                    });
        } else {
            return orderRepository.findByIdAndStatus(id, Constants.STATUS_PENDIENTE)
                    .map(order -> {
                        saveToRedis(order);
                        return order;
                    })
                    .map(Optional::of)
                    .orElse(Optional.empty());

        }
    }

    private boolean verificarStock(OrderItemEntity item) {
        boolean flat = false;
        BaseResponse<InventoryResponse> a = msInventoryClient.buscarInventoryProductId(item.getProduct().getId());
        if (a.getObjeto().isPresent()) {
            InventoryResponse c = a.getObjeto().get();
            if (c.getQuantity() >= item.getQuantity()) {
                flat = true;
            }
        }
        return flat;
    }

    private void reducirInventory(Long productId, Long quantity) {
        InventoryRequest request = new InventoryRequest();
        request.setQuantity(quantity);
        msInventoryClient.reducirInventory(productId, request);
    }

}
