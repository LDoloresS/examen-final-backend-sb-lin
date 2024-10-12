package com.codigo.ms_order.controller;

import com.codigo.ms_order.aggregates.request.OrderRequest;
import com.codigo.ms_order.aggregates.response.BaseResponse;
import com.codigo.ms_order.constants.Constants;
import com.codigo.ms_order.entity.OrderEntity;
import com.codigo.ms_order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ms-order/v1")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<BaseResponse<OrderEntity>> save(@RequestBody OrderRequest orderRequest) {
        return Optional.ofNullable(orderService.save(orderRequest).getBody())
                .map(response -> response.getCode().equals(Constants.OK_ORDER_CODE)
                        ? new ResponseEntity<>(response, HttpStatus.CREATED)
                        : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/orders")
    public ResponseEntity<BaseResponse<List<OrderEntity>>> getOrders(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return Optional.ofNullable(orderService.getOrders(page, size).getBody())
                .map(response -> response.getCode().equals(Constants.OK_ORDER_CODE)
                        ? new ResponseEntity<>(response, HttpStatus.CREATED)
                        : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<BaseResponse<OrderEntity>> updateStatus(@PathVariable("id") Long id) {
        return Optional.ofNullable(orderService.updateStatus(id).getBody())
                .map(response -> response.getCode().equals(Constants.OK_ORDER_CODE)
                        ? new ResponseEntity<>(response, HttpStatus.CREATED)
                        : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/allorders")
    public ResponseEntity<BaseResponse<List<OrderEntity>>> findAll() {
        return Optional.ofNullable(orderService.findAll().getBody())
                .map(response -> response.getCode().equals(Constants.OK_ORDER_CODE)
                        ? new ResponseEntity<>(response, HttpStatus.CREATED)
                        : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<BaseResponse<OrderEntity>> findById(@PathVariable("id") Long id) {
        return Optional.ofNullable(orderService.findById(id).getBody())
                .map(response -> response.getCode().equals(Constants.OK_ORDER_CODE)
                        ? new ResponseEntity<>(response, HttpStatus.CREATED)
                        : new ResponseEntity<>(response, HttpStatus.BAD_REQUEST))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
