package com.fitness.aiservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class GeminiInteractionService {
    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String uri;

    @Value("${gemini.api.key}")
    private String key;
    public GeminiInteractionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getAnswer(String prompt){
        Map<String,Object> body = Map.of(
                "contents", new Object[] {
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );
        String response = webClient.post()
                .uri(uri+key)
                .header("Content-Type", "application/json")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        return response;
    }

}
