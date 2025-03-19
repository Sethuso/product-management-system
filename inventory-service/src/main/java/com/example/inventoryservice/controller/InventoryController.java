package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryDto;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.response.ApiResponse;
import com.example.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("com/api")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/inventory")
    public ApiResponse updateInventory(@RequestBody InventoryDto inventory) {
        return inventoryService.updateInventory(inventory);
    }

    @GetMapping("/getByProductId")
    public ApiResponse getInventoryByProductId(@RequestParam Long productId) {
        return inventoryService.getInventoryByProductId(productId);
    }
}
