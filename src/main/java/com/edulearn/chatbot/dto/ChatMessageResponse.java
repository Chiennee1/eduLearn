package com.edulearn.chatbot.dto;

import com.edulearn.chatbot.entity.ChatRole;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatMessageResponse {

    private final Long id;
    private final ChatRole role;
    private final String content;
    private final Integer tokensUsed;
    private final LocalDateTime createdAt;
}

