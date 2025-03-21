//package com.example.apigateway.filter;
//
//import com.example.apigateway.response.ApiResponse;
//import com.example.apigateway.util.JwtUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.util.UUID;
//
//@Component
//public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
//
//    @Autowired
//    private RouteValidator routeValidator;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    public AuthFilter() {
//        super(Config.class);
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            String requestPath = exchange.getRequest().getURI().getPath();
//            String traceId = UUID.randomUUID().toString(); // Generate a trace ID for logging
//
//            logger.info("Incoming request: {} | Trace ID: {}", requestPath, traceId);
//
//            if (routeValidator.isSecured.test(exchange.getRequest())) {
//                logger.info("Request is secured for path: {} | Trace ID: {}", requestPath, traceId);
//
//                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                    logger.error("AUTHORIZATION header is missing for path: {} | Trace ID: {}", requestPath, traceId);
//                    return handleErrorResponse(exchange, "AUTHORIZATION header is missing", traceId, HttpStatus.UNAUTHORIZED);
//                }
//
//                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                    logger.error("Invalid Authorization header format for path: {} | Trace ID: {}", requestPath, traceId);
//                    return handleErrorResponse(exchange, "Invalid Authorization header format", traceId, HttpStatus.BAD_REQUEST);
//                }
//
//                String token = authHeader.substring(7);
//
//                try {
//                    // Validate the token
//                    jwtUtil.validateToken(token);
//                    logger.info("Token validated successfully for path: {} | Trace ID: {}", requestPath, traceId);
//
//                    // Optionally, you can add custom headers to the request before forwarding it
//                    ServerHttpRequest modifiedRequest = exchange.getRequest()
//                            .mutate()
//                            .header("X-Trace-Id", traceId) // Add a custom header
//                            .build();
//
//                    // Forward the request with the modified headers
//                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
//                } catch (Exception e) {
//                    logger.error("Unauthorized access for path: {} | Error: {} | Trace ID: {}", requestPath, e.getMessage(), traceId);
//                    return handleErrorResponse(exchange, "Unauthorized access: " + e.getMessage(), traceId, HttpStatus.UNAUTHORIZED);
//                }
//            } else {
//                logger.info("Request is open for path: {} | Trace ID: {}", requestPath, traceId);
//                // Forward the request as-is for open endpoints
//                return chain.filter(exchange);
//            }
//        };
//    }
//
//    private Mono<Void> handleErrorResponse(ServerWebExchange exchange, String message, String traceId, HttpStatus status) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(status);
//        response.getHeaders().add("Content-Type", "application/json");
//
//        ApiResponse apiResponse = ApiResponse.failure(message, traceId, status);
//
//        byte[] responseBytes = apiResponse.toString().getBytes(StandardCharsets.UTF_8);
//        return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBytes)));
//    }
//
//    public static class Config {
//        // Configuration properties if needed in the future
//    }
//}package com.example.apigateway.filter;
//
//import com.example.apigateway.response.ApiResponse;
//import com.example.apigateway.util.JwtUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
//
//    // List of trusted internal services that can communicate without JWT
//    private static final List<String> TRUSTED_SERVICES = Arrays.asList(
//            "PRICING-SERVICE", "INVENTORY-SERVICE"
//    );
//
//    @Autowired
//    private RouteValidator routeValidator;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    public AuthFilter() {
//        super(Config.class);
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            String requestPath = exchange.getRequest().getURI().getPath();
//            String traceId = UUID.randomUUID().toString(); // Generate a unique trace ID for logging
//
//            logger.info("Incoming request: {} | Trace ID: {}", requestPath, traceId);
//
//            if (routeValidator.isSecured.test(exchange.getRequest())) {
//                logger.info("Request is secured for path: {} | Trace ID: {}", requestPath, traceId);
//
//                // Check if the request is from a trusted internal service
//                String serviceName = exchange.getRequest().getHeaders().getFirst("Service-Name");
//                if (serviceName != null && TRUSTED_SERVICES.contains(serviceName)) {
//                    logger.info("Request from trusted service: {} | Trace ID: {}", serviceName, traceId);
//                    return chain.filter(exchange);  // Allow the request without validation
//                }
//
//                // Check if Authorization header exists
//                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                    logger.error("AUTHORIZATION header is missing for path: {} | Trace ID: {}", requestPath, traceId);
//                    return handleErrorResponse(exchange, "AUTHORIZATION header is missing", traceId, HttpStatus.UNAUTHORIZED);
//                }
//
//                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                    logger.error("Invalid Authorization header format for path: {} | Trace ID: {}", requestPath, traceId);
//                    return handleErrorResponse(exchange, "Invalid Authorization header format", traceId, HttpStatus.BAD_REQUEST);
//                }
//
//                String token = authHeader.substring(7);
//
//                try {
//                    jwtUtil.validateToken(token);
//                    logger.info("Token validated successfully for path: {} | Trace ID: {}", requestPath, traceId);
//
//                    ServerHttpRequest modifiedRequest = exchange.getRequest()
//                            .mutate()
//                            .header("X-Trace-Id", traceId)
//                            .build();
//
//                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
//                } catch (Exception e) {
//                    logger.error("Unauthorized access for path: {} | Error: {} | Trace ID: {}", requestPath, e.getMessage(), traceId);
//                    return handleErrorResponse(exchange, "Unauthorized access: " + e.getMessage(), traceId, HttpStatus.UNAUTHORIZED);
//                }
//            } else {
//                logger.info("Request is open for path: {} | Trace ID: {}", requestPath, traceId);
//                return chain.filter(exchange);
//            }
//        };
//    }
//
//    private Mono<Void> handleErrorResponse(ServerWebExchange exchange, String message, String traceId, HttpStatus status) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(status);
//        response.getHeaders().add("Content-Type", "application/json");
//
//        ApiResponse apiResponse = ApiResponse.failure(message, traceId, status);
//        byte[] responseBytes = apiResponse.toString().getBytes(StandardCharsets.UTF_8);
//
//        return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBytes)));
//    }
//
//    public static class Config {
//        // Placeholder for future configuration properties
//    }
//}package com.example.apigateway.filter;
//
//import com.example.apigateway.response.ApiResponse;
//import com.example.apigateway.util.JwtUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.nio.charset.StandardCharsets;
//import java.util.Arrays;
//import java.util.List;
//import java.util.UUID;
//
//@Component
//public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
//
//    private static final List<String> TRUSTED_SERVICES = Arrays.asList(
//            "PRICING-SERVICE", "INVENTORY-SERVICE", "PRODUCT-SERVICE"
//    );
//
//    @Autowired
//    private RouteValidator routeValidator;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    public AuthFilter() {
//        super(Config.class);
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//        return (exchange, chain) -> {
//            String requestPath = exchange.getRequest().getURI().getPath();
//            String traceId = UUID.randomUUID().toString();
//
//            logger.info("Incoming request: {} | Trace ID: {}", requestPath, traceId);
//
//            if (routeValidator.isSecured.test(exchange.getRequest())) {
//                logger.info("Request is secured for path: {} | Trace ID: {}", requestPath, traceId);
//
//                String serviceName = exchange.getRequest().getHeaders().getFirst("Service-Name");
//                if (serviceName != null && TRUSTED_SERVICES.contains(serviceName)) {
//                    logger.info("Request from trusted service: {} | Trace ID: {}", serviceName, traceId);
//                    return chain.filter(exchange);
//                }
//
//                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
//                    logger.error("AUTHORIZATION header is missing for path: {} | Trace ID: {}", requestPath, traceId);
//                    return handleErrorResponse(exchange, "AUTHORIZATION header is missing", traceId, HttpStatus.UNAUTHORIZED);
//                }
//
//                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//
//                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                    logger.error("Invalid Authorization header format for path: {} | Trace ID: {}", requestPath, traceId);
//                    return handleErrorResponse(exchange, "Invalid Authorization header format", traceId, HttpStatus.BAD_REQUEST);
//                }
//
//                String token = authHeader.substring(7);
//
//                try {
//                    jwtUtil.validateToken(token);
//                    logger.info("Token validated successfully for path: {} | Trace ID: {}", requestPath, traceId);
//
//                    ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
//                            .header("X-Trace-Id", traceId)
//                            .header(HttpHeaders.AUTHORIZATION, authHeader);
//
//                    ServerHttpRequest modifiedRequest = requestBuilder.build();
//                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
//                } catch (Exception e) {
//                    logger.error("Unauthorized access for path: {} | Error: {} | Trace ID: {}", requestPath, e.getMessage(), traceId);
//                    return handleErrorResponse(exchange, "Unauthorized access: " + e.getMessage(), traceId, HttpStatus.UNAUTHORIZED);
//                }
//            } else {
//                logger.info("Request is open for path: {} | Trace ID: {}", requestPath, traceId);
//                return chain.filter(exchange);
//            }
//        };
//    }
//
//    private Mono<Void> handleErrorResponse(ServerWebExchange exchange, String message, String traceId, HttpStatus status) {
//        ServerHttpResponse response = exchange.getResponse();
//        response.setStatusCode(status);
//        response.getHeaders().add("Content-Type", "application/json");
//
//        ApiResponse apiResponse = ApiResponse.failure(message, traceId, status);
//        byte[] responseBytes = apiResponse.toString().getBytes(StandardCharsets.UTF_8);
//
//        return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBytes)));
//    }
//
//    public static class Config {
//    }
//}
package com.example.apigateway.filter;

import com.example.apigateway.response.ApiResponse;
import com.example.apigateway.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    private static final List<String> TRUSTED_SERVICES = Arrays.asList(
            "PRICING-SERVICE", "INVENTORY-SERVICE", "PRODUCT-SERVICE"
    );

    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String requestPath = exchange.getRequest().getURI().getPath();
            String traceId = UUID.randomUUID().toString();

            logger.info("Incoming request: {} | Trace ID: {}", requestPath, traceId);

            if (routeValidator.isSecured.test(exchange.getRequest())) {
                logger.info("Request is secured for path: {} | Trace ID: {}", requestPath, traceId);

                String serviceName = exchange.getRequest().getHeaders().getFirst("Service-Name");
                if (serviceName != null && TRUSTED_SERVICES.contains(serviceName)) {
                    logger.info("Request from trusted service: {} | Trace ID: {}", serviceName, traceId);
                    return chain.filter(exchange);
                }

                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    logger.error("AUTHORIZATION header is missing for path: {} | Trace ID: {}", requestPath, traceId);
                    return handleErrorResponse(exchange, "AUTHORIZATION header is missing", traceId, HttpStatus.UNAUTHORIZED);
                }

                String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

                if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                    logger.error("Invalid Authorization header format for path: {} | Trace ID: {}", requestPath, traceId);
                    return handleErrorResponse(exchange, "Invalid Authorization header format", traceId, HttpStatus.BAD_REQUEST);
                }

                String token = authHeader.substring(7);

                try {
                    jwtUtil.validateToken(token);
                    logger.info("Token validated successfully for path: {} | Trace ID: {}", requestPath, traceId);

                    ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate()
                            .header("X-Trace-Id", traceId)
                            .header(HttpHeaders.AUTHORIZATION, authHeader);

                    ServerHttpRequest modifiedRequest = requestBuilder.build();
                    return chain.filter(exchange.mutate().request(modifiedRequest).build());
                } catch (Exception e) {
                    logger.error("Unauthorized access for path: {} | Error: {} | Trace ID: {}", requestPath, e.getMessage(), traceId);
                    return handleErrorResponse(exchange, "Unauthorized access: " + e.getMessage(), traceId, HttpStatus.UNAUTHORIZED);
                }
            } else {
                logger.info("Request is open for path: {} | Trace ID: {}", requestPath, traceId);
                return chain.filter(exchange);
            }
        };
    }

    private Mono<Void> handleErrorResponse(ServerWebExchange exchange, String message, String traceId, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().add("Content-Type", "application/json");

        ApiResponse apiResponse = ApiResponse.failure(message, traceId, status);
        byte[] responseBytes = apiResponse.toString().getBytes(StandardCharsets.UTF_8);

        return response.writeWith(Mono.just(response.bufferFactory().wrap(responseBytes)));
    }

    public static class Config {
    }
}
