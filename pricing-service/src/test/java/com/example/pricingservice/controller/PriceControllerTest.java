package com.example.pricingservice.controller;

import com.example.pricingservice.dto.PriceDto;
import com.example.pricingservice.response.ApiResponse;
import com.example.pricingservice.service.PriceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PriceControllerTest {

    @Mock
    private PriceService priceService;

    @InjectMocks
    private PriceController priceController;

    private PriceDto priceDto;
    private ApiResponse apiResponse;

    @BeforeEach
    void setUp() {
        priceDto = new PriceDto();
        priceDto.setProductId(1L);
        priceDto.setPrice(100.0);

        apiResponse = ApiResponse.builder()
                .success(true)
                .message("Success")
                .httpStatus(HttpStatus.OK.value())
                .build();
    }

    @Test
    void createOrUpdatePrice_Success() {
        // Mock the service to return a successful response
        when(priceService.createOrUpdatePrice(any(PriceDto.class))).thenReturn(apiResponse);

        // Call the controller method
        ApiResponse response = priceController.createOrUpdatePrice(priceDto);

        // Assertions
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Success", response.getMessage());

        // Verify interactions
        verify(priceService, times(1)).createOrUpdatePrice(any(PriceDto.class));
    }

    @Test
    void getPriceByProductId_Success() {
        // Mock the service to return a successful response
        when(priceService.getPriceByProductId(anyLong())).thenReturn(apiResponse);

        // Call the controller method
        ApiResponse response = priceController.getPriceByProductId(1L);

        // Assertions
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Success", response.getMessage());

        // Verify interactions
        verify(priceService, times(1)).getPriceByProductId(anyLong());
    }
}