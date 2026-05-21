package com.steve.ai.llm;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.steve.ai.SteveMod;
import com.steve.ai.config.SteveConfig;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Client for OpenRouter API - Provides access to 100+ LLM models
 * Supports models from: OpenAI, Anthropic, Google, Cohere, Meta, and more
 * 
 * Popular OpenRouter models:
 * - openai/gpt-4-turbo
 * - openai/gpt-4
 * - openai/gpt-3.5-turbo
 * - anthropic/claude-3-sonnet
 * - anthropic/claude-3-opus
 * - anthropic/claude-3-haiku
 * - google/gemini-pro
 * - meta/llama-3-70b-instruct
 */
public class OpenRouterClient {
    private static final String OPENROUTER_API_URL = "https://openrouter.ai/api/v1/chat/completions";
    
    private final HttpClient client;
    private final String apiKey;
    private final String model;

    public OpenRouterClient() {
        this.apiKey = SteveConfig.OPENROUTER_API_KEY.get();
        this.model = SteveConfig.OPENROUTER_MODEL.get();
        this.client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();
    }

    public String sendRequest(String systemPrompt, String userPrompt) {
        if (apiKey == null || apiKey.isEmpty()) {
            SteveMod.LOGGER.error("OpenRouter API key is not set in the config.");
            return null;
        }

        JsonObject requestBody = buildRequestBody(systemPrompt, userPrompt);

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(OPENROUTER_API_URL))
            .header("Content-Type", "application/json")
            .header("Authorization", "Bearer " + apiKey)
            .header("HTTP-Referer", "https://github.com/YuvDwi/Steve")
            .header("X-Title", "Steve AI - Minecraft Autonomous Agent")
            .timeout(Duration.ofSeconds(60))
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return parseResponse(response.body());
            } else {
                SteveMod.LOGGER.error("OpenRouter API request failed: {}", response.statusCode());
                SteveMod.LOGGER.error("Response body: {}", response.body());
                return null;
            }
        } catch (Exception e) {
            SteveMod.LOGGER.error("Error sending request to OpenRouter API", e);
            return null;
        }
    }

    private JsonObject buildRequestBody(String systemPrompt, String userPrompt) {
        JsonObject body = new JsonObject();
        
        // Set model
        body.addProperty("model", model);
        
        // Set parameters
        body.addProperty("temperature", SteveConfig.TEMPERATURE.get());
        body.addProperty("max_tokens", SteveConfig.MAX_TOKENS.get());
        
        // Optional: Add streaming support for faster responses
        body.addProperty("stream", false);

        // Build messages array
        JsonArray messages = new JsonArray();
        
        // System message
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemPrompt);
        messages.add(systemMessage);

        // User message
        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", userPrompt);
        messages.add(userMessage);

        body.add("messages", messages);
        
        return body;
    }

    private String parseResponse(String responseBody) {
        try {
            JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
            
            if (json.has("choices") && json.getAsJsonArray("choices").size() > 0) {
                JsonObject firstChoice = json.getAsJsonArray("choices").get(0).getAsJsonObject();
                if (firstChoice.has("message")) {
                    JsonObject message = firstChoice.getAsJsonObject("message");
                    if (message.has("content")) {
                        String content = message.get("content").getAsString();
                        
                        // Log model information for debugging
                        if (json.has("model")) {
                            SteveMod.LOGGER.info("OpenRouter used model: {}", json.get("model").getAsString());
                        }
                        
                        return content;
                    }
                }
            }
            
            SteveMod.LOGGER.error("Unexpected OpenRouter response format: {}", responseBody);
            return null;
            
        } catch (Exception e) {
            SteveMod.LOGGER.error("Error parsing OpenRouter response", e);
            return null;
        }
    }
}