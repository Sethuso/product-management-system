package com.example.pricingservice.dto;

import lombok.Data;

@Data
public class PriceDto {
    private Long id;
    private Long productId;
    private Double price;

}
