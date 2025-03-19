package com.example.pricingservice.service;

import com.example.pricingservice.dto.PriceDto;
import com.example.pricingservice.model.Price;
import com.example.pricingservice.response.ApiResponse;

public interface PriceService {

    ApiResponse createOrUpdatePrice(PriceDto price);
    ApiResponse getPriceByProductId(Long productId);
    ApiResponse deletePrice(Long id);

}
