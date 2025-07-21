# FHIR JSON Converter

A Spring Boot application that converts structured JSON data to FHIR R4 resources using Liquid templates with advanced conditional logic and custom filters.

## Features

- Convert JSON input to FHIR R4 resources
- Support for multiple resource types (Patient, Observation, MedicationRequest, Encounter, Bundle)
- Advanced Liquid template features:
  - Complex if/else conditions
  - For loops for arrays
  - Case/when statements
  - Variable assignments
  - Custom FHIR-specific filters
- Built-in FHIR validation
- Template caching for performance
- RESTful API endpoints

## Requirements

- Java 21
- Maven 3.6+

## Getting Started

1. Clone the repository
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on port 8080.

## API Usage

### Convert JSON to FHIR

**Endpoint:** `POST /api/v1/convert`

### Simple Patient Example
```json
{
  "resourceType": "Patient",
  "inputData": {
    "identifier_value": "12345",
    "family_name": "Doe",
    "given_names": ["John"],
    "gender": "male",
    "birth_date": "1990-01-01"
  },
  "validate": true
}
```

### Complex Patient with Multiple Identifiers, Names, and Addresses
```json
{
  "resourceType": "Patient",
  "inputData": {
    "ssn": "123456789",
    "mrn": "MRN001234",
    "drivers_license": "D123456789",
    "identifiers": [
      {
        "system": "http://hospital.example.org/ids",
        "value": "HOSP-12345",
        "type": "MR",
        "type_display": "Medical Record Number"
      }
    ],
    "names": [
      {
        "use": "official",
        "family": "Smith",
        "given": ["John", "Michael"],
        "prefix": ["Dr."],
        "suffix": ["Jr.", "MD"],
        "period": {
          "start": "01/01/2020"
        }
      },
      {
        "use": "nickname",
        "given": ["Johnny"]
      }
    ],
    "gender": "M",
    "birth_date": "03/15/1980",
    "deceased_date": "12/31/2023",
    "marital_status": "M",
    "phones": [
      {
        "number": "5551234567",
        "use": "home"
      },
      {
        "number": "(555) 987-6543",
        "use": "mobile"
      }
    ],
    "emails": [
      "john.smith@email.com",
      {
        "address": "dr.smith@hospital.org",
        "use": "work"
      }
    ],
    "addresses": [
      {
        "use": "home",
        "type": "physical",
        "lines": ["123 Main Street", "Apt 4B"],
        "city": "Boston",
        "state": "MA",
        "postal_code": "02101",
        "period": {
          "start": "01/01/2020"
        }
      }
    ],
    "contacts": [
      {
        "relationship": "Emergency Contact",
        "relationship_code": "C",
        "full_name": "Jane Marie Smith",
        "phone": "555-123-9999",
        "email": "jane.smith@email.com"
      }
    ],
    "preferred_language": "English",
    "languages": [
      {
        "code": "es",
        "display": "Spanish",
        "preferred": false
      }
    ],
    "race": {
      "code": "2106-3",
      "display": "White"
    },
    "ethnicity": {
      "code": "2186-5",
      "display": "Not Hispanic or Latino"
    },
    "birth_sex": "M"
  },
  "validate": true
}
```

### Multi-Component Observation (Blood Pressure)
```json
{
  "resourceType": "Observation",
  "inputData": {
    "status": "final",
    "categories": ["vital-signs"],
    "code": "85354-9",
    "code_display": "Blood pressure panel with all children optional",
    "patient_id": "patient-123",
    "effective_date": "03/15/2024 14:30",
    "components": [
      {
        "code": "8480-6",
        "display": "Systolic blood pressure",
        "value": 120,
        "unit": "mmHg",
        "interpretation": "N",
        "reference_range": {
          "low": 90,
          "high": 140,
          "unit": "mmHg",
          "unit_code": "mm[Hg]"
        }
      },
      {
        "code": "8462-4",
        "display": "Diastolic blood pressure",
        "value": 80,
        "unit": "mmHg",
        "interpretation": "N",
        "reference_range": {
          "low": 60,
          "high": 90,
          "unit": "mmHg",
          "unit_code": "mm[Hg]"
        }
      }
    ],
    "notes": [
      {
        "text": "Patient was sitting during measurement",
        "author": "Nurse Johnson",
        "time": "03/15/2024 14:32"
      }
    ],
    "body_site": "368209003",
    "body_site_display": "Right arm",
    "method": "37931006",
    "method_display": "Auscultation",
    "device_id": "device-bp-001"
  },
  "validate": true
}
```

### Transaction Bundle with Multiple Resources
```json
{
  "resourceType": "Bundle",
  "inputData": {
    "bundle_type": "transaction",
    "entries": [
      {
        "resource_type": "Patient",
        "method": "POST",
        "patient_id": "patient-new-001",
        "full_name": "Jane Elizabeth Doe",
        "gender": "F",
        "birth_date": "06/15/1985",
        "phone": "5559876543",
        "simple_address": "789 Oak Street, Seattle, WA, 98101"
      },
      {
        "resource_type": "Observation",
        "method": "POST",
        "status": "final",
        "code": "85354-9",
        "code_display": "Blood pressure panel",
        "patient_reference": "urn:uuid:patient-new-001",
        "components": [
          {
            "code": "8480-6",
            "display": "Systolic blood pressure",
            "value": 130,
            "unit": "mmHg"
          },
          {
            "code": "8462-4",
            "display": "Diastolic blood pressure",
            "value": 85,
            "unit": "mmHg"
          }
        ]
      },
      {
        "resource": {
          "resourceType": "Condition",
          "clinicalStatus": {
            "coding": [{
              "system": "http://terminology.hl7.org/CodeSystem/condition-clinical",
              "code": "active"
            }]
          },
          "code": {
            "coding": [{
              "system": "http://snomed.info/sct",
              "code": "38341003",
              "display": "Hypertension"
            }]
          },
          "subject": {
            "reference": "urn:uuid:patient-new-001"
          }
        },
        "method": "POST"
      }
    ]
  }
}
```

## Liquid Template Syntax Guide

### Understanding the Pipe (|) Delimiter

The pipe delimiter (`|`) is the core of Liquid's filter system. It takes the value on the left and passes it through the filter on the right, similar to Unix command pipelines.

**Basic Syntax:**
```liquid
{{ input_value | filter_name }}
{{ input_value | filter_name: parameter1, parameter2 }}
```

**How it works:**
1. The value before the pipe is the input
2. The filter name comes after the pipe
3. Filter parameters (if any) follow the filter name after a colon
4. Filters can be chained together

**Examples:**
```liquid
{{ "hello world" | upcase }}                    → "HELLO WORLD"
{{ "  trim me  " | strip }}                     → "trim me"
{{ birth_date | fhir_date: 'MM/dd/yyyy', 'yyyy-MM-dd' }}  → "1990-01-15"
```

### Filter Chaining

Multiple filters can be chained together. The output of one filter becomes the input of the next:

```liquid
{{ "  john doe  " | strip | capitalize | append: ", MD" }}
→ "John doe, MD"

{{ phone | fhir_phone: 'mobile' | json }}
→ {"system":"phone","value":"(555) 123-4567","use":"mobile"}
```

### Built-in Liquid Filters

Liquid comes with many built-in filters:

```liquid
{{ "hello" | upcase }}              → "HELLO"
{{ "HELLO" | downcase }}            → "hello"
{{ "hello world" | capitalize }}    → "Hello world"
{{ "hello" | append: " world" }}    → "hello world"
{{ "hello world" | prepend: "Say " }}  → "Say hello world"
{{ "hello world" | size }}          → 11
{{ "hello,world" | split: "," }}    → ["hello", "world"]
{{ items | first }}                 → first item in array
{{ items | last }}                  → last item in array
{{ 5 | plus: 3 }}                  → 8
{{ 10 | minus: 3 }}                → 7
{{ null | default: "N/A" }}        → "N/A"
```

## Custom FHIR Filters

The application includes custom filters specifically designed for FHIR transformations:

### fhir_identifier
Formats identifiers with appropriate systems based on type:

```liquid
{{ "123456789" | fhir_identifier: 'SSN' }}
→ {"system": "http://hl7.org/fhir/sid/us-ssn", "value": "123-45-6789"}

{{ "MRN001234" | fhir_identifier: 'MRN' }}
→ {"system": "urn:oid:1.2.3.4.5.6.7", "value": "MRN001234"}

{{ "D123456789" | fhir_identifier: 'DL' }}
→ {"system": "urn:oid:2.16.840.1.113883.4.3.25", "value": "D123456789"}
```

**How it works:**
- Takes an identifier value and type
- Automatically assigns the correct system URL
- For SSN, also formats the value with dashes

### fhir_name_parts
Parses full names into FHIR name structure:

```liquid
{{ "John Michael Doe Jr" | fhir_name_parts }}
→ {"family": "Jr", "given": ["John", "Michael", "Doe"]}

{{ "Mary Smith" | fhir_name_parts }}
→ {"family": "Smith", "given": ["Mary"]}
```

**How it works:**
- Splits the name by spaces
- Treats the last part as the family name
- All other parts become given names in order

### fhir_date
Converts date formats with flexible input/output formats:

```liquid
{{ "01/15/1990" | fhir_date: 'MM/dd/yyyy', 'yyyy-MM-dd' }}
→ "1990-01-15"

{{ "15-Jan-1990" | fhir_date: 'dd-MMM-yyyy', 'yyyy-MM-dd' }}
→ "1990-01-15"

{{ "1990-01-15T10:30:00" | fhir_date: 'yyyy-MM-dd\'T\'HH:mm:ss', 'yyyy-MM-dd' }}
→ "1990-01-15"

{{ birth_date | fhir_date: 'MM/dd/yyyy', 'yyyy' }}
→ "1990"  (year only precision)
```

**Parameters:**
1. Input format pattern (Java SimpleDateFormat)
2. Output format pattern

### fhir_phone
Formats phone numbers into FHIR ContactPoint structure:

```liquid
{{ "5551234567" | fhir_phone: 'home' }}
→ {"system": "phone", "value": "(555) 123-4567", "use": "home"}

{{ "+1-555-123-4567" | fhir_phone: 'mobile' }}
→ {"system": "phone", "value": "+1-555-123-4567", "use": "mobile"}

{{ "555.123.4567" | fhir_phone: 'work' }}
→ {"system": "phone", "value": "(555) 123-4567", "use": "work"}
```

**How it works:**
- Cleans the phone number (removes non-numeric characters except +)
- Formats US 10-digit numbers as (XXX) XXX-XXXX
- Preserves international format if it starts with +
- Adds the specified use (home, work, mobile, etc.)

### fhir_address
Parses comma-separated addresses into FHIR Address structure:

```liquid
{{ "123 Main St, Boston, MA, 02101" | fhir_address }}
→ {
    "line": ["123 Main St"], 
    "city": "Boston", 
    "state": "MA", 
    "postalCode": "02101", 
    "country": "US"
  }

{{ "456 Oak Ave, Apt 2B, Seattle, WA, 98101" | fhir_address }}
→ {
    "line": ["456 Oak Ave", "Apt 2B"], 
    "city": "Seattle", 
    "state": "WA", 
    "postalCode": "98101", 
    "country": "US"
  }
```

**How it works:**
- Splits address by commas
- First part becomes the street line
- Expects format: street, city, state, zip
- Always adds "US" as country

### fhir_code
Maps codes between different systems:

```liquid
{{ "M" | fhir_code: 'gender' }}        → "male"
{{ "F" | fhir_code: 'gender' }}        → "female"
{{ "O" | fhir_code: 'gender' }}        → "other"
{{ "U" | fhir_code: 'gender' }}        → "unknown"

{{ "S" | fhir_code: 'marital' }}       → "S"
{{ "M" | fhir_code: 'marital' }}       → "M"
{{ "D" | fhir_code: 'marital' }}       → "D"
```

**How it works:**
- Maintains internal mappings for different code systems
- Currently supports gender and marital status mappings
- Returns original value if no mapping exists

### fhir_unit
Converts common units to UCUM codes:

```liquid
{{ "mmHg" | fhir_unit }}
→ {"unit": "mmHg", "system": "http://unitsofmeasure.org", "code": "mm[Hg]"}

{{ "mg" | fhir_unit }}
→ {"unit": "mg", "system": "http://unitsofmeasure.org", "code": "mg"}

{{ "milliliter" | fhir_unit }}
→ {"unit": "milliliter", "system": "http://unitsofmeasure.org", "code": "mL"}
```

**How it works:**
- Maps common unit names to UCUM codes
- Returns a complete FHIR Quantity unit structure
- Preserves original unit for display

### fhir_reference
Builds FHIR reference strings:

```liquid
{{ "123" | fhir_reference: 'Patient' }}
→ "Patient/123"

{{ "Patient/123" | fhir_reference: 'Patient' }}
→ "Patient/123"  (already formatted)

{{ "http://example.org/Patient/123" | fhir_reference: 'Patient' }}
→ "http://example.org/Patient/123"  (preserves full URLs)
```

**How it works:**
- Combines resource type and ID
- Handles already-formatted references
- Preserves full URLs if provided

## Using Filters in Templates

### Basic Usage in Templates

```liquid
{
  "resourceType": "Patient",
  "identifier": [
    {% if ssn %}
    {{ ssn | fhir_identifier: 'SSN' | json }}
    {% endif %}
  ],
  "name": [
    {% if full_name %}
    {{ full_name | fhir_name_parts | json }}
    {% endif %}
  ],
  "birthDate": "{{ birth_date | fhir_date: 'MM/dd/yyyy', 'yyyy-MM-dd' }}",
  "telecom": [
    {{ phone | fhir_phone: 'home' | json }}
  ]
}
```

### Combining with Conditionals

```liquid
{% if gender %}
  {% comment %} First normalize the gender value {% endcomment %}
  {% assign gender_code = gender | downcase %}
  "gender": "{{ gender_code | fhir_code: 'gender' }}",
{% endif %}
```

### Using with Loops

```liquid
"telecom": [
  {% for phone_item in phones %}
    {% if phone_item.number %}
      {{ phone_item.number | fhir_phone: phone_item.use | json }}
    {% else %}
      {{ phone_item | fhir_phone: 'home' | json }}
    {% endif %}
    {% unless forloop.last %},{% endunless %}
  {% endfor %}
]
```

## Creating New Custom Filters

### Step 1: Create the Filter Class

Create a new class in `src/main/java/com/example/fhirconverter/filters/`:

```java
package com.example.fhirconverter.filters;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component  // This registers the filter with Spring
public class FhirMyCustomFilter extends Filter {
    
    public FhirMyCustomFilter() {
        super("fhir_my_custom");  // This is the filter name used in templates
    }
    
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        // Your filter logic here
        if (value == null) return null;
        
        // Convert input to string
        String input = super.asString(value, context);
        
        // Access parameters if provided
        String param1 = params.length > 0 ? super.asString(params[0], context) : "default";
        
        // Do your transformation
        String result = transformValue(input, param1);
        
        return result;
    }
    
    private String transformValue(String input, String param) {
        // Your transformation logic
        return input + "-" + param;
    }
}
```

### Step 2: Complex Filter Example - Temperature Converter

Here's a complete example of a temperature conversion filter:

```java
package com.example.fhirconverter.filters;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;
import java.text.DecimalFormat;

@Component
public class FhirTemperatureFilter extends Filter {
    
    private static final DecimalFormat df = new DecimalFormat("#.#");
    
    public FhirTemperatureFilter() {
        super("fhir_temperature");
    }
    
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (value == null) return null;
        
        // Parse the numeric value
        double temp;
        try {
            temp = Double.parseDouble(super.asString(value, context));
        } catch (NumberFormatException e) {
            return value; // Return original if not a number
        }
        
        // Get source and target units
        String fromUnit = params.length > 0 ? super.asString(params[0], context) : "F";
        String toUnit = params.length > 1 ? super.asString(params[1], context) : "C";
        
        // Convert to Celsius first (as base unit)
        double celsius = toCelsius(temp, fromUnit);
        
        // Convert from Celsius to target unit
        double result = fromCelsius(celsius, toUnit);
        
        // Return as FHIR Quantity
        Map<String, Object> quantity = new HashMap<>();
        quantity.put("value", Double.parseDouble(df.format(result)));
        quantity.put("unit", getUnitDisplay(toUnit));
        quantity.put("system", "http://unitsofmeasure.org");
        quantity.put("code", getUCUMCode(toUnit));
        
        return quantity;
    }
    
    private double toCelsius(double temp, String unit) {
        switch (unit.toUpperCase()) {
            case "F":
                return (temp - 32) * 5.0 / 9.0;
            case "K":
                return temp - 273.15;
            case "C":
            default:
                return temp;
        }
    }
    
    private double fromCelsius(double celsius, String unit) {
        switch (unit.toUpperCase()) {
            case "F":
                return celsius * 9.0 / 5.0 + 32;
            case "K":
                return celsius + 273.15;
            case "C":
            default:
                return celsius;
        }
    }
    
    private String getUnitDisplay(String unit) {
        switch (unit.toUpperCase()) {
            case "F":
                return "°F";
            case "K":
                return "K";
            case "C":
            default:
                return "°C";
        }
    }
    
    private String getUCUMCode(String unit) {
        switch (unit.toUpperCase()) {
            case "F":
                return "[degF]";
            case "K":
                return "K";
            case "C":
            default:
                return "Cel";
        }
    }
}
```

**Usage in templates:**
```liquid
{{ body_temp | fhir_temperature: 'F', 'C' }}
→ {"value": 37.0, "unit": "°C", "system": "http://unitsofmeasure.org", "code": "Cel"}

{{ 37 | fhir_temperature: 'C', 'F' }}
→ {"value": 98.6, "unit": "°F", "system": "http://unitsofmeasure.org", "code": "[degF]"}
```

### Step 3: Advanced Filter - Dosage Instruction Builder

Here's a complex filter that builds FHIR dosage instructions:

```java
package com.example.fhirconverter.filters;

import liqp.TemplateContext;
import liqp.filters.Filter;
import org.springframework.stereotype.Component;
import java.util.*;

@Component
public class FhirDosageFilter extends Filter {
    
    private static final Map<String, String> ROUTE_MAPPINGS = new HashMap<>();
    private static final Map<String, String> FREQUENCY_MAPPINGS = new HashMap<>();
    
    static {
        // Route mappings
        ROUTE_MAPPINGS.put("oral", "26643006");
        ROUTE_MAPPINGS.put("PO", "26643006");
        ROUTE_MAPPINGS.put("IV", "47625008");
        ROUTE_MAPPINGS.put("IM", "78421000");
        ROUTE_MAPPINGS.put("subcutaneous", "34206005");
        ROUTE_MAPPINGS.put("SC", "34206005");
        
        // Frequency mappings
        FREQUENCY_MAPPINGS.put("daily", "1");
        FREQUENCY_MAPPINGS.put("BID", "2");
        FREQUENCY_MAPPINGS.put("TID", "3");
        FREQUENCY_MAPPINGS.put("QID", "4");
        FREQUENCY_MAPPINGS.put("Q4H", "6");
        FREQUENCY_MAPPINGS.put("Q6H", "4");
        FREQUENCY_MAPPINGS.put("Q8H", "3");
        FREQUENCY_MAPPINGS.put("Q12H", "2");
    }
    
    public FhirDosageFilter() {
        super("fhir_dosage");
    }
    
    @Override
    public Object apply(Object value, TemplateContext context, Object... params) {
        if (value == null) return null;
        
        // Parse input - expects format: "2 tablets oral twice daily"
        String instruction = super.asString(value, context);
        
        Map<String, Object> dosage = new HashMap<>();
        
        // Parse the instruction
        DosageComponents components = parseInstruction(instruction);
        
        // Build sequence
        dosage.put("sequence", 1);
        
        // Build text
        dosage.put("text", instruction);
        
        // Build timing
        if (components.frequency != null) {
            Map<String, Object> timing = new HashMap<>();
            Map<String, Object> repeat = new HashMap<>();
            
            Integer freq = getFrequencyPerDay(components.frequency);
            if (freq != null) {
                repeat.put("frequency", freq);
                repeat.put("period", 1);
                repeat.put("periodUnit", "d");
            }
            
            timing.put("repeat", repeat);
            dosage.put("timing", timing);
        }
        
        // Build route
        if (components.route != null) {
            Map<String, Object> route = new HashMap<>();
            List<Map<String, Object>> coding = new ArrayList<>();
            Map<String, Object> routeCode = new HashMap<>();
            
            routeCode.put("system", "http://snomed.info/sct");
            routeCode.put("code", ROUTE_MAPPINGS.getOrDefault(components.route, "26643006"));
            routeCode.put("display", components.route);
            coding.add(routeCode);
            
            route.put("coding", coding);
            dosage.put("route", route);
        }
        
        // Build dose quantity
        if (components.dose != null && components.unit != null) {
            Map<String, Object> doseQuantity = new HashMap<>();
            doseQuantity.put("value", components.dose);
            doseQuantity.put("unit", components.unit);
            doseQuantity.put("system", "http://unitsofmeasure.org");
            doseQuantity.put("code", getUnitCode(components.unit));
            
            List<Map<String, Object>> doseAndRate = new ArrayList<>();
            Map<String, Object> doseAndRateElement = new HashMap<>();
            doseAndRateElement.put("doseQuantity", doseQuantity);
            doseAndRate.add(doseAndRateElement);
            
            dosage.put("doseAndRate", doseAndRate);
        }
        
        // Return as array since FHIR expects array of dosages
        List<Map<String, Object>> dosageList = new ArrayList<>();
        dosageList.add(dosage);
        return dosageList;
    }
    
    private DosageComponents parseInstruction(String instruction) {
        DosageComponents comp = new DosageComponents();
        String[] parts = instruction.toLowerCase().split("\\s+");
        
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            
            // Check if it's a number (dose)
            try {
                comp.dose = Double.parseDouble(part);
                // Next part might be unit
                if (i + 1 < parts.length) {
                    String nextPart = parts[i + 1];
                    if (isUnit(nextPart)) {
                        comp.unit = nextPart;
                        i++; // Skip next iteration
                    }
                }
            } catch (NumberFormatException e) {
                // Not a number, check other patterns
                
                // Check for route
                if (ROUTE_MAPPINGS.containsKey(part) || 
                    ROUTE_MAPPINGS.containsKey(part.toUpperCase())) {
                    comp.route = part;
                }
                
                // Check for frequency
                if (FREQUENCY_MAPPINGS.containsKey(part.toUpperCase()) ||
                    part.equals("daily") || part.equals("twice") || 
                    part.equals("three") || part.equals("four")) {
                    comp.frequency = part;
                    if (part.equals("twice")) comp.frequency = "BID";
                    if (part.equals("three")) comp.frequency = "TID";
                    if (part.equals("four")) comp.frequency = "QID";
                }
            }
        }
        
        return comp;
    }
    
    private boolean isUnit(String text) {
        return text.matches("tablets?|capsules?|ml|mg|units?|drops?|puffs?");
    }
    
    private Integer getFrequencyPerDay(String frequency) {
        String freq = FREQUENCY_MAPPINGS.get(frequency.toUpperCase());
        return freq != null ? Integer.parseInt(freq) : null;
    }
    
    private String getUnitCode(String unit) {
        // Simplified unit mapping
        switch (unit.toLowerCase()) {
            case "tablet":
            case "tablets":
                return "{tbl}";
            case "capsule":
            case "capsules":
                return "{capsule}";
            case "ml":
                return "mL";
            case "mg":
                return "mg";
            default:
                return "1";
        }
    }
    
    private static class DosageComponents {
        Double dose;
        String unit;
        String route;
        String frequency;
    }
}
```

**Usage in templates:**
```liquid
{{ "2 tablets oral twice daily" | fhir_dosage | json }}
→ [{
    "sequence": 1,
    "text": "2 tablets oral twice daily",
    "timing": {
      "repeat": {
        "frequency": 2,
        "period": 1,
        "periodUnit": "d"
      }
    },
    "route": {
      "coding": [{
        "system": "http://snomed.info/sct",
        "code": "26643006",
        "display": "oral"
      }]
    },
    "doseAndRate": [{
      "doseQuantity": {
        "value": 2.0,
        "unit": "tablets",
        "system": "http://unitsofmeasure.org",
        "code": "{tbl}"
      }
    }]
  }]

{{ "5 ml IV Q6H" | fhir_dosage | json }}
→ [{
    "sequence": 1,
    "text": "5 ml IV Q6H",
    "timing": {
      "repeat": {
        "frequency": 4,
        "period": 1,
        "periodUnit": "d"
      }
    },
    "route": {
      "coding": [{
        "system": "http://snomed.info/sct",
        "code": "47625008",
        "display": "iv"
      }]
    },
    "doseAndRate": [{
      "doseQuantity": {
        "value": 5.0,
        "unit": "ml",
        "system": "http://unitsofmeasure.org",
        "code": "mL"
      }
    }]
  }]
```

### Step 4: Register the Filter (Automatic with @Component)

When you use the `@Component` annotation, Spring automatically discovers and registers your filter. The filter is then available in all templates.

If you need manual registration or want to add filters dynamically, you can modify the `LiquidConfig` class:

```java
@Configuration
public class LiquidConfig {
    
    @Bean
    public TemplateParser liquidTemplateParser(List<Filter> customFilters) {
        TemplateParser.Builder builder = new TemplateParser.Builder()
            .withFlavor(Flavor.LIQP);
        
        // All @Component filters are automatically added here
        customFilters.forEach(builder::withFilter);
        
        // Or manually add a filter
        builder.withFilter(new FhirMyCustomFilter());
        
        return builder.build();
    }
}
```

## Best Practices for Custom Filters

1. **Null Safety**: Always check for null inputs
   ```java
   if (value == null) return null;
   ```

2. **Parameter Validation**: Check parameter count and provide defaults
   ```java
   String param = params.length > 0 ? super.asString(params[0], context) : "default";
   ```

3. **Type Conversion**: Use the parent class helper methods
   ```java
   String str = super.asString(value, context);
   Long num = super.asNumber(value, context);
   Boolean bool = super.asBoolean(value, context);
   ```

4. **Error Handling**: Return sensible defaults or original values on error
   ```java
   try {
       // transformation logic
   } catch (Exception e) {
       return value; // Return original on error
   }
   ```

5. **Naming Convention**: Use `fhir_` prefix for FHIR-specific filters
6. **Documentation**: Add JavaDoc comments explaining usage
7. **Testing**: Create unit tests for each filter

## Complex Template Examples

### Using Multiple Filters Together

```liquid
{% comment %} Parse a complex patient input {% endcomment %}
{
  "resourceType": "Patient",
  "identifier": [
    {% comment %} Format SSN with system and dashes {% endcomment %}
    {{ patient.ssn | fhir_identifier: 'SSN' | json }}
  ],
  "name": [
    {% comment %} Parse full name into parts {% endcomment %}
    {{ patient.full_name | strip | fhir_name_parts | json }}
  ],
  "gender": "{{ patient.gender | upcase | fhir_code: 'gender' }}",
  "birthDate": "{{ patient.dob | fhir_date: 'MM-dd-yyyy', 'yyyy-MM-dd' }}",
  "telecom": [
    {% comment %} Clean and format phone, then convert to JSON {% endcomment %}
    {{ patient.phone | strip | fhir_phone: 'home' | json }}
  ],
  "address": [
    {% comment %} Parse address string into structured format {% endcomment %}
    {{ patient.address | fhir_address | json }}
  ]
}
```

### Conditional Filter Application

```liquid
{% comment %} Apply different date formats based on input {% endcomment %}
{% if date_format == 'US' %}
  "date": "{{ input_date | fhir_date: 'MM/dd/yyyy', 'yyyy-MM-dd' }}"
{% elsif date_format == 'EU' %}
  "date": "{{ input_date | fhir_date: 'dd/MM/yyyy', 'yyyy-MM-dd' }}"
{% else %}
  "date": "{{ input_date }}"
{% endif %}
```

### Filter in Loops with Transformation

```liquid
"medicationRequest": [
  {% for med in medications %}
    {
      "medicationCodeableConcept": {
        "text": "{{ med.name }}"
      },
      "dosageInstruction": {{ med.instructions | fhir_dosage | json }},
      "dispenseRequest": {
        "quantity": {
          "value": {{ med.quantity }},
          "unit": "{{ med.unit }}",
          "system": "http://unitsofmeasure.org",
          "code": "{{ med.unit | fhir_unit | map: 'code' }}"
        }
      }
    }{% unless forloop.last %},{% endunless %}
  {% endfor %}
]
```

## Advanced Template Features

### Conditional Logic (if/elsif/else)
```liquid
{% if gender == "M" or gender == "male" %}
  "gender": "male",
{% elsif gender == "F" or gender == "female" %}
  "gender": "female",
{% else %}
  "gender": "unknown",
{% endif %}
```

### For Loops with Arrays
```liquid
"name": [
  {% for name in names %}
    {
      "use": "{{ name.use | default: 'official' }}",
      "family": "{{ name.family }}",
      "given": {{ name.given | json }}
    }{% unless forloop.last %},{% endunless %}
  {% endfor %}
]
```

### Case/When Statements
```liquid
{% case status %}
  {% when 'registered', 'preliminary', 'final', 'amended' %}
    "status": "{{ status }}",
  {% when 'cancelled', 'entered-in-error' %}
    "status": "{{ status }}",
  {% else %}
    "status": "final",
{% endcase %}
```

### Variable Assignment and Counters
```liquid
{% assign telecom_count = 0 %}
{% if phone %}
  {{ phone | fhir_phone: 'home' | json }}
  {% assign telecom_count = telecom_count | plus: 1 %}
{% endif %}
{% if mobile %}
  {% if telecom_count > 0 %},{% endif %}
  {{ mobile | fhir_phone: 'mobile' | json }}
{% endif %}
```

### Complex Array Processing
```liquid
{% comment %} Process array of identifiers with different types {% endcomment %}
"identifier": [
  {% assign id_count = 0 %}
  {% if ssn %}
    {{ ssn | fhir_identifier: 'SSN' | json }}
    {% assign id_count = id_count | plus: 1 %}
  {% endif %}
  {% for identifier in identifiers %}
    {% if id_count > 0 %},{% endif %}
    {
      "system": "{{ identifier.system }}",
      "value": "{{ identifier.value }}"
    }
    {% assign id_count = id_count | plus: 1 %}
  {% endfor %}
]
```

## Project Structure

```
src/
├── main/
│   ├── java/com/example/fhirconverter/
│   │   ├── config/          # Configuration classes
│   │   ├── controller/      # REST controllers
│   │   ├── service/         # Business logic
│   │   ├── model/           # Data models
│   │   ├── exception/       # Exception handling
│   │   ├── filters/         # Custom Liquid filters
│   │   └── util/            # Utility classes
│   └── resources/
│       ├── templates/       # Liquid templates
│       │   ├── patient.liquid
│       │   ├── observation.liquid
│       │   ├── bundle.liquid
│       │   └── ...
│       ├── examples/        # Example JSON files
│       └── application.yml  # Application configuration
└── test/                    # Test classes
```

## Configuration

Key configuration properties in `application.yml`:

```yaml
fhir:
  converter:
    templates-path: classpath:templates/
    validation:
      enabled: true
      strict: false
    output-format: json
    pretty-print: true
```

## Testing

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=ComplexConversionTest
```

## Performance Considerations

- Templates are cached using Caffeine cache
- FHIR context is reused across conversions
- Filters are singleton beans for efficiency

## License

This project is licensed under the MIT License.