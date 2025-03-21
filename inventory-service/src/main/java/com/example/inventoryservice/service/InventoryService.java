package com.example.inventoryservice.service;

import com.example.inventoryservice.request.InventoryRequest;
import com.example.inventoryservice.response.ApiResponse;

public interface InventoryService {
    ApiResponse updateInventory(InventoryRequest inventory);
    ApiResponse getInventoryByProductId(Long productId);
}
