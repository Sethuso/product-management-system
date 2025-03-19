package com.example.pricingservice.controller;

import com.example.pricingservice.dto.PriceDto;
import com.example.pricingservice.model.Price;
import com.example.pricingservice.response.ApiResponse;
import com.example.pricingservice.service.PriceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/com/api")
public class PriceController {

    private static final Logger logger = LoggerFactory.getLogger(PriceController.class);

    @Autowired
    private PriceService priceService;

    @PostMapping("price")
    public ApiResponse createOrUpdatePrice(@Valid @RequestBody PriceDto price) {
        logger.info("Request to create/update price for productId: {}", price.getProductId());
        return priceService.createOrUpdatePrice(price);
    }

    @GetMapping("getProductId")
    public ApiResponse getPriceByProductId(@RequestParam Long productId) {
        logger.info("Request to retrieve price for productId: {}", productId);
        return priceService.getPriceByProductId(productId);
    }

}
