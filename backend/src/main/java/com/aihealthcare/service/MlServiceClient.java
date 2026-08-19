package com.aihealthcare.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MlServiceClient {

    @Value("${app.ml.api-url}")
    private String mlApiUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> predict(Integer age, String gender, List<String> symptoms) {
        Map<String, Object> request = new HashMap<>();
        request.put("age", age);
        request.put("gender", gender);
        request.put("symptoms", symptoms);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                mlApiUrl + "/predict",
                HttpMethod.POST,
                entity,
                String.class
        );

        try {
            JsonNode node = objectMapper.readTree(response.getBody());
            Map<String, Object> result = new HashMap<>();
            result.put("success", node.get("success").asBoolean());
            result.put("disease", node.get("disease").asText());
            result.put("confidence", node.get("confidence").asDouble());
            result.put("riskLevel", node.get("riskLevel").asText());
            result.put("possibleDiseases", node.get("possibleDiseases"));
            if (node.has("warnings")) {
                result.put("warnings", node.get("warnings"));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse ML response: " + e.getMessage());
        }
    }
}
