package com.example.fhirconverter.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private String message;
    private List<String> details;
    private Instant timestamp;
    private String path;
}