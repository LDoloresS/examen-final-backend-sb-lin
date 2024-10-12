package com.codigo.ms_inventory.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "inventory")
@Data
public class InventoryEntity {
    @Id
    @Column(name = "product_id")
    private Long productId;
    private Long quantity;
}
