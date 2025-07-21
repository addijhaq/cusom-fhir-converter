package com.example.fhirconverter.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ConversionResponse {
    private boolean success;
    private String resourceType;
    private String resourceId;
    private String output;
    private List<String> errors;
    private List<String> warnings;
}