package com.example.productservice.response;

import lombok.Data;

@Data
public class PriceResponse {
    private Long id;
    private Long productId;
    private Double price;

}
