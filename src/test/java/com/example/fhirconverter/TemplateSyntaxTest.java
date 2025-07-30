package com.example.fhirconverter;

import com.example.fhirconverter.model.ConversionRequest;
import com.example.fhirconverter.model.ConversionResponse;
import com.example.fhirconverter.service.ConversionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class TemplateSyntaxTest {

    @Autowired
    private ConversionService conversionService;

    @Autowired
    private ResourceLoader resourceLoader;

    /**
     * Test that all templates can be parsed without syntax errors
     */
    @ParameterizedTest(name = "Template {0} should parse without errors")
    @ValueSource(strings = {
        "Patient",
        "Patient-simple",
        "Patient-fixed",
        "Patient-advanced",
        "Bundle",
        "Encounter",
        "Medication-request",
        "Observation"
    })
    void testTemplateSyntax(String resourceType) {
        // Create minimal data required for the template
        Map<String, Object> inputData = createMinimalDataForTemplate(resourceType);

        // Create conversion request
        ConversionRequest request = new ConversionRequest();
        request.setResourceType(resourceType);
        request.setInputData(inputData);
        request.setValidate(false); // Skip validation, we only care about syntax

        // Attempt to convert
        ConversionResponse response = conversionService.convert(request);

        // Print debug info
        System.out.println("Testing template: " + resourceType);
        System.out.println("Response success: " + response.isSuccess());
        if (!response.isSuccess()) {
            System.out.println("Response errors: " + response.getErrors());
        }

        // Assert that conversion was successful (no syntax errors)
        assertTrue(response.isSuccess(),
            "Template " + resourceType + " has syntax errors: " +
            (response.getErrors() != null ? response.getErrors().toString() : "unknown error"));
    }

    /**
     * Create minimal data required for each template type
     */
    private Map<String, Object> createMinimalDataForTemplate(String resourceType) {
        Map<String, Object> data = new HashMap<>();

        // Common fields for all templates
        data.put("id", "test-id");

        // Template-specific required fields
        switch (resourceType.toLowerCase()) {
            case "patient":
            case "patient-simple":
            case "patient-fixed":
            case "patient-advanced":
                data.put("identifier_value", "12345");
                data.put("family_name", "Doe");
                data.put("given_names", new String[]{"John"});
                break;

            case "observation":
                data.put("code", "8480-6");
                data.put("code_system", "http://loinc.org");
                data.put("code_display", "Systolic blood pressure");
                data.put("value", 120);
                data.put("unit", "mm[Hg]");
                break;

            case "bundle":
                // Minimal bundle data
                data.put("entry_count", 0);
                break;

            case "encounter":
                // Minimal encounter data
                data.put("status", "finished");
                data.put("class", "ambulatory");
                break;

            case "medication-request":
                // Minimal medication request data
                data.put("status", "active");
                data.put("intent", "order");
                data.put("medication_code", "123456");
                data.put("medication_display", "Test Medication");
                break;
        }

        return data;
    }
}
