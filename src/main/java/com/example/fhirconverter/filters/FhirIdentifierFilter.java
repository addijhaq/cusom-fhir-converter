package com.example.fhirconverter.filters;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FhirIdentifierFilter extends Filter {
    
    private static final Map<String, String> SYSTEM_MAPPINGS = Map.of(
        "SSN", "http://hl7.org/fhir/sid/us-ssn",
        "MRN", "urn:oid:1.2.3.4.5.6.7",
        "DL", "urn:oid:2.16.840.1.113883.4.3.25",
        "PASSPORT", "http://hl7.org/fhir/sid/passport-USA"
    );
    
    public FhirIdentifierFilter() {
        super("fhir_identifier");
    }
    
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (value == null) return null;
        
        String identifierValue = super.asString(value, context);
        String type = params.length > 0 ? super.asString(params[0], context) : "MRN";
        
        Map<String, Object> identifier = new HashMap<>();
        identifier.put("value", identifierValue);
        identifier.put("system", SYSTEM_MAPPINGS.getOrDefault(type.toUpperCase(), "urn:oid:1.2.3.4.5"));
        
        // Format SSN
        if ("SSN".equalsIgnoreCase(type) && identifierValue.matches("\\d{9}")) {
            identifier.put("value", 
                identifierValue.substring(0, 3) + "-" + 
                identifierValue.substring(3, 5) + "-" + 
                identifierValue.substring(5));
        }
        
        return identifier;
    }
}