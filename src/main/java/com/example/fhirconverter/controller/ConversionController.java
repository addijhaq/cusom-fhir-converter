package com.example.fhirconverter.controller;

import com.example.fhirconverter.model.ConversionRequest;
import com.example.fhirconverter.model.ConversionResponse;
import com.example.fhirconverter.service.ConversionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/convert")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ConversionController {
    
    private final ConversionService conversionService;
    
    @PostMapping
    public ResponseEntity<ConversionResponse> convert(@Valid @RequestBody ConversionRequest request) {
        log.info("Converting {} resource", request.getResourceType());
        ConversionResponse response = conversionService.convert(request);
        
        return response.isSuccess() 
            ? ResponseEntity.ok(response)
            : ResponseEntity.badRequest().body(response);
    }
    
    @GetMapping("/templates")
    public ResponseEntity<List<String>> getAvailableTemplates() {
        // Implementation to list available templates
        return ResponseEntity.ok(Arrays.asList("Patient", "Observation", "MedicationRequest", "Encounter"));
    }
}