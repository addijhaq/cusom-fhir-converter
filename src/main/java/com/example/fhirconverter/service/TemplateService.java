package com.example.fhirconverter.service;

import com.example.fhirconverter.exception.TemplateException;
import liqp.Template;
import liqp.TemplateParser;
import liqp.filters.Filter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                TemplateParser.Builder builder = new TemplateParser.Builder();
                
                additionalFilters.forEach(builder::withFilter);
                
                Template template = builder.build().parse(templateContent);
                return template.render(data);
            }
        } catch (IOException e) {
            throw new TemplateException("Failed to render template with filters", e);
        }
    }
}