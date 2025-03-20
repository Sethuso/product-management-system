package com.example.inventoryservice.service;


import com.example.inventoryservice.dto.InventoryDto;
import com.example.inventoryservice.feignclient.ProductServiceFeignClient;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
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

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private ProductServiceFeignClient productServiceFeignClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUpdateInventory_Success() {
        // Arrange
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setProductId(1L);
        inventoryDto.setQuantity(10);

        Inventory inventory = new Inventory();
        inventory.setProductId(1L);
        inventory.setQuantity(10);

        ApiResponse productResponse = new ApiResponse();
        productResponse.setData(new Object()); // Simulate product exists

        when(productServiceFeignClient.getProductById(anyLong())).thenReturn(productResponse);
        when(modelMapper.map(inventoryDto, Inventory.class)).thenReturn(inventory);
        when(inventoryRepository.save(any(Inventory.class))).thenReturn(inventory);
        when(modelMapper.map(inventory, InventoryDto.class)).thenReturn(inventoryDto);

        // Act
        ApiResponse response = inventoryService.updateInventory(inventoryDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Inventory updated successfully", response.getMessage());
        verify(productServiceFeignClient, times(1)).getProductById(anyLong());
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    void testUpdateInventory_ProductNotFound() {
        // Arrange
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setProductId(1L);

        when(productServiceFeignClient.getProductById(anyLong())).thenReturn(null);

        // Act
        ApiResponse response = inventoryService.updateInventory(inventoryDto);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals("Product not found", response.getMessage());
        verify(productServiceFeignClient, times(1)).getProductById(anyLong());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void testUpdateInventory_ExceptionThrown() {
        // Arrange
        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setProductId(1L);

        when(productServiceFeignClient.getProductById(anyLong())).thenThrow(new RuntimeException("Feign Client Error"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            inventoryService.updateInventory(inventoryDto);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Failed to update inventory", exception.getReason());
        verify(productServiceFeignClient, times(1)).getProductById(anyLong());
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void testGetInventoryByProductId_Success() {
        // Arrange
        Long productId = 1L;
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setQuantity(10);

        InventoryDto inventoryDto = new InventoryDto();
        inventoryDto.setProductId(productId);
        inventoryDto.setQuantity(10);

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.of(inventory));
        when(modelMapper.map(inventory, InventoryDto.class)).thenReturn(inventoryDto);

        // Act
        ApiResponse response = inventoryService.getInventoryByProductId(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Inventory fetched successfully", response.getMessage());
        assertEquals(inventoryDto, response.getData());
        verify(inventoryRepository, times(1)).findByProductId(productId);
    }

    @Test
    void testGetInventoryByProductId_InventoryNotFound() {
        // Arrange
        Long productId = 1L;

        when(inventoryRepository.findByProductId(productId)).thenReturn(Optional.empty());

        // Act
        ApiResponse response = inventoryService.getInventoryByProductId(productId);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals("Inventory not found", response.getMessage());
        verify(inventoryRepository, times(1)).findByProductId(productId);
    }

    @Test
    void testGetInventoryByProductId_ExceptionThrown() {
        // Arrange
        Long productId = 1L;

        when(inventoryRepository.findByProductId(productId)).thenThrow(new RuntimeException("Database Error"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            inventoryService.getInventoryByProductId(productId);
        });

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Failed to fetch inventory", exception.getReason());
        verify(inventoryRepository, times(1)).findByProductId(productId);
    }
}