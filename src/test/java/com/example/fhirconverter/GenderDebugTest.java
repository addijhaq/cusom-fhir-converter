package com.example.fhirconverter;

import com.example.fhirconverter.model.ConversionRequest;
import com.example.fhirconverter.model.ConversionResponse;
import com.example.fhirconverter.service.ConversionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenderDebugTest {
    
    @Autowired
    private ConversionService conversionService;
    
    @Test
    void testSimplePatientWithGender() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("identifier_value", "12345");
        inputData.put("family_name", "Doe");
        inputData.put("given_names", new String[]{"John"});
        inputData.put("gender", "M");
        inputData.put("birth_date", "01/15/1990");
        
        ConversionRequest request = new ConversionRequest();
        request.setResourceType("Patient");
        request.setInputData(inputData);
        request.setValidate(false);
        
        ConversionResponse response = conversionService.convert(request);
        
        System.out.println("Response success: " + response.isSuccess());
        System.out.println("Response errors: " + response.getErrors());
        System.out.println("Generated output:\n" + response.getOutput());
        
        assertTrue(response.isSuccess());
        assertNotNull(response.getOutput());
        assertTrue(response.getOutput().contains("\"gender\""));
    }
}