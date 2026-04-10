package com.edulearn.chatbot.service;

import com.edulearn.chatbot.config.GeminiProperties;
import com.edulearn.chatbot.entity.ChatMessage;
import com.edulearn.chatbot.entity.ChatRole;
import com.edulearn.exception.BusinessException;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
@RequiredArgsConstructor
public class GeminiClient {

    private final GeminiProperties properties;

    public GeminiReply generateReply(String systemPrompt, List<ChatMessage> history, String userMessage) {
        if (!properties.isEnabled()) {
            return new GeminiReply(buildFallbackReply(userMessage), null);
        }
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new BusinessException("Gemini API key is missing", HttpStatus.SERVICE_UNAVAILABLE);
        }

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Math.max(1, properties.getConnectTimeoutMs()));
        requestFactory.setReadTimeout(Math.max(1, properties.getReadTimeoutMs()));

        RestClient client = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();

        List<Map<String, Object>> contents = new ArrayList<>();
        for (ChatMessage message : history) {
            Map<String, Object> content = new HashMap<>();
            content.put("role", message.getRole() == ChatRole.ASSISTANT ? "model" : "user");

            Map<String, Object> part = new HashMap<>();
            part.put("text", message.getContent());

            List<Map<String, Object>> parts = new ArrayList<>();
            parts.add(part);
            content.put("parts", parts);
            contents.add(content);
        }

        Map<String, Object> systemInstructionPart = new HashMap<>();
        systemInstructionPart.put("text", systemPrompt);

        List<Map<String, Object>> systemInstructionParts = new ArrayList<>();
        systemInstructionParts.add(systemInstructionPart);

        Map<String, Object> systemInstruction = new HashMap<>();
        systemInstruction.put("parts", systemInstructionParts);

        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("maxOutputTokens", properties.getMaxOutputTokens());

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("systemInstruction", systemInstruction);
        requestBody.put("contents", contents);
        requestBody.put("generationConfig", generationConfig);

        String uri = "/v1beta/models/" + properties.getModel() + ":generateContent?key=" + properties.getApiKey();

        try {
            JsonNode root = client.post()
                    .uri(uri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            if (root == null || root.isMissingNode()) {
                throw new BusinessException("Gemini API returned empty response", HttpStatus.BAD_GATEWAY);
            }

            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.isEmpty()) {
                throw new BusinessException("Gemini API returned no candidates", HttpStatus.BAD_GATEWAY);
            }

            JsonNode parts = candidates.get(0).path("content").path("parts");
            if (parts.isArray() && !parts.isEmpty()) {
                String text = parts.get(0).path("text").asText();
                if (text != null && !text.isBlank()) {
                    Integer outputTokens = extractOutputTokens(root);
                    return new GeminiReply(text, outputTokens);
                }
            }
            throw new BusinessException("Gemini API returned invalid content", HttpStatus.BAD_GATEWAY);
        } catch (RestClientResponseException ex) {
            throw toApiError(ex);
        } catch (ResourceAccessException ex) {
            throw new BusinessException("Gemini API timeout or network failure", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RestClientException ex) {
            throw new BusinessException("Gemini API request failed", HttpStatus.BAD_GATEWAY);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Failed to parse Gemini response", HttpStatus.BAD_GATEWAY);
        }
    }

    private Integer extractOutputTokens(JsonNode root) {
        JsonNode outputTokens = root.path("usageMetadata").path("candidatesTokenCount");
        if (outputTokens.isInt()) {
            return outputTokens.asInt();
        }
        return null;
    }

    private BusinessException toApiError(RestClientResponseException ex) {
        int statusCode = ex.getStatusCode().value();
        String detail = compactError(ex.getResponseBodyAsString());

        String message;
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        boolean includeDetail = true;
        if (statusCode == 400) {
            message = "Gemini API rejected the request";
        } else if (statusCode == 401 || statusCode == 403) {
            message = "Gemini API key is invalid or missing permission";
            includeDetail = false;
        } else if (statusCode == 404) {
            message = "Gemini model or endpoint was not found";
        } else if (statusCode == 429) {
            message = "Gemini API quota or rate limit exceeded";
            status = HttpStatus.TOO_MANY_REQUESTS;
            includeDetail = false;
        } else if (statusCode >= 500) {
            message = "Gemini API is temporarily unavailable";
            includeDetail = false;
        } else {
            String statusText = ex.getStatusText();
            if (statusText != null && !statusText.isBlank()) {
                message = "Gemini API request failed with HTTP " + statusCode + " (" + statusText + ")";
            } else {
                message = "Gemini API request failed with HTTP " + statusCode;
            }
        }

        if (includeDetail && detail != null) {
            message = message + ": " + detail;
        }
        return new BusinessException(message, status);
    }

    private String compactError(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return null;
        }

        String compact = responseBody.replaceAll("[\\r\\n\\t]+", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();
        if (compact.length() > 220) {
            return compact.substring(0, 220) + "...";
        }
        return compact;
    }

    private String buildFallbackReply(String userMessage) {
        return "AI service hien dang o che do fallback. Cau hoi cua ban da duoc ghi nhan: " + userMessage;
    }

    public record GeminiReply(String text, Integer outputTokens) {
    }
}
