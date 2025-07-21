package com.example.fhirconverter.filters;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FhirNamePartsFilter extends Filter {
    
    public FhirNamePartsFilter() {
        super("fhir_name_parts");
    }
    
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (value == null) return null;
        
        String fullName = super.asString(value, context);
        String[] parts = fullName.trim().split("\\s+");
        
        Map<String, Object> name = new HashMap<>();
        List<String> given = new ArrayList<>();
        
        if (parts.length > 0) {
            // Last part is family name
            name.put("family", parts[parts.length - 1]);
            
            // All other parts are given names
            for (int i = 0; i < parts.length - 1; i++) {
                given.add(parts[i]);
            }
        }
        
        if (!given.isEmpty()) {
            name.put("given", given);
        }
        
        return name;
    }
}