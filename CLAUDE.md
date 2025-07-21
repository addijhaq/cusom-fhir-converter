## Advanced Features to Implement

1. **Batch Processing**: Support for converting multiple resources
2. **Template Hot Reload**: Watch template files for changes in development
3. **Custom Liquid Filters**: Add FHIR-specific filters for common transformations
4. **Mapping Configuration**: External mapping configuration files
5. **Audit Logging**: Track all conversions for compliance

## Liquid Template Features in liqp

### Control Flow Structures

1. **If/Elsif/Else Statements**
   - Basic conditionals: `{% if condition %}`
   - Multiple conditions: `{% elsif another_condition %}`
   - Default case: `{% else %}`
   - Comparison operators: `==`, `!=`, `<`, `>`, `<=`, `>=`
   - Boolean operators: `and`, `or`
   - Special checks: `contains`, `empty`, `blank`

2. **For Loops**
   - Iterate arrays: `{% for item in array %}`
   - Iterate hashes: `{% for key, value in hash %}`
   - Range loops: `{% for i in (1..10) %}`
   - Loop controls: `{% break %}`, `{% continue %}`
   - Loop metadata: `forloop.index`, `forloop.first`, `forloop.last`
   - Empty collections: `{% for item in array %}...{% else %}Empty{% endfor %}`

3. **Case/When Statements**
   - Switch-like behavior: `{% case variable %}`
   - Multiple values: `{% when 'val1', 'val2' %}`
   - Default case: `{% else %}`

4. **Unless Statements**
   - Reverse of if: `{% unless condition %}`

### Custom Filters

Custom filters in liqp allow you to transform data before it's inserted into templates:

1. **Creating Custom Filters**:
   - Extend the `Filter` class
   - Implement the `apply` method
   - Use `@Component` for Spring auto-discovery
   - Access parameters with `params` array

2. **Filter Examples**:
   - **Date Formatting**: `{{ date | fhir_date: 'MM/dd/yyyy', 'yyyy-MM-dd' }}`
   - **Code Mapping**: `{{ 'M' | fhir_code: 'gender' }}` → `"male"`
   - **Reference Building**: `{{ '123' | fhir_reference: 'Patient' }}` → `"Patient/123"`
   - **Phone Formatting**: `{{ '5551234567' | fhir_phone: 'home' }}`
   - **Unit Conversion**: `{{ 'mg' | fhir_unit }}` → UCUM code structure

3. **Chaining Filters**:
   ```liquid
   {{ value | filter1 | filter2: param | filter3 }}
   ```

### Advanced Template Examples

#### Complex Patient with Validation
```json
{
  "resourceType": "Patient",
  "inputData": {
    "identifier_value": "12345",
    "ssn": "123-45-6789",
    "medical_record_number": "MRN001",
    "names": [
      {
        "use": "official",
        "family": "Doe",
        "given": ["John", "James"],
        "prefix": ["Dr."]
      },
      {
        "use": "nickname",
        "given": ["Johnny"]
      }
    ],
    "gender": "M",
    "birth_date": "1990-01-01",
    "phone": "555-0123",
    "mobile": "555-9876",
    "email": "john.doe@example.com",
    "addresses": [
      {
        "use": "home",
        "lines": ["123 Main St", "Apt 4B"],
        "city": "Boston",
        "state": "MA",
        "postal_code": "02101"
      },
      {
        "use": "work",
        "lines": ["456 Corporate Blvd"],
        "city": "Cambridge",
        "state": "MA",
        "postal_code": "02139"
      }
    ]
  },
  "validate": true
}
```

#### Multi-Component Observation (Blood Pressure)
```json
{
  "resourceType": "Observation",
  "inputData": {
    "patient_id": "patient-123",
    "status": "final",
    "code": "85354-9",
    "code_display": "Blood pressure panel",
    "components": [
      {
        "code": "8480-6",
        "display": "Systolic blood pressure",
        "value": 120,
        "unit": "mmHg",
        "unit_code": "mm[Hg]"
      },
      {
        "code": "8462-4",
        "display": "Diastolic blood pressure",
        "value": 80,
        "unit": "mmHg",
        "unit_code": "mm[Hg]"
      }
    ],
    "reference_ranges": [
      {
        "low": 90,
        "high": 140,
        "unit": "mmHg",
        "unit_code": "mm[Hg]",
        "type": "normal"
      }
    ]
  }
}
```

#### Transaction Bundle with Multiple Resources
```json
{
  "resourceType": "Bundle",
  "inputData": {
    "bundle_type": "transaction",
    "entries": [
      {
        "resource_type": "Patient",
        "# JSON to FHIR R4 Conversion Tool - Implementation Guide

## Project Overview
Create a Spring Boot application that converts structured JSON data to FHIR R4 resources using the liqp liquid templating engine.

## Technical Stack
- **Java**: 21 (LTS)
- **Spring Boot**: 3.5.3
- **HAPI FHIR**: 8.0.0
- **liqp (Liquid Templates)**: 0.9.2.3
- **Build Tool**: Maven

## Project Structure
```
fhir-json-converter/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/fhirconverter/
│   │   │       ├── FhirConverterApplication.java
│   │   │       ├── config/
│   │   │       │   ├── FhirConfig.java
│   │   │       │   ├── LiquidConfig.java
│   │   │       │   └── WebConfig.java
│   │   │       ├── controller/
│   │   │       │   └── ConversionController.java
│   │   │       ├── service/
│   │   │       │   ├── ConversionService.java
│   │   │       │   ├── TemplateService.java
│   │   │       │   └── ValidationService.java
│   │   │       ├── model/
│   │   │       │   ├── ConversionRequest.java
│   │   │       │   ├── ConversionResponse.java
│   │   │       │   └── TemplateMetadata.java
│   │   │       ├── exception/
│   │   │       │   ├── ConversionException.java
│   │   │       │   ├── TemplateException.java
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       └── util/
│   │   │           ├── FhirUtils.java
│   │   │           └── JsonUtils.java
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── templates/
│   │       │   ├── patient.liquid
│   │       │   ├── observation.liquid
│   │       │   ├── medication-request.liquid
│   │       │   └── encounter.liquid
│   │       └── static/
│   └── test/
│       ├── java/
│       └── resources/
├── pom.xml
└── README.md
```

## Implementation Steps

### Step 1: Create the pom.xml
Create a Maven project with the following dependencies:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.3</version>
        <relativePath/>
    </parent>
    
    <groupId>com.example</groupId>
    <artifactId>fhir-json-converter</artifactId>
    <version>1.0.0</version>
    <name>FHIR JSON Converter</name>
    <description>JSON to FHIR R4 conversion tool using Liquid templates</description>
    
    <properties>
        <java.version>21</java.version>
        <hapi.fhir.version>8.0.0</hapi.fhir.version>
        <liqp.version>0.9.2.3</liqp.version>
    </properties>
    
    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        
        <!-- HAPI FHIR -->
        <dependency>
            <groupId>ca.uhn.hapi.fhir</groupId>
            <artifactId>hapi-fhir-structures-r4</artifactId>
            <version>${hapi.fhir.version}</version>
        </dependency>
        <dependency>
            <groupId>ca.uhn.hapi.fhir</groupId>
            <artifactId>hapi-fhir-validation</artifactId>
            <version>${hapi.fhir.version}</version>
        </dependency>
        <dependency>
            <groupId>ca.uhn.hapi.fhir</groupId>
            <artifactId>hapi-fhir-validation-resources-r4</artifactId>
            <version>${hapi.fhir.version}</version>
        </dependency>
        
        <!-- Liquid Template Engine -->
        <dependency>
            <groupId>nl.big-o</groupId>
            <artifactId>liqp</artifactId>
            <version>${liqp.version}</version>
        </dependency>
        
        <!-- Lombok for boilerplate reduction -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>
        
        <!-- JSON Processing -->
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
        </dependency>
        
        <!-- Caching -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-cache</artifactId>
        </dependency>
        <dependency>
            <groupId>com.github.ben-manes.caffeine</groupId>
            <artifactId>caffeine</artifactId>
        </dependency>
        
        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.mockito</groupId>
            <artifactId>mockito-junit-jupiter</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### Step 2: Configuration Classes

#### application.yml
```yaml
spring:
  application:
    name: fhir-json-converter
  
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=100,expireAfterWrite=10m

server:
  port: 8080
  error:
    include-message: always
    include-binding-errors: always

fhir:
  converter:
    templates-path: classpath:templates/
    validation:
      enabled: true
      strict: false
    output-format: json
    pretty-print: true

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics
  endpoint:
    health:
      show-details: always

logging:
  level:
    com.example.fhirconverter: DEBUG
    ca.uhn.fhir: INFO
```

### Step 3: Core Implementation Classes

#### FhirConfig.java
```java
@Configuration
@Slf4j
public class FhirConfig {
    
    @Bean
    public FhirContext fhirContext() {
        FhirContext context = FhirContext.forR4();
        context.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);
        return context;
    }
    
    @Bean
    public IParser fhirJsonParser(FhirContext fhirContext, 
                                  @Value("${fhir.converter.pretty-print}") boolean prettyPrint) {
        IParser parser = fhirContext.newJsonParser();
        parser.setPrettyPrint(prettyPrint);
        parser.setOmitResourceId(false);
        return parser;
    }
    
    @Bean
    public FhirValidator fhirValidator(FhirContext fhirContext) {
        return fhirContext.newValidator();
    }
}
```

#### LiquidConfig.java
```java
@Configuration
@EnableCaching
public class LiquidConfig {
    
    @Bean
    public TemplateParser liquidTemplateParser(List<Filter> customFilters) {
        TemplateParser.Builder builder = TemplateParser.newBuilder()
            .withFlavor(Flavor.LIQP)
            .withStrictVariables(false)
            .withLiquidStyleInclude(true)
            .withEvaluateInOutputTag(true);
        
        // Register all custom filters
        customFilters.forEach(builder::withFilter);
        
        return builder.build();
    }
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("templates");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES));
        return cacheManager;
    }
}
```

#### ConversionService.java
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class ConversionService {
    
    private final TemplateService templateService;
    private final ValidationService validationService;
    private final FhirContext fhirContext;
    private final IParser fhirJsonParser;
    
    public ConversionResponse convert(ConversionRequest request) {
        try {
            // Load and render template
            String renderedJson = templateService.renderTemplate(
                request.getResourceType(), 
                request.getInputData()
            );
            
            // Parse to FHIR resource
            IBaseResource resource = fhirJsonParser.parseResource(renderedJson);
            
            // Validate if enabled
            if (request.isValidate()) {
                ValidationResult validationResult = validationService.validate(resource);
                if (!validationResult.isSuccessful()) {
                    return ConversionResponse.builder()
                        .success(false)
                        .errors(validationResult.getMessages())
                        .build();
                }
            }
            
            // Serialize back to JSON
            String outputJson = fhirJsonParser.encodeResourceToString(resource);
            
            return ConversionResponse.builder()
                .success(true)
                .resourceType(resource.fhirType())
                .resourceId(resource.getIdElement().getIdPart())
                .output(outputJson)
                .build();
                
        } catch (Exception e) {
            log.error("Conversion failed", e);
            return ConversionResponse.builder()
                .success(false)
                .errors(List.of(e.getMessage()))
                .build();
        }
    }
}
```

#### TemplateService.java
```java
@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateService {
    
    private final TemplateParser templateParser;
    private final ResourceLoader resourceLoader;
    
    @Value("${fhir.converter.templates-path}")
    private String templatesPath;
    
    @Cacheable(value = "templates", key = "#resourceType")
    public Template loadTemplate(String resourceType) {
        try {
            String templatePath = templatesPath + resourceType.toLowerCase() + ".liquid";
            Resource resource = resourceLoader.getResource(templatePath);
            
            try (InputStream is = resource.getInputStream()) {
                String templateContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                return templateParser.parse(templateContent);
            }
        } catch (IOException e) {
            throw new TemplateException("Failed to load template for resource type: " + resourceType, e);
        }
    }
    
    public String renderTemplate(String resourceType, Map<String, Object> data) {
        Template template = loadTemplate(resourceType);
        
        // Add utility functions
        Map<String, Object> context = new HashMap<>(data);
        context.put("uuid", UUID.randomUUID().toString());
        context.put("now", Instant.now().toString());
        
        return template.render(context);
    }
    
    /**
     * Alternative method for rendering with instance-specific filters
     */
    public String renderTemplateWithFilters(String resourceType, Map<String, Object> data, List<Filter> additionalFilters) {
        try {
            String templatePath = templatesPath + resourceType.toLowerCase() + ".liquid";
            Resource resource = resourceLoader.getResource(templatePath);
            
            try (InputStream is = resource.getInputStream()) {
                String templateContent = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                
                // Create parser with additional filters
                TemplateParser.Builder builder = TemplateParser.newBuilder()
                    .withFlavor(Flavor.LIQP);
                
                additionalFilters.forEach(builder::withFilter);
                
                Template template = builder.build().parse(templateContent);
                return template.render(data);
            }
        } catch (IOException e) {
            throw new TemplateException("Failed to render template with filters", e);
        }
    }
}
```

### Step 3: Core Implementation Classes

#### FhirConfig.java
```java
@Configuration
@Slf4j
public class FhirConfig {
    
    @Bean
    public FhirContext fhirContext() {
        FhirContext context = FhirContext.forR4();
        context.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);
        return context;
    }
    
    @Bean
    public IParser fhirJsonParser(FhirContext fhirContext, 
                                  @Value("${fhir.converter.pretty-print}") boolean prettyPrint) {
        IParser parser = fhirContext.newJsonParser();
        parser.setPrettyPrint(prettyPrint);
        parser.setOmitResourceId(false);
        return parser;
    }
    
    @Bean
    public FhirValidator fhirValidator(FhirContext fhirContext) {
        return fhirContext.newValidator();
    }
}
```

#### LiquidConfig.java
```java
@Configuration
@EnableCaching
public class LiquidConfig {
    
    @Bean
    public TemplateParser liquidTemplateParser(List<Filter> customFilters) {
        TemplateParser.Builder builder = TemplateParser.newBuilder()
            .withFlavor(Flavor.LIQP)
            .withStrictVariables(false)
            .withLiquidStyleInclude(true)
            .withEvaluateInOutputTag(true);
        
        // Register all custom filters
        customFilters.forEach(builder::withFilter);
        
        return builder.build();
    }
    
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("templates");
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES));
        return cacheManager;
    }
}
```

### Step 4: Custom FHIR Filters

#### filters/FhirDateFilter.java
```java
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
```

#### filters/FhirCodeFilter.java
```java
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
```

#### filters/FhirReferenceFilter.java
```java
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
```

#### filters/FhirIdentifierFilter.java
```java
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
```

#### filters/FhirPhoneFilter.java
```java
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
```

#### filters/FhirUnitFilter.java
```java
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
```

#### filters/FhirNamePartsFilter.java
```java
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
```

#### filters/FhirAddressFilter.java
```java
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
```

### Step 5: Using Custom Filters in Templates

### Step 5: Using Custom Filters in Templates

#### patient.liquid with custom filters
```liquid
{
  "resourceType": "Patient",
  "id": "{{ id | default: uuid }}",
  "meta": {
    "profile": ["http://hl7.org/fhir/StructureDefinition/Patient"]
  },
  
  {% comment %} Use fhir_identifier filter for automatic system assignment {% endcomment %}
  "identifier": [
    {% if ssn %}
    {{ ssn | fhir_identifier: 'SSN' | json }},
    {% endif %}
    {% if mrn %}
    {{ mrn | fhir_identifier: 'MRN' | json }},
    {% endif %}
    {% if drivers_license %}
    {{ drivers_license | fhir_identifier: 'DL' | json }},
    {% endif %}
    {
      "system": "{{ identifier_system | default: 'urn:oid:1.2.3.4.5' }}",
      "value": "{{ identifier_value }}"
    }
  ],
  
  "active": {{ active | default: true }},
  
  {% comment %} Handle name parsing {% endcomment %}
  "name": [
    {% if full_name %}
      {{ full_name | fhir_name_parts | json }}
    {% elsif names %}
      {% for name in names %}
        {
          "use": "{{ name.use | default: 'official' }}",
          "family": "{{ name.family }}",
          "given": {{ name.given | json }}
        }{% unless forloop.last %},{% endunless %}
      {% endfor %}
    {% else %}
      {
        "use": "official",
        "family": "{{ family_name }}",
        "given": {{ given_names | json }}
      }
    {% endif %}
  ],
  
  {% comment %} Use fhir_code filter for gender mapping {% endcomment %}
  "gender": "{{ gender | fhir_code: 'gender' }}",
  
  {% comment %} Use fhir_date filter for date formatting {% endcomment %}
  "birthDate": "{{ birth_date | fhir_date: 'MM/dd/yyyy', 'yyyy-MM-dd' }}",
  
  {% comment %} Use fhir_phone filter for phone formatting {% endcomment %}
  {% if phone or mobile or work_phone %}
  "telecom": [
    {% if phone %}
      {{ phone | fhir_phone: 'home' | json }}
    {% endif %}
    {% if mobile %}
      {% if phone %},{% endif %}
      {{ mobile | fhir_phone: 'mobile' | json }}
    {% endif %}
    {% if work_phone %}
      {% if phone or mobile %},{% endif %}
      {{ work_phone | fhir_phone: 'work' | json }}
    {% endif %}
  ],
  {% endif %}
  
  {% comment %} Use fhir_address filter for address parsing {% endcomment %}
  {% if simple_address %}
  "address": [
    {{ simple_address | fhir_address | json }}
  ]
  {% elsif addresses %}
  "address": {{ addresses | json }}
  {% endif %}
}
```

#### observation.liquid with custom filters
```liquid
{
  "resourceType": "Observation",
  "id": "{{ id | default: uuid }}",
  "status": "{{ status | default: 'final' }}",
  
  "code": {
    "coding": [
      {
        "system": "{{ code_system | default: 'http://loinc.org' }}",
        "code": "{{ code }}",
        "display": "{{ code_display }}"
      }
    ]
  },
  
  {% comment %} Use fhir_reference filter {% endcomment %}
  "subject": {
    "reference": "{{ patient_id | fhir_reference: 'Patient' }}"
  },
  
  {% comment %} Use fhir_date filter for datetime {% endcomment %}
  "effectiveDateTime": "{{ effective_date | fhir_date: 'MM/dd/yyyy HH:mm', 'yyyy-MM-dd\'T\'HH:mm:ssXXX' | default: now }}",
  
  {% if value and unit %}
    {% comment %} Use fhir_unit filter for UCUM codes {% endcomment %}
    {% assign unit_info = unit | fhir_unit %}
    "valueQuantity": {
      "value": {{ value }},
      "unit": "{{ unit_info.unit }}",
      "system": "{{ unit_info.system }}",
      "code": "{{ unit_info.code }}"
    }
  {% elsif value_string %}
    "valueString": "{{ value_string }}"
  {% endif %}
}
```

### Step 6: Example Liquid Templates
```liquid
{
  "resourceType": "Patient",
  "id": "{{ id | default: uuid }}",
  "meta": {
    "profile": ["http://hl7.org/fhir/StructureDefinition/Patient"]
  },
  
  {% comment %} Multiple identifiers with conditional logic {% endcomment %}
  "identifier": [
    {
      "system": "{{ identifier_system | default: 'urn:oid:1.2.3.4.5' }}",
      "value": "{{ identifier_value }}"
    }
    {% if ssn %}
    ,{
      "system": "http://hl7.org/fhir/sid/us-ssn",
      "value": "{{ ssn }}"
    }
    {% endif %}
    {% if medical_record_number %}
    ,{
      "system": "{{ mrn_system | default: 'urn:oid:1.2.3.4.5.6.7' }}",
      "value": "{{ medical_record_number }}",
      "type": {
        "coding": [{
          "system": "http://terminology.hl7.org/CodeSystem/v2-0203",
          "code": "MR",
          "display": "Medical Record Number"
        }]
      }
    }
    {% endif %}
  ],
  
  "active": {{ active | default: true }},
  
  {% comment %} Handle multiple names with for loop {% endcomment %}
  "name": [
    {% if names %}
      {% for name in names %}
        {
          "use": "{{ name.use | default: 'official' }}",
          "family": "{{ name.family }}",
          "given": {{ name.given | json }}
          {% if name.prefix %},
          "prefix": {{ name.prefix | json }}
          {% endif %}
          {% if name.suffix %},
          "suffix": {{ name.suffix | json }}
          {% endif %}
        }{% unless forloop.last %},{% endunless %}
      {% endfor %}
    {% else %}
      {
        "use": "official",
        "family": "{{ family_name }}",
        "given": {{ given_names | json }}
      }
    {% endif %}
  ],
  
  {% comment %} Gender with validation {% endcomment %}
  {% case gender | downcase %}
    {% when 'male', 'm' %}
      "gender": "male",
    {% when 'female', 'f' %}
      "gender": "female",
    {% when 'other', 'o' %}
      "gender": "other",
    {% else %}
      "gender": "unknown",
  {% endcase %}
  
  "birthDate": "{{ birth_date }}",
  
  {% comment %} Multiple telecom entries {% endcomment %}
  {% if phone or email or mobile %}
  "telecom": [
    {% assign telecom_items = 0 %}
    {% if phone %}
      {% assign telecom_items = telecom_items | plus: 1 %}
      {
        "system": "phone",
        "value": "{{ phone }}",
        "use": "home"
      }
    {% endif %}
    {% if mobile %}
      {% if telecom_items > 0 %},{% endif %}
      {% assign telecom_items = telecom_items | plus: 1 %}
      {
        "system": "phone",
        "value": "{{ mobile }}",
        "use": "mobile"
      }
    {% endif %}
    {% if email %}
      {% if telecom_items > 0 %},{% endif %}
      {
        "system": "email",
        "value": "{{ email }}",
        "use": "home"
      }
    {% endif %}
  ],
  {% endif %}
  
  {% comment %} Handle multiple addresses {% endcomment %}
  {% if addresses %}
  "address": [
    {% for addr in addresses %}
      {
        "use": "{{ addr.use | default: 'home' }}",
        {% if addr.lines %}
        "line": {{ addr.lines | json }},
        {% endif %}
        "city": "{{ addr.city }}",
        "state": "{{ addr.state }}",
        {% if addr.postal_code %}
        "postalCode": "{{ addr.postal_code }}",
        {% endif %}
        "country": "{{ addr.country | default: 'US' }}"
      }{% unless forloop.last %},{% endunless %}
    {% endfor %}
  ]
  {% elsif address %}
  "address": [
    {
      "use": "home",
      "line": {{ address.lines | json }},
      "city": "{{ address.city }}",
      "state": "{{ address.state }}",
      "postalCode": "{{ address.postal_code }}",
      "country": "{{ address.country | default: 'US' }}"
    }
  ]
  {% endif %}
}
```

#### observation.liquid
```liquid
{
  "resourceType": "Observation",
  "id": "{{ id | default: uuid }}",
  
  {% comment %} Status validation with if-elsif-else {% endcomment %}
  {% if status == 'registered' or status == 'preliminary' or status == 'final' or status == 'amended' %}
    "status": "{{ status }}",
  {% elsif status == 'cancelled' or status == 'entered-in-error' %}
    "status": "{{ status }}",
  {% else %}
    "status": "final",
  {% endif %}
  
  {% comment %} Handle multiple categories {% endcomment %}
  {% if categories %}
  "category": [
    {% for category in categories %}
      {
        "coding": [{
          "system": "{{ category.system | default: 'http://terminology.hl7.org/CodeSystem/observation-category' }}",
          "code": "{{ category.code }}",
          "display": "{{ category.display }}"
        }]
      }{% unless forloop.last %},{% endunless %}
    {% endfor %}
  ],
  {% endif %}
  
  "code": {
    "coding": [
      {
        "system": "{{ code_system | default: 'http://loinc.org' }}",
        "code": "{{ code }}",
        "display": "{{ code_display }}"
      }
    ]
  },
  
  "subject": {
    "reference": "Patient/{{ patient_id }}"
  },
  
  {% comment %} Conditional effective date/period {% endcomment %}
  {% if effective_period %}
    "effectivePeriod": {
      "start": "{{ effective_period.start }}",
      "end": "{{ effective_period.end }}"
    },
  {% else %}
    "effectiveDateTime": "{{ effective_date | default: now }}",
  {% endif %}
  
  {% comment %} Complex value handling {% endcomment %}
  {% if value_quantity %}
    "valueQuantity": {
      "value": {{ value_quantity.value }},
      "unit": "{{ value_quantity.unit }}",
      "system": "http://unitsofmeasure.org",
      "code": "{{ value_quantity.code }}"
      {% if value_quantity.value < low_threshold or value_quantity.value > high_threshold %}
      ,"extension": [{
        "url": "http://example.org/fhir/StructureDefinition/observation-abnormal",
        "valueBoolean": true
      }]
      {% endif %}
    }
  {% elsif value_codeable_concept %}
    "valueCodeableConcept": {
      "coding": [{
        "system": "{{ value_codeable_concept.system }}",
        "code": "{{ value_codeable_concept.code }}",
        "display": "{{ value_codeable_concept.display }}"
      }]
    }
  {% elsif value_string %}
    "valueString": "{{ value_string }}"
  {% elsif components %}
    {% comment %} Handle multi-component observations like blood pressure {% endcomment %}
    "component": [
      {% for component in components %}
        {
          "code": {
            "coding": [{
              "system": "{{ component.code_system | default: 'http://loinc.org' }}",
              "code": "{{ component.code }}",
              "display": "{{ component.display }}"
            }]
          },
          "valueQuantity": {
            "value": {{ component.value }},
            "unit": "{{ component.unit }}",
            "system": "http://unitsofmeasure.org",
            "code": "{{ component.unit_code }}"
          }
        }{% unless forloop.last %},{% endunless %}
      {% endfor %}
    ]
  {% endif %}
  
  {% comment %} Add reference ranges if provided {% endcomment %}
  {% if reference_ranges %}
  ,"referenceRange": [
    {% for range in reference_ranges %}
      {
        {% if range.low %}
        "low": {
          "value": {{ range.low }},
          "unit": "{{ range.unit }}",
          "system": "http://unitsofmeasure.org",
          "code": "{{ range.unit_code }}"
        },
        {% endif %}
        {% if range.high %}
        "high": {
          "value": {{ range.high }},
          "unit": "{{ range.unit }}",
          "system": "http://unitsofmeasure.org",
          "code": "{{ range.unit_code }}"
        },
        {% endif %}
        "type": {
          "coding": [{
            "system": "http://terminology.hl7.org/CodeSystem/referencerange-meaning",
            "code": "{{ range.type | default: 'normal' }}"
          }]
        }
      }{% unless forloop.last %},{% endunless %}
    {% endfor %}
  ]
  {% endif %}
}
```

### Step 7: REST Controller

#### ConversionController.java
```java
@RestController
@RequestMapping("/api/v1/convert")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ConversionController {
    
    private final ConversionService conversionService;
    
    @PostMapping
    public ResponseEntity<ConversionResponse> convert(@Valid @RequestBody ConversionRequest request) {
        log.info("Converting {} resource", request.getResourceType());
        ConversionResponse response = conversionService.convert(request);
        
        return response.isSuccess() 
            ? ResponseEntity.ok(response)
            : ResponseEntity.badRequest().body(response);
    }
    
    @GetMapping("/templates")
    public ResponseEntity<List<String>> getAvailableTemplates() {
        // Implementation to list available templates
        return ResponseEntity.ok(Arrays.asList("Patient", "Observation", "MedicationRequest", "Encounter"));
    }
}
```

### Step 8: Error Handling and Validation

#### GlobalExceptionHandler.java
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ConversionException.class)
    public ResponseEntity<ErrorResponse> handleConversionException(ConversionException e) {
        log.error("Conversion error", e);
        return ResponseEntity.badRequest()
            .body(ErrorResponse.builder()
                .message(e.getMessage())
                .timestamp(Instant.now())
                .build());
    }
    
    @ExceptionHandler(TemplateException.class)
    public ResponseEntity<ErrorResponse> handleTemplateException(TemplateException e) {
        log.error("Template error", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse.builder()
                .message("Template processing failed: " + e.getMessage())
                .timestamp(Instant.now())
                .build());
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList());
            
        return ResponseEntity.badRequest()
            .body(ErrorResponse.builder()
                .message("Validation failed")
                .details(errors)
                .timestamp(Instant.now())
                .build());
    }
}
```

### Step 9: Testing Strategy

#### Unit Tests
```java
@SpringBootTest
@AutoConfigureMockMvc
class ConversionControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    void testPatientConversion() throws Exception {
        String requestJson = """
            {
                "resourceType": "Patient",
                "inputData": {
                    "identifier_value": "12345",
                    "family_name": "Doe",
                    "given_names": ["John", "Smith"],
                    "gender": "male",
                    "birth_date": "1990-01-01"
                },
                "validate": true
            }
            """;
            
        mockMvc.perform(post("/api/v1/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.resourceType").value("Patient"));
    }
}
```

#### Custom Filter Tests
```java
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
```

### Step 10: Performance Optimization

1. **Template Caching**: Use Spring Cache with Caffeine for compiled templates
2. **Connection Pooling**: Configure appropriate HTTP connection pools
3. **Async Processing**: For bulk conversions, implement async endpoints
4. **Resource Pooling**: Reuse FHIR context and parsers

### Step 11: Security Considerations

1. **Input Validation**: Validate JSON schema before processing
2. **Template Injection**: Sanitize template inputs
3. **Rate Limiting**: Implement rate limiting for API endpoints
4. **Authentication**: Add Spring Security for protected endpoints

### Step 12: Deployment Considerations

1. **Docker Support**: Create Dockerfile for containerization
2. **Health Checks**: Implement custom health indicators
3. **Metrics**: Use Micrometer for application metrics
4. **Configuration**: Externalize configuration with Spring Cloud Config

## Usage Example

### Basic Patient Conversion
```bash
curl -X POST http://localhost:8080/api/v1/convert \
  -H "Content-Type: application/json" \
  -d '{
    "resourceType": "Patient",
    "inputData": {
      "identifier_value": "12345",
      "family_name": "Doe",
      "given_names": ["John"],
      "gender": "male",
      "birth_date": "1990-01-01",
      "phone": "555-0123",
      "address": {
        "lines": ["123 Main St"],
        "city": "Anytown",
        "state": "CA",
        "postal_code": "90210"
      }
    },
    "validate": true
  }'
```

### Using Custom Filters
```bash
# Example with custom filters for data transformation
curl -X POST http://localhost:8080/api/v1/convert \
  -H "Content-Type: application/json" \
  -d '{
    "resourceType": "Patient",
    "inputData": {
      "mrn": "MRN123456",
      "ssn": "123456789",
      "full_name": "John Michael Doe",
      "gender": "M",
      "birth_date": "01/15/1990",
      "phone": "5551234567",
      "mobile": "(555) 987-6543",
      "simple_address": "123 Main St, Boston, MA, 02101"
    },
    "validate": true
  }'
```

The custom filters will transform:
- `ssn: "123456789"` → `{ "system": "http://hl7.org/fhir/sid/us-ssn", "value": "123-45-6789" }`
- `full_name: "John Michael Doe"` → `{ "family": "Doe", "given": ["John", "Michael"] }`
- `gender: "M"` → `"male"`
- `birth_date: "01/15/1990"` → `"1990-01-15"`
- `phone: "5551234567"` → `{ "system": "phone", "value": "(555) 123-4567", "use": "home" }`
- `simple_address: "123 Main St, Boston, MA, 02101"` → Full address structure

## Advanced Features to Implement

1. **Batch Processing**: Support for converting multiple resources
2. **Template Hot Reload**: Watch template files for changes in development
3. **Custom Liquid Filters**: Add FHIR-specific filters for common transformations
4. **Mapping Configuration**: External mapping configuration files
5. **Audit Logging**: Track all conversions for compliance

## Best Practices

1. **Template Organization**: 
   - Group templates by resource category
   - Use includes for common structures
   - Version control templates separately

2. **Custom Filter Guidelines**:
   - Keep filters focused on single transformations
   - Use descriptive names with `fhir_` prefix
   - Handle null values gracefully
   - Document expected input/output formats
   - Write comprehensive unit tests

3. **Error Messages**: 
   - Provide clear, actionable error messages
   - Include context about which field failed
   - Log detailed errors for debugging

4. **Documentation**: 
   - Use OpenAPI/Swagger for API documentation
   - Document all custom filters and their usage
   - Maintain template documentation

5. **Testing**: 
   - Maintain high test coverage (>80%)
   - Test edge cases in filters
   - Validate FHIR output against profiles

6. **Performance**:
   - Cache compiled templates
   - Reuse filter instances
   - Profile template rendering for bottlenecks

This implementation guide provides a solid foundation for building a production-ready JSON to FHIR R4 conversion tool using modern Java technologies and best practices.