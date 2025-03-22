package com.example.pricingservice.service;

import com.example.pricingservice.dto.PriceDto;
import com.example.pricingservice.model.Price;
import com.example.pricingservice.response.ApiResponse;

import java.util.List;

public interface PriceService {

    ApiResponse createOrUpdatePrice(PriceDto price);
    PriceDto getPriceByProductId(Long productId);
}
