package com.example.fhirconverter.filters;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.springframework.stereotype.Component;

@Component
public class FhirReferenceFilter extends Filter {
    
    public FhirReferenceFilter() {
        super("fhir_reference");
    }
    
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (value == null) return null;
        
        String resourceType = params.length > 0 ? super.asString(params[0], context) : "Resource";
        String id = super.asString(value, context);
        
        // Handle different reference formats
        if (id.startsWith("http://") || id.startsWith("https://")) {
            return id; // Already a full URL
        } else if (id.contains("/")) {
            return id; // Already in ResourceType/id format
        } else {
            return resourceType + "/" + id;
        }
    }
}