package com.example.fhirconverter.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Data
public class ConversionRequest {
    
    @NotBlank(message = "Resource type is required")
    private String resourceType;
    
    @NotNull(message = "Input data is required")
    private Map<String, Object> inputData;
    
    private boolean validate = false;
}