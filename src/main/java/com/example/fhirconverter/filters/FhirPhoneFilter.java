package com.example.fhirconverter.filters;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FhirPhoneFilter extends Filter {
    
    public FhirPhoneFilter() {
        super("fhir_phone");
    }
    
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (value == null) return null;
        
        String phone = super.asString(value, context);
        String use = params.length > 0 ? super.asString(params[0], context) : "home";
        
        // Clean phone number
        String cleaned = phone.replaceAll("[^0-9+]", "");
        
        // Format US phone numbers
        if (cleaned.matches("1?\\d{10}")) {
            if (cleaned.startsWith("1")) {
                cleaned = cleaned.substring(1);
            }
            cleaned = String.format("(%s) %s-%s", 
                cleaned.substring(0, 3), 
                cleaned.substring(3, 6), 
                cleaned.substring(6));
        }
        
        Map<String, Object> telecom = new HashMap<>();
        telecom.put("system", "phone");
        telecom.put("value", cleaned);
        telecom.put("use", use);
        
        return telecom;
    }
}