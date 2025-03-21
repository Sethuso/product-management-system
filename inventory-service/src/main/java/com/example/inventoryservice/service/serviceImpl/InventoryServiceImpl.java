package com.example.inventoryservice.service.serviceImpl;

import com.example.inventoryservice.dto.InventoryDto;
import com.example.inventoryservice.feignclient.ProductServiceFeignClient;
import com.example.inventoryservice.model.Inventory;
import com.example.inventoryservice.repository.InventoryRepository;
import com.example.inventoryservice.request.InventoryRequest;
import com.example.inventoryservice.response.ApiResponse;
import com.example.inventoryservice.service.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductServiceFeignClient productServiceFeignClient;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public ApiResponse updateInventory(InventoryRequest inventoryDto) {
        String traceId = UUID.randomUUID().toString();
        try {
            log.info("[{}] Checking if product exists via Product Service for Product ID: {}", traceId, inventoryDto.getProductId());

            // Check if the product exists via Product Service
            ApiResponse productResponse = productServiceFeignClient.getProductById(inventoryDto.getProductId(),"INVENTORY-SERVICE");

            if (productResponse == null || productResponse.getData() == null) {
                log.error("[{}] Product not found for Product ID: {}", traceId, inventoryDto.getProductId());
                return ApiResponse.failure("Product not found", traceId, HttpStatus.NOT_FOUND);
            }

            // Convert DTO to entity
            Inventory inventory = modelMapper.map(inventoryDto, Inventory.class);
            // Save inventory to the database
            Inventory savedInventory = inventoryRepository.save(inventory);

            // Convert saved entity to DTO
            InventoryDto savedInventoryDto = modelMapper.map(savedInventory, InventoryDto.class);

            log.info("[{}] Inventory updated successfully for Product ID: {}", traceId, inventoryDto.getProductId());
            return ApiResponse.success(savedInventoryDto, "Inventory updated successfully", traceId, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("[{}] Error while updating inventory: {}", traceId, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to update inventory", ex);
        }
    }

    @Override
    public ApiResponse getInventoryByProductId(Long productId) {
        String traceId = UUID.randomUUID().toString();
        try {
            log.info("[{}] Fetching inventory for Product ID: {}", traceId, productId);

            // Fetch inventory from the database
            Optional<Inventory> inventoryOpt = inventoryRepository.findByProductId(productId);

            if (inventoryOpt.isEmpty()) {
                log.error("[{}] Inventory not found for Product ID: {}", traceId, productId);
                return ApiResponse.failure("Inventory not found", traceId, HttpStatus.NOT_FOUND);
            }

            // Convert entity to DTO
            InventoryDto inventoryDto = modelMapper.map(inventoryOpt.get(), InventoryDto.class);

            log.info("[{}] Inventory fetched successfully for Product ID: {}", traceId, productId);
            return ApiResponse.success(inventoryDto, "Inventory fetched successfully", traceId, HttpStatus.OK);

        } catch (Exception ex) {
            log.error("[{}] Error while fetching inventory: {}", traceId, ex.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to fetch inventory", ex);
        }
    }

}
