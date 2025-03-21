package com.example.productservice.config;

import com.example.productservice.dto.ProductDto;
import com.example.productservice.model.Product;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {


    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        // Custom mapping for Product to ProductDto
        modelMapper.addMappings(new PropertyMap<Product, ProductDto>() {
            @Override
            protected void configure() {
                // Map all fields except quantityStatus
                map().setId(source.getId());
                map().setName(source.getName());
                map().setBrand(source.getBrand());
                map().setDescription(source.getDescription());
                map().setCategory(source.getCategory().getName());
                map().setPrice(source.getPrice());

                using(ctx -> {
                    Integer quantity = ((Product) ctx.getSource()).getQuantity();
                    return quantity == 0 ? "Out of Stock" : "In Stock";
                }).map(source, destination.getQuantityStatus());

            }
        });

        return modelMapper;
    }
    private static final Logger logger = LoggerFactory.getLogger(Config.class);

    @Bean
    public ObjectMapper objectMapper() {
        logger.info("Configuring ObjectMapper to ignore unknown properties...");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignore unknown fields
        return mapper;
    }
}
