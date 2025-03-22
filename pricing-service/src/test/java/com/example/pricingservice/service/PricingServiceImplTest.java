package com.example.pricingservice.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.pricingservice.dto.PriceDto;
import com.example.pricingservice.feignclient.ProductServiceFeignClient;
import com.example.pricingservice.model.Price;
import com.example.pricingservice.repository.PriceRepository;
import com.example.pricingservice.response.ApiResponse;
import com.example.pricingservice.service.serviceImpl.PricingServiceImpl;
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

public class PricingServiceImplTest {

    @Mock
    private PriceRepository priceRepository;

    @Mock
    private ProductServiceFeignClient productServiceFeignClient;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private PricingServiceImpl pricingService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test cases for createOrUpdatePrice method

    @Test
    public void testCreateOrUpdatePrice_Success() {
        // Arrange
        PriceDto priceDto = new PriceDto();
        priceDto.setProductId(1L);
        priceDto.setPrice(100.0);

        ApiResponse productResponse = new ApiResponse();
        productResponse.setData(new Object()); // Mock product exists

        Price price = new Price();
        price.setProductId(1L);
        price.setPrice(100.0);

        when(productServiceFeignClient.getProductById(1L, "PRICING-SERVICE")).thenReturn(productResponse);
        when(modelMapper.map(priceDto, Price.class)).thenReturn(price);
        when(priceRepository.save(price)).thenReturn(price);
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        // Act
        ApiResponse response = pricingService.createOrUpdatePrice(priceDto);

        // Assert
        assertEquals(HttpStatus.OK.value(), response.getHttpStatus());
        assertEquals("Price saved successfully", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void testCreateOrUpdatePrice_ProductNotFound() {
        // Arrange
        PriceDto priceDto = new PriceDto();
        priceDto.setProductId(1L);

        when(productServiceFeignClient.getProductById(1L, "PRICING-SERVICE")).thenReturn(null);

        // Act
        ApiResponse response = pricingService.createOrUpdatePrice(priceDto);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getHttpStatus());
        assertEquals("Product not found", response.getMessage());
    }

    @Test
    public void testCreateOrUpdatePrice_Exception() {
        // Arrange
        PriceDto priceDto = new PriceDto();
        priceDto.setProductId(1L);

        when(productServiceFeignClient.getProductById(1L, "PRICING-SERVICE")).thenThrow(new RuntimeException("Feign Client Error"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> pricingService.createOrUpdatePrice(priceDto));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Failed to save price", exception.getReason());
    }

    // Test cases for getPriceByProductId method

    @Test
    public void testGetPriceByProductId_Success() {
        // Arrange
        Long productId = 1L;
        Price price = new Price();
        price.setProductId(productId);
        price.setPrice(100.0);

        PriceDto priceDto = new PriceDto();
        priceDto.setProductId(productId);
        priceDto.setPrice(100.0);

        when(priceRepository.findByProductId(productId)).thenReturn(Optional.of(price));
        when(modelMapper.map(price, PriceDto.class)).thenReturn(priceDto);

        // Act
        PriceDto result = pricingService.getPriceByProductId(productId);

        // Assert
        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(100.0, result.getPrice());
    }

    @Test
    public void testGetPriceByProductId_PriceNotFound() {
        // Arrange
        Long productId = 1L;

        when(priceRepository.findByProductId(productId)).thenReturn(Optional.empty());

        // Act
        PriceDto result = pricingService.getPriceByProductId(productId);

        // Assert
        assertNull(result);
    }

    @Test
    public void testGetPriceByProductId_Exception() {
        // Arrange
        Long productId = 1L;

        when(priceRepository.findByProductId(productId)).thenThrow(new RuntimeException("Database Error"));

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> pricingService.getPriceByProductId(productId));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatusCode());
        assertEquals("Failed to retrieve price", exception.getReason());
    }
}