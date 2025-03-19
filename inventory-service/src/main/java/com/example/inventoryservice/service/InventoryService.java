package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryDto;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.response.ApiResponse;

public interface InventoryService {
    ApiResponse updateInventory(InventoryDto inventory);
    ApiResponse getInventoryByProductId(Long productId);
}
