package com.edulearn.chatbot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatAskRequest {

    private Long conversationId;
    private Long courseId;

    @NotBlank(message = "Message is required")
    private String message;
}

