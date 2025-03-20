package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.InventoryDto;
import com.example.inventoryservice.response.ApiResponse;
import com.example.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(inventoryController).build();
    }

    @Test
    void testUpdateInventory_Success() throws Exception {
        // Arrange
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setProductId(1L);
        inventoryDto.setQuantity(10);

        ApiResponse apiResponse = ApiResponse.success(inventoryDto, "Inventory updated successfully", "traceId", HttpStatus.OK);

        when(inventoryService.updateInventory(any(InventoryDto.class))).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(post("/com/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\": 1, \"quantity\": 10}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inventory updated successfully"))
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.quantity").value(10));
    }

    @Test
    void testUpdateInventory_ProductNotFound() throws Exception {
        // Arrange
        ApiResponse apiResponse = ApiResponse.failure("Product not found", "traceId", HttpStatus.NOT_FOUND);

        when(inventoryService.updateInventory(any(InventoryDto.class))).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(post("/com/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\": 1, \"quantity\": 10}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Product not found"));
    }

    @Test
    void testUpdateInventory_InternalServerError() throws Exception {
        // Arrange
        ApiResponse apiResponse = ApiResponse.failure("Failed to update inventory", "traceId", HttpStatus.INTERNAL_SERVER_ERROR);

        when(inventoryService.updateInventory(any(InventoryDto.class))).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(post("/com/api/inventory")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"productId\": 1, \"quantity\": 10}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to update inventory"));
    }

    @Test
    void testGetInventoryByProductId_Success() throws Exception {
        // Arrange
        Long productId = 1L;
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setProductId(productId);
        inventoryDto.setQuantity(10);

        ApiResponse apiResponse = ApiResponse.success(inventoryDto, "Inventory fetched successfully", "traceId", HttpStatus.OK);

        when(inventoryService.getInventoryByProductId(productId)).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(get("/com/api/getByProductId")
                        .param("productId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Inventory fetched successfully"))
                .andExpect(jsonPath("$.data.productId").value(1))
                .andExpect(jsonPath("$.data.quantity").value(10));
    }

    @Test
    void testGetInventoryByProductId_InventoryNotFound() throws Exception {
        // Arrange
        Long productId = 1L;
        ApiResponse apiResponse = ApiResponse.failure("Inventory not found", "traceId", HttpStatus.NOT_FOUND);

        when(inventoryService.getInventoryByProductId(productId)).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(get("/com/api/getByProductId")
                        .param("productId", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Inventory not found"));
    }

    @Test
    void testGetInventoryByProductId_InternalServerError() throws Exception {
        // Arrange
        Long productId = 1L;
        ApiResponse apiResponse = ApiResponse.failure("Failed to fetch inventory", "traceId", HttpStatus.INTERNAL_SERVER_ERROR);

        when(inventoryService.getInventoryByProductId(productId)).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(get("/com/api/getByProductId")
                        .param("productId", "1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Failed to fetch inventory"));
    }
}