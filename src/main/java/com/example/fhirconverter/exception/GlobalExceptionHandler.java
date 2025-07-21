package com.example.fhirconverter.exception;

import com.example.fhirconverter.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ConversionException.class)
    public ResponseEntity<ErrorResponse> handleConversionException(ConversionException e) {
        log.error("Conversion error", e);
        return ResponseEntity.badRequest()
            .body(ErrorResponse.builder()
                .message(e.getMessage())
                .timestamp(Instant.now())
                .build());
    }
    
    @ExceptionHandler(TemplateException.class)
    public ResponseEntity<ErrorResponse> handleTemplateException(TemplateException e) {
        log.error("Template error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.builder()
                .message("Template processing failed: " + e.getMessage())
                .timestamp(Instant.now())
                .build());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
            
        return ResponseEntity.badRequest()
            .body(ErrorResponse.builder()
                .message("Validation failed")
                .details(errors)
                .timestamp(Instant.now())
                .build());
    }
}