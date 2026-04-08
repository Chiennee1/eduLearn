package com.edulearn.chatbot.service;

import com.edulearn.chatbot.config.AnthropicProperties;
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
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Component
@RequiredArgsConstructor
public class AnthropicClient {

    private final AnthropicProperties properties;

    public AnthropicReply generateReply(String systemPrompt, List<ChatMessage> history, String userMessage) {
        if (!properties.isEnabled()) {
            return new AnthropicReply(buildFallbackReply(userMessage), null);
        }
        if (properties.getApiKey() == null || properties.getApiKey().isBlank()) {
            throw new BusinessException("Anthropic API key is missing", HttpStatus.SERVICE_UNAVAILABLE);
        }

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Math.max(1, properties.getConnectTimeoutMs()));
        requestFactory.setReadTimeout(Math.max(1, properties.getReadTimeoutMs()));

        RestClient client = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .requestFactory(requestFactory)
                .build();

        List<Map<String, Object>> messages = new ArrayList<>();
        for (ChatMessage message : history) {
            Map<String, Object> item = new HashMap<>();
            item.put("role", message.getRole() == ChatRole.ASSISTANT ? "assistant" : "user");
            item.put("content", message.getContent());
            messages.add(item);
        }

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", properties.getModel());
        requestBody.put("max_tokens", properties.getMaxTokens());
        requestBody.put("system", systemPrompt);
        requestBody.put("messages", messages);

        try {
            JsonNode root = client.post()
                    .uri("/v1/messages")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("x-api-key", properties.getApiKey())
                    .header("anthropic-version", properties.getVersion())
                    .body(requestBody)
                    .retrieve()
                    .body(JsonNode.class);

            if (root == null || root.isMissingNode()) {
                throw new BusinessException("Anthropic API returned empty response", HttpStatus.BAD_GATEWAY);
            }

            JsonNode content = root.path("content");
            if (content.isArray() && !content.isEmpty()) {
                String text = content.get(0).path("text").asText();
                if (text != null && !text.isBlank()) {
                    Integer outputTokens = extractOutputTokens(root);
                    return new AnthropicReply(text, outputTokens);
                }
            }
            throw new BusinessException("Anthropic API returned invalid content", HttpStatus.BAD_GATEWAY);
        } catch (RestClientResponseException ex) {
            throw new BusinessException("Anthropic API returned error status", HttpStatus.BAD_GATEWAY);
        } catch (ResourceAccessException ex) {
            throw new BusinessException("Anthropic API timeout or network failure", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (RestClientException ex) {
            throw new BusinessException("Anthropic API request failed", HttpStatus.BAD_GATEWAY);
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException("Failed to parse Anthropic response", HttpStatus.BAD_GATEWAY);
        }
    }

    private Integer extractOutputTokens(JsonNode root) {
        JsonNode outputTokens = root.path("usage").path("output_tokens");
        if (outputTokens.isInt()) {
            return outputTokens.asInt();
        }
        return null;
    }

    private String buildFallbackReply(String userMessage) {
        return "AI service hien dang o che do fallback. Cau hoi cua ban da duoc ghi nhan: " + userMessage;
    }

    public record AnthropicReply(String text, Integer outputTokens) {
    }
}

