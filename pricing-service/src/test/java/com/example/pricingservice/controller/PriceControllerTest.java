package com.example.pricingservice.controller;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.pricingservice.dto.PriceDto;
import com.example.pricingservice.response.ApiResponse;
import com.example.pricingservice.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class PriceControllerTest {

    @Mock
    private PriceService priceService;

    @InjectMocks
    private PriceController priceController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases for createOrUpdatePrice endpoint

    @Test
    public void testCreateOrUpdatePrice_Success() {
        // Arrange
        PriceDto priceDto = new PriceDto();
        priceDto.setProductId(1L);
        priceDto.setPrice(100.0);

        ApiResponse serviceResponse = ApiResponse.success(priceDto, "Price saved successfully", UUID.randomUUID().toString(), HttpStatus.OK);

        when(priceService.createOrUpdatePrice(priceDto)).thenReturn(serviceResponse);

        // Act
        ApiResponse response = priceController.createOrUpdatePrice(priceDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Price successfully created/updated", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testCreateOrUpdatePrice_Exception() {
        // Arrange
        PriceDto priceDto = new PriceDto();
        priceDto.setProductId(1L);

        when(priceService.createOrUpdatePrice(priceDto)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse response = priceController.createOrUpdatePrice(priceDto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertEquals("Failed to create/update price", response.getMessage());
    }

    // Test cases for getPriceByProductId endpoint

    @Test
    public void testGetPriceByProductId_Success() {
        // Arrange
        Long productId = 1L;
        PriceDto priceDto = new PriceDto();
        priceDto.setProductId(productId);
        priceDto.setPrice(100.0);

        when(priceService.getPriceByProductId(productId)).thenReturn(priceDto);

        // Act
        ApiResponse<PriceDto> response = priceController.getPriceByProductId(productId, "PRODUCT-SERVICE");

        // Assert
        assertEquals(HttpStatus.OK, response.getHttpStatus());
        assertEquals("Price fetched successfully", response.getMessage());
        assertNotNull(response.getData());
        assertEquals(productId, response.getData().getProductId());
    }

    @Test
    public void testGetPriceByProductId_PriceNotFound() {
        // Arrange
        Long productId = 1L;

        when(priceService.getPriceByProductId(productId)).thenReturn(null);

        // Act
        ApiResponse<PriceDto> response = priceController.getPriceByProductId(productId, "PRODUCT-SERVICE");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getHttpStatus());
        assertEquals("Price not found for Product ID: " + productId, response.getMessage());
    }

    @Test
    public void testGetPriceByProductId_Exception() {
        // Arrange
        Long productId = 1L;

        when(priceService.getPriceByProductId(productId)).thenThrow(new RuntimeException("Service Error"));

        // Act
        ApiResponse<PriceDto> response = priceController.getPriceByProductId(productId, "PRODUCT-SERVICE");

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getHttpStatus());
        assertTrue(response.getMessage().contains("Failed to fetch price"));
    }

    @Test
    public void testGetPriceByProductId_Unauthorized() {
        // Arrange
        Long productId = 1L;

        // Act
        ApiResponse<PriceDto> response = priceController.getPriceByProductId(productId, "INVALID-SERVICE");

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
        assertEquals("Unauthorized request. Service-Name header is missing or incorrect.", response.getMessage());
    }

    @Test
    public void testGetPriceByProductId_MissingServiceNameHeader() {
        // Arrange
        Long productId = 1L;

        // Act
        ApiResponse<PriceDto> response = priceController.getPriceByProductId(productId, null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getHttpStatus());
        assertEquals("Unauthorized request. Service-Name header is missing or incorrect.", response.getMessage());
    }
}