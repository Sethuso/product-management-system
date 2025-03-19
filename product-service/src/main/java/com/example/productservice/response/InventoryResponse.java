package com.example.productservice.response;

import lombok.Data;

@Data
public class InventoryResponse {

    private Long id;
    private Long productId;
    private Integer quantity;

}
