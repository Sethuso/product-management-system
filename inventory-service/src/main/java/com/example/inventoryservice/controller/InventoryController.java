package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryDto;
import com.example.inventoryservice.request.InventoryRequest;
import com.example.inventoryservice.response.ApiResponse;
import com.example.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/com/api/inventory-service")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/inventory")
    public ApiResponse updateInventory(@Valid @RequestBody InventoryRequest inventory) {
        String traceId = UUID.randomUUID().toString();
        logger.info("[{}] Request to update inventory for productId: {}", traceId, inventory.getProductId());

        try {
            ApiResponse response = inventoryService.updateInventory(inventory);
            logger.info("[{}] Inventory updated successfully for productId: {}", traceId, inventory.getProductId());
            return ApiResponse.success(response, "Inventory updated successfully", traceId, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("[{}] Error while updating inventory: {}", traceId, e.getMessage(), e);
            return ApiResponse.failure("Failed to update inventory", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getByProductId")
    public ApiResponse getInventoryByProductId(@RequestParam Long productId,
                                               @RequestHeader(value = "Service-Name", required = false) String serviceName) {
        String traceId = UUID.randomUUID().toString();
        logger.info("[{}] Request to fetch inventory for productId: {} from Service: {}", traceId, productId, serviceName);

        if ("PRODUCT-SERVICE".equalsIgnoreCase(serviceName)) {
            try {
                InventoryDto response = inventoryService.getInventoryByProductId(productId);
                logger.info("[{}] Successfully fetched inventory for productId: {}", traceId, productId);
                return ApiResponse.success(response, "Inventory fetched successfully", traceId, HttpStatus.OK);
            } catch (Exception e) {
                logger.error("[{}] Error while fetching inventory: {}", traceId, e.getMessage(), e);
                return ApiResponse.failure("Failed to fetch inventory", traceId, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        logger.warn("[{}] Unauthorized request. Service-Name header is missing or incorrect.", traceId);
        return ApiResponse.failure("Unauthorized request. Service-Name header is missing or incorrect.", traceId, HttpStatus.UNAUTHORIZED);
    }
}
