package com.example.pricingservice.service.serviceImpl;

import com.example.pricingservice.dto.PriceDto;
import com.example.pricingservice.feignclient.ProductServiceFeignClient;
import com.example.pricingservice.model.Price;
import com.example.pricingservice.repository.PriceRepository;
import com.example.pricingservice.response.ApiResponse;
import com.example.pricingservice.service.PriceService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PricingServiceImpl implements PriceService {

    @Autowired
    private PriceRepository priceRepository;

    @Autowired
    private ProductServiceFeignClient productServiceFeignClient;

    @Autowired
    private ModelMapper  modelMapper;


    @Override
    public ApiResponse createOrUpdatePrice(PriceDto priceDto) {
        String traceId = UUID.randomUUID().toString();
        try {
            log.info("[{}] Attempting to fetch product details for Product ID: {}", traceId, priceDto.getProductId());
            ApiResponse productResponse = productServiceFeignClient.getProductById(priceDto.getProductId());

            if (productResponse == null || productResponse.getData() == null) {
                log.error("[{}] Product not found for Product ID: {}", traceId, priceDto.getProductId());
                return ApiResponse.failure("Product not found", traceId, HttpStatus.NOT_FOUND);
            }

            // Convert DTO to entity
            Price price = modelMapper.map(priceDto, Price.class);


            // Save the price
            Price savedPrice = priceRepository.save(price);

            // Convert saved entity to DTO
            PriceDto savedPriceDto = modelMapper.map(savedPrice, PriceDto.class);

            log.info("[{}] Price saved successfully for Product ID: {}", traceId, priceDto.getProductId());
            return ApiResponse.success(savedPriceDto, "Price saved successfully", traceId, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("[{}] Error while creating or updating price: {}", traceId, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to save price", ex);
        }
    }

    @Override
    public ApiResponse getPriceByProductId(Long productId) {
        String traceId = UUID.randomUUID().toString();
        try {
            log.info("[{}] Attempting to fetch price details for Product ID: {}", traceId, productId);

            Optional<Price> priceOpt = priceRepository.findByProductId(productId);

            if (priceOpt.isEmpty()) {
                log.error("[{}] Price not found for Product ID: {}", traceId, productId);
                return ApiResponse.failure("Price not found", traceId, HttpStatus.NOT_FOUND);
            }

            Price price = priceOpt.get();

            // Convert entity to DTO
            PriceDto priceDto = modelMapper.map(price, PriceDto.class);

            log.info("[{}] Price retrieved successfully for Product ID: {}", traceId, productId);
            return ApiResponse.success(priceDto, "Price retrieved successfully", traceId, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("[{}] Error while fetching price details: {}", traceId, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to retrieve price", ex);
        }
    }


    @Override
    public ApiResponse deletePrice(Long id) {
        return null;
    }
}
