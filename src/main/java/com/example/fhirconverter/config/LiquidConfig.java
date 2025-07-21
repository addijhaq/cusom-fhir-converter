package com.example.fhirconverter.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import liqp.TemplateParser;
import liqp.filters.Filter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class LiquidConfig {
    
    @Bean
    public TemplateParser liquidTemplateParser(List<Filter> customFilters) {
        TemplateParser.Builder builder = new TemplateParser.Builder();
        
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