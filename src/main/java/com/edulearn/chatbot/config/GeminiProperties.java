package com.edulearn.chatbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.gemini")
public class GeminiProperties {

    private boolean enabled = false;
    private String baseUrl = "https://generativelanguage.googleapis.com";
    private String apiKey = "";
    private String model = "gemini-2.0-flash";
    private Integer maxOutputTokens = 512;
    private Integer connectTimeoutMs = 3000;
    private Integer readTimeoutMs = 30000;
}
