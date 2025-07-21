package com.example.fhirconverter.filters;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FhirCodeFilter extends Filter {
    
    private final Map<String, Map<String, String>> codeMappings;
    
    public FhirCodeFilter() {
        super("fhir_code");
        this.codeMappings = new HashMap<>();
        initializeCodeMappings();
    }
    
    private void initializeCodeMappings() {
        // Gender mappings
        Map<String, String> genderMap = new HashMap<>();
        genderMap.put("M", "male");
        genderMap.put("F", "female");
        genderMap.put("O", "other");
        genderMap.put("U", "unknown");
        codeMappings.put("gender", genderMap);
        
        // Marital status mappings
        Map<String, String> maritalMap = new HashMap<>();
        maritalMap.put("S", "S");  // Single
        maritalMap.put("M", "M");  // Married
        maritalMap.put("D", "D");  // Divorced
        maritalMap.put("W", "W");  // Widowed
        codeMappings.put("marital", maritalMap);
    }
    
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (value == null || params.length == 0) return value;
        
        String input = super.asString(value, context);
        String codeSystem = super.asString(params[0], context);
        
        Map<String, String> mapping = codeMappings.get(codeSystem);
        if (mapping != null && mapping.containsKey(input)) {
            return mapping.get(input);
        }
        
        return input;
    }
}