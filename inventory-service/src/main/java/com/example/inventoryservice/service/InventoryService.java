package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.InventoryDto;
import com.example.inventoryservice.request.InventoryRequest;
import com.example.inventoryservice.response.ApiResponse;

public interface InventoryService {
    ApiResponse updateInventory(InventoryRequest inventory);
    InventoryDto getInventoryByProductId(Long productId);
}
