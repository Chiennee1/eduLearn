package com.edulearn.chatbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.anthropic")
public class AnthropicProperties {

    private boolean enabled = false;
    private String baseUrl = "https://api.anthropic.com";
    private String apiKey = "";
    private String model = "claude-3-5-sonnet-latest";
    private Integer maxTokens = 512;
    private String version = "2023-06-01";
    private Integer connectTimeoutMs = 3000;
    private Integer readTimeoutMs = 30000;
}

