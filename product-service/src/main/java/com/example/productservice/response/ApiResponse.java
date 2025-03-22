package com.example.productservice.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {  // Use Generics for type safety
    private boolean success;
    private String message;
    private T data; // Type-safe data handling
    private String traceId;
    private int httpStatus;

    public ApiResponse(boolean b, String success, PriceResponse priceResponse, HttpStatus httpStatus) {
    }


    // Success Response
    public static <T> ApiResponse<T> success(T data, String message, String traceId, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .traceId(traceId)
                .httpStatus(status.value())
                .build();
    }

    // Failure Response
    public static <T> ApiResponse<T> failure(String message, String traceId, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .traceId(traceId)
                .httpStatus(status.value())
                .build();
    }

    // Validation Failure Response
    public static ApiResponse<Map<String, String>> validationFailure(Map<String, String> errors, String traceId, HttpStatus status) {
        return ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors)
                .traceId(traceId)
                .httpStatus(status.value())
                .build();
    }
}
