package com.example.fhirconverter.config;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.PerformanceOptionsEnum;
import ca.uhn.fhir.parser.IParser;
import ca.uhn.fhir.validation.FhirValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class FhirConfig {
    
    @Bean
    public FhirContext fhirContext() {
        FhirContext context = FhirContext.forR4();
        context.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);
        return context;
    }
    
    @Bean
    public IParser fhirJsonParser(FhirContext fhirContext, 
                                  @Value("${fhir.converter.pretty-print}") boolean prettyPrint) {
        IParser parser = fhirContext.newJsonParser();
        parser.setPrettyPrint(prettyPrint);
        parser.setOmitResourceId(false);
        return parser;
    }
    
    @Bean
    public FhirValidator fhirValidator(FhirContext fhirContext) {
        return fhirContext.newValidator();
    }
}