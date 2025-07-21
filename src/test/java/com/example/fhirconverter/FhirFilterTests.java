package com.example.fhirconverter;

import com.example.fhirconverter.filters.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FhirFilterTests {
    
    @Autowired
    private FhirDateFilter dateFilter;
    
    @Autowired
    private FhirPhoneFilter phoneFilter;
    
    @Autowired
    private FhirCodeFilter codeFilter;
    
    @Autowired
    private FhirNamePartsFilter namePartsFilter;
    
    @Test
    void testDateFilter() {
        // Test date format conversion
        String result = (String) dateFilter.apply("01/15/1990", null, "MM/dd/yyyy", "yyyy-MM-dd");
        assertEquals("1990-01-15", result);
        
        // Test with different precision
        result = (String) dateFilter.apply("01/15/1990", null, "MM/dd/yyyy", "yyyy-MM");
        assertEquals("1990-01", result);
        
        // Test null handling
        assertNull(dateFilter.apply(null, null));
    }
    
    @Test
    void testPhoneFilter() {
        // Test US phone formatting
        Map<String, Object> result = (Map<String, Object>) phoneFilter.apply("5551234567", null, "home");
        assertEquals("phone", result.get("system"));
        assertEquals("(555) 123-4567", result.get("value"));
        assertEquals("home", result.get("use"));
        
        // Test with existing formatting
        result = (Map<String, Object>) phoneFilter.apply("(555) 123-4567", null, "mobile");
        assertEquals("(555) 123-4567", result.get("value"));
        assertEquals("mobile", result.get("use"));
    }
    
    @Test
    void testCodeFilter() {
        // Test gender mapping
        assertEquals("male", codeFilter.apply("M", null, "gender"));
        assertEquals("female", codeFilter.apply("F", null, "gender"));
        assertEquals("other", codeFilter.apply("O", null, "gender"));
        assertEquals("unknown", codeFilter.apply("U", null, "gender"));
        
        // Test unmapped value
        assertEquals("X", codeFilter.apply("X", null, "gender"));
    }
    
    @Test
    void testNamePartsFilter() {
        // Test full name parsing
        Map<String, Object> result = (Map<String, Object>) namePartsFilter.apply("John Michael Doe", null);
        assertEquals("Doe", result.get("family"));
        List<String> given = (List<String>) result.get("given");
        assertEquals(2, given.size());
        assertEquals("John", given.get(0));
        assertEquals("Michael", given.get(1));
        
        // Test single name
        result = (Map<String, Object>) namePartsFilter.apply("Madonna", null);
        assertEquals("Madonna", result.get("family"));
        assertFalse(result.containsKey("given"));
    }
}