package com.example.fhirconverter.service;

import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValidationService {
    
    private final FhirValidator fhirValidator;
    
    public com.example.fhirconverter.model.ValidationResult validate(IBaseResource resource) {
        log.debug("Validating {} resource", resource.fhirType());
        
        ValidationResult result = fhirValidator.validateWithResult(resource);
        
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> information = new ArrayList<>();
        List<String> messages = new ArrayList<>();
        
        result.getMessages().forEach(message -> {
            String msg = message.getLocationString() + ": " + message.getMessage();
            messages.add(msg);
            
            switch (message.getSeverity()) {
                case ERROR:
                case FATAL:
                    errors.add(msg);
                    break;
                case WARNING:
                    warnings.add(msg);
                    break;
                case INFORMATION:
                    information.add(msg);
                    break;
            }
        });
        
        return com.example.fhirconverter.model.ValidationResult.builder()
            .successful(result.isSuccessful())
            .messages(messages)
            .errors(errors)
            .warnings(warnings)
            .information(information)
            .build();
    }
}