package com.example.pricingservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PriceDto {
    private Long id;
    @NotNull(message = "Product ID cannot be null")
    @Min(value = 1, message = "Product ID must be a positive number")
    private Long productId;

    @NotNull(message = "Price cannot be null")
    @Min(value = 0, message = "Price cannot be negative")
    private Double price;
}
