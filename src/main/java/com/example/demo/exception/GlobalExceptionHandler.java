package com.example.demo.exception;

import com.example.demo.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(final ResourceNotFoundException ex) {
        log.warn("[ExceptionHandler] - NOT_FOUND: message: {}", ex.getMessage());
        final ErrorResponse response = ErrorResponse.builder()
                .withStatus(404)
                .withError("Not Found")
                .withMessage(ex.getMessage())
                .withTimestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(final MethodArgumentNotValidException ex) {
        final String message = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        log.warn("[ExceptionHandler] - VALIDATION_FAILED: errors: {}", message);
        final ErrorResponse response = ErrorResponse.builder()
                .withStatus(400)
                .withError("Validation Error")
                .withMessage(message)
                .withTimestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(400).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(final IllegalArgumentException ex) {
        log.warn("[ExceptionHandler] - BAD_REQUEST: message: {}", ex.getMessage());
        final ErrorResponse response = ErrorResponse.builder()
                .withStatus(400)
                .withError("Bad Request")
                .withMessage(ex.getMessage())
                .withTimestamp(LocalDateTime.now())
                .build();
        return ResponseEntity.status(400).body(response);
    }
}