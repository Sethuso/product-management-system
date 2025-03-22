package com.example.inventoryservice.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.inventoryservice.dto.InventoryDto;
import com.example.inventoryservice.exception.ResourceNotFoundException;
import com.example.inventoryservice.feignclient.ProductServiceFeignClient;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import com.example.inventoryservice.request.InventoryRequest;
import com.example.inventoryservice.response.ApiResponse;
import com.example.inventoryservice.service.serviceImpl.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductServiceFeignClient productServiceFeignClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases for updateInventory method

    @Test
    public void testUpdateInventory_Success() {
        // Arrange
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setProductId(1L);
        inventoryRequest.setQuantity(10);

        ApiResponse productResponse = new ApiResponse();
        productResponse.setData(new Object()); // Mock product exists

        Inventory inventory = new Inventory();
        inventory.setProductId(1L);
        inventory.setQuantity(10);

        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setProductId(1L);
        inventoryDto.setQuantity(10);

        when(productServiceFeignClient.getProductById(1L, "INVENTORY-SERVICE")).thenReturn(productResponse);
        when(modelMapper.map(inventoryRequest, Inventory.class)).thenReturn(inventory);
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        when(modelMapper.map(inventory, InventoryDto.class)).thenReturn(inventoryDto);

        // Act
        ApiResponse response = inventoryService.updateInventory(inventoryRequest);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Inventory updated successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testUpdateInventory_ProductNotFound() {
        // Arrange
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setProductId(1L);

        when(productServiceFeignClient.getProductById(1L, "INVENTORY-SERVICE")).thenReturn(null);

        // Act
        ApiResponse response = inventoryService.updateInventory(inventoryRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("Product not found", response.getMessage());
    }

    @Test
    public void testUpdateInventory_Exception() {
        // Arrange
        InventoryRequest inventoryRequest = new InventoryRequest();
        inventoryRequest.setProductId(1L);

        when(productServiceFeignClient.getProductById(1L, "INVENTORY-SERVICE")).thenThrow(new RuntimeException("Feign Client Error"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> inventoryService.updateInventory(inventoryRequest));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Failed to update inventory", exception.getReason());
    }

    // Test cases for getInventoryByProductId method

    @Test
    public void testGetInventoryByProductId_Success() {
        // Arrange
        Long productId = 1L;
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(10);

        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setProductId(productId);
        inventoryDto.setQuantity(10);

        when(inventoryRepository.findByProductId(productId)).thenReturn(inventory);
        when(modelMapper.map(inventory, InventoryDto.class)).thenReturn(inventoryDto);

        // Act
        InventoryDto result = inventoryService.getInventoryByProductId(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(10, result.getQuantity());
    }

    @Test
    public void testGetInventoryByProductId_InventoryNotFound() {
        // Arrange
        Long productId = 1L;

        when(inventoryRepository.findByProductId(productId)).thenReturn(null);

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> inventoryService.getInventoryByProductId(productId));
        assertEquals("Inventory not found for Product ID: " + productId, exception.getMessage());
    }

    @Test
    public void testGetInventoryByProductId_Exception() {
        // Arrange
        Long productId = 1L;

        when(inventoryRepository.findByProductId(productId)).thenThrow(new RuntimeException("Database Error"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> inventoryService.getInventoryByProductId(productId));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Failed to fetch inventory"));
    }
}