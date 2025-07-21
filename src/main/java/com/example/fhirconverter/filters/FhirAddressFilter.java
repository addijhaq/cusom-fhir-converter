package com.example.fhirconverter.filters;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FhirAddressFilter extends Filter {
    
    public FhirAddressFilter() {
        super("fhir_address");
    }
    
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (value == null) return null;
        
        if (value instanceof String) {
            // Parse single line address
            String addressLine = (String) value;
            String[] parts = addressLine.split(",");
            
            Map<String, Object> address = new HashMap<>();
            List<String> lines = new ArrayList<>();
            
            if (parts.length > 0) {
                lines.add(parts[0].trim());
            }
            
            address.put("line", lines);
            
            if (parts.length > 1) {
                address.put("city", parts[1].trim());
            }
            if (parts.length > 2) {
                address.put("state", parts[2].trim());
            }
            if (parts.length > 3) {
                address.put("postalCode", parts[3].trim());
            }
            
            address.put("country", "US");
            return address;
        }
        
        return value;
    }
}