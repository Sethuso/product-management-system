package com.example.pricingservice.controller;

import com.example.pricingservice.dto.PriceDto;
import com.example.pricingservice.response.ApiResponse;
import com.example.pricingservice.service.PriceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/com/api/price-service")
public class PriceController {

    private static final Logger logger = LoggerFactory.getLogger(PriceController.class);

    @Autowired
    private PriceService priceService;

    @PostMapping("/price")
    public ApiResponse createOrUpdatePrice(@Valid @RequestBody PriceDto price) {
        String traceId = UUID.randomUUID().toString();
        logger.info("[{}] Request to create/update price for productId: {}", traceId, price.getProductId());

        try {
            ApiResponse response = priceService.createOrUpdatePrice(price);
            logger.info("[{}] Price successfully created/updated for productId: {}", traceId, price.getProductId());
            return ApiResponse.success(response, "Price successfully created/updated", traceId, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[{}] Error while creating/updating price: {}", traceId, e.getMessage(), e);
            return ApiResponse.failure("Failed to create/update price", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getProductId")
    public ApiResponse<PriceDto> getPriceByProductId(
            @RequestParam("productId") Long productId,
            @RequestHeader(value = "Service-Name", required = false) String serviceName) {

        String traceId = UUID.randomUUID().toString();
        logger.info("[{}] Incoming request to fetch price for Product ID: {} from Service: {}", traceId, productId, serviceName);

        if ("PRODUCT-SERVICE".equalsIgnoreCase(serviceName)) {
            try {
                // Fetch the price from the service
                PriceDto priceDto = priceService.getPriceByProductId(productId);

                if (priceDto != null) {
                    logger.info("[{}] Successfully fetched price for Product ID: {}", traceId, productId);
                    return ApiResponse.success(priceDto, "Price fetched successfully", traceId, HttpStatus.OK);
                } else {
                    logger.warn("[{}] Price not found for Product ID: {}", traceId, productId);
                    return ApiResponse.failure("Price not found for Product ID: " + productId, traceId, HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                logger.error("[{}] Error while fetching price for Product ID: {}: {}", traceId, productId, e.getMessage(), e);
                return ApiResponse.failure("Failed to fetch price. Error: " + e.getMessage(), traceId, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        logger.warn("[{}] Unauthorized request. Service-Name header is missing or incorrect.", traceId);
        return ApiResponse.failure("Unauthorized request. Service-Name header is missing or incorrect.", traceId, HttpStatus.UNAUTHORIZED);
    }


    @GetMapping("/getPricesByProductIds")
    public ApiResponse getPricesByProductIds(
            @RequestParam("productIds") List<Long> productIds,
            @RequestHeader(value = "Service-Name", required = false) String serviceName) {

        String traceId = UUID.randomUUID().toString();
        logger.info("[{}] Incoming request to fetch prices for Product IDs: {} from Service: {}", traceId, productIds, serviceName);

        if ("PRODUCT-SERVICE".equalsIgnoreCase(serviceName)) {
            try {
                ApiResponse response = priceService.getPricesByProductIds(productIds);
                logger.info("[{}] Successfully fetched prices for Product IDs: {}", traceId, productIds);
                return ApiResponse.success(response.getData(), "Prices fetched successfully", traceId, HttpStatus.OK);
            } catch (Exception e) {
                logger.error("[{}] Error while fetching prices for Product IDs: {}: {}", traceId, productIds, e.getMessage(), e);
                return ApiResponse.failure("Failed to fetch prices", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        logger.warn("[{}] Unauthorized request. Service-Name header is missing or incorrect.", traceId);
        return ApiResponse.failure("Unauthorized request. Service-Name header is missing or incorrect.", traceId, HttpStatus.UNAUTHORIZED);
    }
}
