package com.example.inventoryservice.dto;

import lombok.Data;

@Data
public class InventoryDto {

    private Long id;
    private Long productId;
    private Integer quantity;


}
