package com.github.jovanbeldar.intellijaicodeexplainer.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jovanbeldar.intellijaicodeexplainer.models.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class AiService {

    private static final String API_KEY_ENV = "OPENAI_API_KEY";
    private static final String MODEL = "gpt-4.1-mini";
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static String getApiKey() {
        String apiKey = System.getenv(API_KEY_ENV);

        if(apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("OPENAI_API_KEY environment variable is missing.");
        }

        return apiKey;
    }

    private static ChatRequest createRequest(String prompt) {
        Message message = new Message("user", prompt);
        return new ChatRequest(MODEL, List.of(message));
    }

    private static String extractExplanation(ChatResponse response) {
        if(response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new IllegalStateException("No AI response received");
        }

        return response.getChoices().getFirst().getMessage().getContent();
    }

    public static String explainCode(String prompt) {
        String apiKey = getApiKey();

        ChatRequest chatRequest = createRequest(prompt);

        try {
            String json = MAPPER.writeValueAsString(chatRequest);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() != 200) {
                throw new IllegalStateException("OpenAI API request failed: " + response.body());
            }

            ChatResponse chatResponse = MAPPER.readValue(response.body(), ChatResponse.class);

            return extractExplanation(chatResponse);

        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize OpenAI request.", e);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
