package com.edulearn.chatbot.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatConversationResponse {

    private final Long id;
    private final Long courseId;
    private final String title;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}

