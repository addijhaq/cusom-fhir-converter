package com.example.fhirconverter.model;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class TemplateMetadata {
    private String resourceType;
    private String version;
    private String description;
    private List<String> requiredFields;
    private List<String> optionalFields;
    private Instant lastModified;
}