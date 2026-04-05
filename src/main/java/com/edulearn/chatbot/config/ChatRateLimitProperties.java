package com.edulearn.chatbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.chat.rate-limit")
public class ChatRateLimitProperties {

    private Integer maxRequests = 20;
    private Integer windowSeconds = 60;
}

