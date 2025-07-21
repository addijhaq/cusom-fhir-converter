package com.example.fhirconverter;

import com.example.fhirconverter.model.ConversionRequest;
import com.example.fhirconverter.model.ConversionResponse;
import com.example.fhirconverter.service.ConversionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ComplexConversionTest {
    
    @Autowired
    private ConversionService conversionService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testComplexPatientConversion() throws IOException {
        // Load complex patient example
        Map<String, Object> exampleData = objectMapper.readValue(
            new ClassPathResource("examples/patient-complex.json").getInputStream(),
            Map.class
        );
        
        ConversionRequest request = new ConversionRequest();
        request.setResourceType((String) exampleData.get("resourceType"));
        request.setInputData((Map<String, Object>) exampleData.get("inputData"));
        request.setValidate((Boolean) exampleData.get("validate"));
        
        ConversionResponse response = conversionService.convert(request);
        
        assertTrue(response.isSuccess());
        assertEquals("Patient", response.getResourceType());
        assertNotNull(response.getOutput());
        
        // Verify the output contains expected elements
        String output = response.getOutput();
        assertTrue(output.contains("\"system\": \"http://hl7.org/fhir/sid/us-ssn\"") || output.contains("\"system\":\"http://hl7.org/fhir/sid/us-ssn\""));
        assertTrue(output.contains("\"value\": \"123-45-6789\"") || output.contains("\"value\":\"123-45-6789\"")); // SSN formatted
        assertTrue(output.contains("\"family\": \"Smith\"") || output.contains("\"family\":\"Smith\""));
        assertTrue(output.contains("[ \"John\", \"Michael\" ]") || output.contains("[\"John\",\"Michael\"]"));
        assertTrue(output.contains("\"gender\": \"male\"") || output.contains("\"gender\":\"male\""));
        assertTrue(output.contains("\"birthDate\": \"1980-03-15\"") || output.contains("\"birthDate\":\"1980-03-15\""));
        assertTrue(output.contains("\"deceasedDateTime\": \"2023-12-31\"") || output.contains("\"deceasedDateTime\":\"2023-12-31\""));
        assertTrue(output.contains("\"maritalStatus\""));
        assertTrue(output.contains("\"telecom\""));
        assertTrue(output.contains("\"address\""));
        assertTrue(output.contains("\"contact\""));
        assertTrue(output.contains("\"communication\""));
        assertTrue(output.contains("\"extension\""));
    }
    
    @Test
    void testBloodPressureObservationConversion() throws IOException {
        // Load blood pressure example
        Map<String, Object> exampleData = objectMapper.readValue(
            new ClassPathResource("examples/observation-blood-pressure.json").getInputStream(),
            Map.class
        );
        
        ConversionRequest request = new ConversionRequest();
        request.setResourceType((String) exampleData.get("resourceType"));
        request.setInputData((Map<String, Object>) exampleData.get("inputData"));
        request.setValidate((Boolean) exampleData.get("validate"));
        
        ConversionResponse response = conversionService.convert(request);
        
        assertTrue(response.isSuccess());
        assertEquals("Observation", response.getResourceType());
        assertNotNull(response.getOutput());
        
        // Verify multi-component structure
        String output = response.getOutput();
        assertTrue(output.contains("\"code\": \"85354-9\"") || output.contains("\"code\":\"85354-9\""));
        assertTrue(output.contains("\"component\""));
        assertTrue(output.contains("\"code\": \"8480-6\"") || output.contains("\"code\":\"8480-6\"")); // Systolic
        assertTrue(output.contains("\"code\": \"8462-4\"") || output.contains("\"code\":\"8462-4\"")); // Diastolic
        assertTrue(output.contains("\"code\": \"8478-0\"") || output.contains("\"code\":\"8478-0\"")); // Mean BP
        assertTrue(output.contains("\"value\": 120") || output.contains("\"value\":120"));
        assertTrue(output.contains("\"value\": 80") || output.contains("\"value\":80"));
        assertTrue(output.contains("\"value\": 93") || output.contains("\"value\":93"));
        assertTrue(output.contains("\"unit\": \"mmHg\"") || output.contains("\"unit\":\"mmHg\""));
        assertTrue(output.contains("\"code\": \"mm[Hg]\"") || output.contains("\"code\":\"mm[Hg]\""));
        assertTrue(output.contains("\"interpretation\""));
        assertTrue(output.contains("\"referenceRange\""));
        assertTrue(output.contains("\"note\""));
        assertTrue(output.contains("\"bodySite\""));
        assertTrue(output.contains("\"method\""));
        assertTrue(output.contains("\"device\""));
    }
    
    @Test
    void testLabResultObservationConversion() throws IOException {
        // Load lab result example
        Map<String, Object> exampleData = objectMapper.readValue(
            new ClassPathResource("examples/observation-lab-result.json").getInputStream(),
            Map.class
        );
        
        ConversionRequest request = new ConversionRequest();
        request.setResourceType((String) exampleData.get("resourceType"));
        request.setInputData((Map<String, Object>) exampleData.get("inputData"));
        request.setValidate((Boolean) exampleData.get("validate"));
        
        ConversionResponse response = conversionService.convert(request);
        
        assertTrue(response.isSuccess());
        assertEquals("Observation", response.getResourceType());
        assertNotNull(response.getOutput());
        
        // Verify lab-specific elements
        String output = response.getOutput();
        assertTrue(output.contains("\"category\""));
        assertTrue(output.contains("\"code\": \"laboratory\"") || output.contains("\"code\":\"laboratory\""));
        assertTrue(output.contains("\"code\": \"2339-0\"") || output.contains("\"code\":\"2339-0\""));
        assertTrue(output.contains("\"effectiveDateTime\"") || output.contains("\"effectiveInstant\""));
        assertTrue(output.contains("\"issued\""));
        assertTrue(output.contains("\"valueQuantity\""));
        assertTrue(output.contains("\"value\": 95") || output.contains("\"value\":95"));
        assertTrue(output.contains("\"unit\": \"mg/dL\"") || output.contains("\"unit\":\"mg/dL\""));
        assertTrue(output.contains("\"referenceRange\""));
        assertTrue(output.contains("\"specimen\""));
    }
    
    @Test
    void testTransactionBundleConversion() throws IOException {
        // Load transaction bundle example
        Map<String, Object> exampleData = objectMapper.readValue(
            new ClassPathResource("examples/bundle-transaction.json").getInputStream(),
            Map.class
        );
        
        ConversionRequest request = new ConversionRequest();
        request.setResourceType((String) exampleData.get("resourceType"));
        request.setInputData((Map<String, Object>) exampleData.get("inputData"));
        request.setValidate((Boolean) exampleData.get("validate"));
        
        ConversionResponse response = conversionService.convert(request);
        
        assertTrue(response.isSuccess());
        assertEquals("Bundle", response.getResourceType());
        assertNotNull(response.getOutput());
        
        // Verify bundle structure
        String output = response.getOutput();
        assertTrue(output.contains("\"type\": \"transaction\"") || output.contains("\"type\":\"transaction\""));
        assertTrue(output.contains("\"entry\""));
        assertTrue(output.contains("\"fullUrl\": \"urn:uuid:") || output.contains("\"fullUrl\":\"urn:uuid:"));
        assertTrue(output.contains("\"request\""));
        assertTrue(output.contains("\"method\": \"POST\"") || output.contains("\"method\":\"POST\""));
        
        // Verify different resource types in bundle
        assertTrue(output.contains("\"resourceType\": \"Patient\"") || output.contains("\"resourceType\":\"Patient\""));
        assertTrue(output.contains("\"resourceType\": \"Observation\"") || output.contains("\"resourceType\":\"Observation\""));
        assertTrue(output.contains("\"resourceType\": \"Encounter\"") || output.contains("\"resourceType\":\"Encounter\""));
        assertTrue(output.contains("\"resourceType\": \"MedicationRequest\"") || output.contains("\"resourceType\":\"MedicationRequest\""));
        assertTrue(output.contains("\"resourceType\": \"Condition\"") || output.contains("\"resourceType\":\"Condition\""));
        
        // Verify references between resources
        assertTrue(output.contains("\"reference\": \"urn:uuid:patient-new-001\"") || output.contains("\"reference\":\"urn:uuid:patient-new-001\""));
        assertTrue(output.contains("\"reference\": \"urn:uuid:encounter-001\"") || output.contains("\"reference\":\"urn:uuid:encounter-001\""));
    }
    
    @Test
    void testPatientWithFilters() throws IOException {
        // Load patient with filters example
        Map<String, Object> exampleData = objectMapper.readValue(
            new ClassPathResource("examples/patient-with-filters.json").getInputStream(),
            Map.class
        );
        
        ConversionRequest request = new ConversionRequest();
        request.setResourceType((String) exampleData.get("resourceType"));
        request.setInputData((Map<String, Object>) exampleData.get("inputData"));
        request.setValidate((Boolean) exampleData.get("validate"));
        
        ConversionResponse response = conversionService.convert(request);
        
        assertTrue(response.isSuccess());
        assertEquals("Patient", response.getResourceType());
        assertNotNull(response.getOutput());
        
        // Verify filter transformations
        String output = response.getOutput();
        System.out.println("Generated output: " + output);
        
        // SSN filter should format the number
        assertTrue(output.contains("\"value\": \"123-45-6789\"") || output.contains("\"value\":\"123-45-6789\""));
        assertTrue(output.contains("\"system\": \"http://hl7.org/fhir/sid/us-ssn\"") || output.contains("\"system\":\"http://hl7.org/fhir/sid/us-ssn\""));
        
        // Name parts filter should parse full name
        assertTrue(output.contains("\"family\": \"Jr\"") || output.contains("\"family\":\"Jr\""));
        assertTrue(output.contains("[ \"John\", \"Michael\", \"Doe\" ]") || output.contains("[\"John\",\"Michael\",\"Doe\"]"));
        
        // Gender code filter
        assertTrue(output.contains("\"gender\": \"male\"") || output.contains("\"gender\":\"male\""));
        
        // Date filter should convert format
        assertTrue(output.contains("\"birthDate\": \"1990-01-15\"") || output.contains("\"birthDate\":\"1990-01-15\""));
        
        // Phone filter should format numbers
        assertTrue(output.contains("\"value\": \"(555) 123-4567\"") || output.contains("\"value\":\"(555) 123-4567\""));
        assertTrue(output.contains("\"value\": \"(555) 987-6543\"") || output.contains("\"value\":\"(555) 987-6543\""));
        
        // Address filter should parse comma-separated address
        assertTrue(output.contains("[ \"123 Main St\" ]") || output.contains("[\"123 Main St\"]"));
        assertTrue(output.contains("\"city\": \"Boston\"") || output.contains("\"city\":\"Boston\""));
        assertTrue(output.contains("\"state\": \"MA\"") || output.contains("\"state\":\"MA\""));
        assertTrue(output.contains("\"postalCode\": \"02101\"") || output.contains("\"postalCode\":\"02101\""));
    }
}