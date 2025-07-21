package com.example.fhirconverter.filters;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FhirUnitFilter extends Filter {
    
    private static final Map<String, String> UNIT_MAPPINGS = Map.of(
        "mg", "mg",
        "milligram", "mg",
        "g", "g",
        "gram", "g",
        "ml", "mL",
        "milliliter", "mL",
        "mmHg", "mm[Hg]",
        "bpm", "/min"
    );
    
    public FhirUnitFilter() {
        super("fhir_unit");
    }
    
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (value == null) return null;
        
        String unit = super.asString(value, context);
        String ucumCode = UNIT_MAPPINGS.getOrDefault(unit, unit);
        
        Map<String, Object> quantity = new HashMap<>();
        quantity.put("unit", unit);
        quantity.put("system", "http://unitsofmeasure.org");
        quantity.put("code", ucumCode);
        
        return quantity;
    }
}