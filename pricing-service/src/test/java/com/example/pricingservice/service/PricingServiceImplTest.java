package com.example.pricingservice.service;


import com.example.pricingservice.dto.PriceDto;
import com.example.pricingservice.feignclient.ProductServiceFeignClient;
import com.example.pricingservice.model.Price;
import com.example.pricingservice.repository.PriceRepository;
import com.example.pricingservice.response.ApiResponse;
import com.example.pricingservice.service.serviceImpl.PricingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PricingServiceImplTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ProductServiceFeignClient productServiceFeignClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PricingServiceImpl pricingService;

    private PriceDto priceDto;
    private Price price;
    private ApiResponse productResponse;

    @BeforeEach
    void setUp() {
        priceDto = new PriceDto();
        priceDto.setProductId(1L);
        priceDto.setPrice(100.0);

        price = new Price();
        price.setId(1L);
        price.setProductId(1L);
        price.setPrice(100.0);
        price.setCreatedAt(LocalDateTime.now());

        // Ensure the product response has success set to true
        productResponse = ApiResponse.builder()
                .success(true)
                .message("Product found")
                .httpStatus(HttpStatus.OK.value())
                .build();
    }

    @Test
    void createOrUpdatePrice_Success() {
        // Mock the product service to return a successful response
        when(productServiceFeignClient.getProductById(anyLong())).thenReturn(productResponse);

        // Mock the model mapper and repository
        when(modelMapper.map(any(PriceDto.class), eq(Price.class))).thenReturn(price);
        when(priceRepository.save(any(Price.class))).thenReturn(price);
        when(modelMapper.map(any(Price.class), eq(PriceDto.class))).thenReturn(priceDto);

        // Call the method under test
        ApiResponse response = pricingService.createOrUpdatePrice(priceDto);

        // Assertions
        assertNotNull(response);
        assertTrue(response.isSuccess()); // Ensure success is true
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Price saved successfully", response.getMessage());

        // Verify interactions
        verify(productServiceFeignClient, times(1)).getProductById(anyLong());
        verify(priceRepository, times(1)).save(any(Price.class));
    }

    @Test
    void createOrUpdatePrice_ProductNotFound() {
        // Mock the product service to return a failure response
        when(productServiceFeignClient.getProductById(anyLong())).thenReturn(
                ApiResponse.failure("Product not found", UUID.randomUUID().toString(), HttpStatus.NOT_FOUND)
        );

        // Call the method under test
        ApiResponse response = pricingService.createOrUpdatePrice(priceDto);

        // Assertions
        assertNotNull(response);
        assertFalse(response.isSuccess()); // Ensure success is false
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("Product not found", response.getMessage());

        // Verify interactions
        verify(productServiceFeignClient, times(1)).getProductById(anyLong());
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void createOrUpdatePrice_Exception() {
        // Mock the product service to throw an exception
        when(productServiceFeignClient.getProductById(anyLong())).thenThrow(new RuntimeException("Feign Client Error"));

        // Call the method under test and expect an exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pricingService.createOrUpdatePrice(priceDto);
        });

        // Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Failed to save price", exception.getReason());

        // Verify interactions
        verify(productServiceFeignClient, times(1)).getProductById(anyLong());
        verify(priceRepository, never()).save(any(Price.class));
    }

    @Test
    void getPriceByProductId_Success() {
        // Mock the repository to return a price
        when(priceRepository.findByProductId(anyLong())).thenReturn(Optional.of(price));

        // Mock the model mapper
        when(modelMapper.map(any(Price.class), eq(PriceDto.class))).thenReturn(priceDto);

        // Call the method under test
        ApiResponse response = pricingService.getPriceByProductId(1L);

        // Assertions
        assertNotNull(response);
        assertTrue(response.isSuccess()); // Ensure success is true
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Price retrieved successfully", response.getMessage());

        // Verify interactions
        verify(priceRepository, times(1)).findByProductId(anyLong());
    }

    @Test
    void getPriceByProductId_NotFound() {
        // Mock the repository to return an empty optional
        when(priceRepository.findByProductId(anyLong())).thenReturn(Optional.empty());

        // Call the method under test
        ApiResponse response = pricingService.getPriceByProductId(1L);

        // Assertions
        assertNotNull(response);
        assertFalse(response.isSuccess()); // Ensure success is false
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("Price not found", response.getMessage());

        // Verify interactions
        verify(priceRepository, times(1)).findByProductId(anyLong());
    }

    @Test
    void getPriceByProductId_Exception() {
        // Mock the repository to throw an exception
        when(priceRepository.findByProductId(anyLong())).thenThrow(new RuntimeException("Database error"));

        // Call the method under test and expect an exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pricingService.getPriceByProductId(1L);
        });

        // Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Failed to retrieve price", exception.getReason());

        // Verify interactions
        verify(priceRepository, times(1)).findByProductId(anyLong());
    }
}