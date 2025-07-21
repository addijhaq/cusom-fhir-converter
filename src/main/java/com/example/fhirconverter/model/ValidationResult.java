package com.example.fhirconverter.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ValidationResult {
    private boolean successful;
    private List<String> messages;
    private List<String> errors;
    private List<String> warnings;
    private List<String> information;
}