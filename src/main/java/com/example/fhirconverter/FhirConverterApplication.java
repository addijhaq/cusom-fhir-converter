package com.example.fhirconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FhirConverterApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(FhirConverterApplication.class, args);
    }
}