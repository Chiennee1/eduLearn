package com.edulearn.chatbot.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatReplyResponse {

    private final Long conversationId;
    private final Long userMessageId;
    private final Long assistantMessageId;
    private final String assistantReply;
    private final LocalDateTime repliedAt;
}

