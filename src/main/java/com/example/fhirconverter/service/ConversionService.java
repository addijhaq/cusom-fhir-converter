package com.example.fhirconverter.service;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.example.fhirconverter.model.ConversionRequest;
import com.example.fhirconverter.model.ConversionResponse;
import com.example.fhirconverter.model.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConversionService {
    
    private final TemplateService templateService;
    private final ValidationService validationService;
    private final FhirContext fhirContext;
    private final IParser fhirJsonParser;
    
    public ConversionResponse convert(ConversionRequest request) {
        try {
            // Load and render template
            String renderedJson = templateService.renderTemplate(
                request.getResourceType(), 
                request.getInputData()
            );
            
            // Debug: Log the raw rendered JSON
            log.debug("Raw rendered JSON from template:\n{}", renderedJson);
            
            // Parse to FHIR resource
            IBaseResource resource = fhirJsonParser.parseResource(renderedJson);
            
            // Validate if enabled
            if (request.isValidate()) {
                ValidationResult validationResult = validationService.validate(resource);
                if (!validationResult.isSuccessful()) {
                    return ConversionResponse.builder()
                        .success(false)
                        .errors(validationResult.getMessages())
                        .build();
                }
            }
            
            // Serialize back to JSON
            String outputJson = fhirJsonParser.encodeResourceToString(resource);
            
            return ConversionResponse.builder()
                .success(true)
                .resourceType(resource.fhirType())
                .resourceId(resource.getIdElement().getIdPart())
                .output(outputJson)
                .build();
                
        } catch (Exception e) {
            log.error("Conversion failed", e);
            return ConversionResponse.builder()
                .success(false)
                .errors(List.of(e.getMessage()))
                .build();
        }
    }
}