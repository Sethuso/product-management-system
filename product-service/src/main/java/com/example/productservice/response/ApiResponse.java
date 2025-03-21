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
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data; // Generic type for data
    private String traceId;
    private int httpStatus;

    public static <T> ApiResponse<T> success(T data, String message, String traceId, HttpStatus status) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .traceId(traceId)
                .httpStatus(status.value())
                .build();
    }

    public static ApiResponse<Void> failure(String message, String traceId, HttpStatus status) {
        return ApiResponse.<Void>builder()
                .success(false)
                .message(message)
                .traceId(traceId)
                .httpStatus(status.value())
                .build();
    }

    public static ApiResponse<Map<String, String>> validationFailure(Map<String, String> errors, String traceId, HttpStatus status) {
        return ApiResponse.<Map<String, String>>builder()
                .success(false)
                .message("Validation failed")
                .data(errors) // Include validation errors in the data field
                .traceId(traceId)
                .httpStatus(status.value())
                .build();
    }
}