package com.github.jovanbeldar.intellijaicodeexplainer.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.jovanbeldar.intellijaicodeexplainer.exceptions.AiServiceException;
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

    private static String getApiKey() throws AiServiceException {
        String apiKey = System.getenv(API_KEY_ENV);

        if(apiKey == null || apiKey.isBlank()) {
            throw new AiServiceException("OPENAI_API_KEY environment variable is missing.");
        }

        return apiKey;
    }

    private static ChatRequest createRequest(String prompt) {
        Message message = new Message("user", prompt);
        return new ChatRequest(MODEL, List.of(message));
    }

    private static void checkResponse(HttpResponse<String> response) throws AiServiceException {
        switch (response.statusCode()) {
            case 401:
                throw new AiServiceException("Invalid API key.");
            case 429:
                throw new AiServiceException("Rate limit exceeded.");
            case 500:
                throw new AiServiceException("OpenAI server error.");
            default:
                throw new AiServiceException("Unexpected API error: " + response.statusCode());
        }
    }

    private static String extractExplanation(ChatResponse response) throws AiServiceException {
        if(response.getChoices() == null || response.getChoices().isEmpty()) {
            throw new AiServiceException("No explanation returned from AI.");
        }

        return response.getChoices().getFirst().getMessage().getContent();
    }

    public static String explainCode(String prompt) throws AiServiceException {
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
                checkResponse(response);
            }

            ChatResponse chatResponse = MAPPER.readValue(response.body(), ChatResponse.class);

            return extractExplanation(chatResponse);

        } catch (JsonProcessingException e) {

            throw new AiServiceException("Failed to process AI response.", e);

        } catch (IOException e) {

            throw new AiServiceException("Network error while contacting OpenAI.", e);

        } catch(InterruptedException e) {

            Thread.currentThread().interrupt();
            throw new AiServiceException("Request was interrupted.", e);
        }
    }
}
