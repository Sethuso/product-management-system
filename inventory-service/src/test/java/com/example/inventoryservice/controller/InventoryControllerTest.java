package com.example.inventoryservice.controller;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.inventoryservice.dto.InventoryDto;
import com.example.inventoryservice.request.InventoryRequest;
import com.example.inventoryservice.response.ApiResponse;
import com.example.inventoryservice.service.InventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class InventoryControllerTest {

    @Mock
    private InventoryService inventoryService;

    @InjectMocks
    private InventoryController inventoryController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases for updateInventory endpoint

    @Test
    public void testUpdateInventory_Success() {
        // Arrange
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setProductId(1L);
        inventoryRequest.setQuantity(10);

        ApiResponse serviceResponse = ApiResponse.success(new InventoryDto(), "Inventory updated successfully", UUID.randomUUID().toString(), HttpStatus.OK);

        when(inventoryService.updateInventory(inventoryRequest)).thenReturn(serviceResponse);

        // Act
        ApiResponse response = inventoryController.updateInventory(inventoryRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Inventory updated successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testUpdateInventory_Exception() {
        // Arrange
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setProductId(1L);

        when(inventoryService.updateInventory(inventoryRequest)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = inventoryController.updateInventory(inventoryRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertEquals("Failed to update inventory", response.getMessage());
    }

    // Test cases for getInventoryByProductId endpoint

    @Test
    public void testGetInventoryByProductId_Success() {
        // Arrange
        Long productId = 1L;
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setProductId(productId);
        inventoryDto.setQuantity(10);

        when(inventoryService.getInventoryByProductId(productId)).thenReturn(inventoryDto);

        // Act
        ApiResponse response = inventoryController.getInventoryByProductId(productId, "PRODUCT-SERVICE");

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Inventory fetched successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(productId, ((InventoryDto) response.getData()).getProductId());
    }

    @Test
    public void testGetInventoryByProductId_Exception() {
        // Arrange
        Long productId = 1L;

        when(inventoryService.getInventoryByProductId(productId)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = inventoryController.getInventoryByProductId(productId, "PRODUCT-SERVICE");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertEquals("Failed to fetch inventory", response.getMessage());
    }

    @Test
    public void testGetInventoryByProductId_Unauthorized() {
        // Arrange
        Long productId = 1L;

        // Act
        ApiResponse response = inventoryController.getInventoryByProductId(productId, "INVALID-SERVICE");

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
        assertEquals("Unauthorized request. Service-Name header is missing or incorrect.", response.getMessage());
    }

    @Test
    public void testGetInventoryByProductId_MissingServiceNameHeader() {
        // Arrange
        Long productId = 1L;

        // Act
        ApiResponse response = inventoryController.getInventoryByProductId(productId, null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
        assertEquals("Unauthorized request. Service-Name header is missing or incorrect.", response.getMessage());
    }
}