package com.example.productservice.feignclient;

import com.example.productservice.response.ApiResponse;
import com.example.productservice.response.PriceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "PRICING-SERVICE")
public interface PricingServiceFeignClient {

    @GetMapping("/com/api/price-service/getProductId")
    ApiResponse<PriceResponse> getPriceByProductId(
            @RequestParam("productId") Long productId,
            @RequestHeader("Service-Name") String serviceName
    );
}

