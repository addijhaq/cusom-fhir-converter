package com.example.fhirconverter.filters;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

@Component
public class FhirDateFilter extends Filter {
    
    public FhirDateFilter() {
        super("fhir_date");
    }
    
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (value == null) return null;
        
        String dateStr = super.asString(value, context);
        String inputFormat = params.length > 0 ? super.asString(params[0], context) : "MM/dd/yyyy";
        String outputFormat = params.length > 1 ? super.asString(params[1], context) : "yyyy-MM-dd";
        
        try {
            SimpleDateFormat input = new SimpleDateFormat(inputFormat);
            SimpleDateFormat output = new SimpleDateFormat(outputFormat);
            Date date = input.parse(dateStr);
            
            // FHIR allows different precision levels
            if (outputFormat.equals("yyyy")) {
                return output.format(date);
            } else if (outputFormat.equals("yyyy-MM")) {
                return output.format(date);
            } else {
                return output.format(date); // Full date
            }
        } catch (ParseException e) {
            // Try parsing as ISO date
            try {
                Instant instant = Instant.parse(dateStr);
                SimpleDateFormat output = new SimpleDateFormat(outputFormat);
                return output.format(Date.from(instant));
            } catch (Exception e2) {
                return dateStr; // Return original if parsing fails
            }
        }
    }
}