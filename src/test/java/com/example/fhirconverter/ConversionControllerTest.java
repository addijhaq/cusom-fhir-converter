package com.example.fhirconverter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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