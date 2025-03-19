package com.example.userservice.response;

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
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data; // Can hold either the response data or validation errors
    private String traceId;
    private int httpStatus;

    public static ApiResponse success(Object data, String message, String traceId, HttpStatus status) {
        return ApiResponse.builder()
                .success(true)
                .message(message)
                .data(data)
                .traceId(traceId)
                .httpStatus(status.value())
                .build();
    }

    public static ApiResponse failure(String message, String traceId, HttpStatus status) {
        return ApiResponse.builder()
                .success(false)
                .message(message)
                .traceId(traceId)
                .httpStatus(status.value())
                .build();
    }

    public static ApiResponse validationFailure(Map<String, String> errors, String traceId, HttpStatus status) {
        return ApiResponse.builder()
                .success(false)
                .message("Validation failed")
                .data(errors) // Include validation errors in the data field
                .traceId(traceId)
                .httpStatus(status.value())
                .build();
    }
}