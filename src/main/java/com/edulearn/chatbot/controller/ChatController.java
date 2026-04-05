package com.edulearn.chatbot.controller;

import com.edulearn.chatbot.dto.ChatAskRequest;
import com.edulearn.chatbot.dto.ChatConversationResponse;
import com.edulearn.chatbot.dto.ChatMessageResponse;
import com.edulearn.chatbot.dto.ChatReplyResponse;
import com.edulearn.chatbot.service.ChatbotService;
import com.edulearn.common.ApiResponse;
import com.edulearn.common.Constants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping(Constants.CHAT_API_PREFIX)
public class ChatController {

    private final ChatbotService chatbotService;

    @PostMapping("/ask")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<ChatReplyResponse>> ask(
            @Valid @RequestBody ChatAskRequest request,
            Authentication authentication
    ) {
        ChatReplyResponse response = chatbotService.ask(request, authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Chat response generated", response));
    }

    @GetMapping("/conversations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ChatConversationResponse>>> getMyConversations(Authentication authentication) {
        List<ChatConversationResponse> response = chatbotService.getMyConversations(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success("Conversation history fetched", response));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ChatMessageResponse>>> getConversationMessages(
            @PathVariable Long conversationId,
            Authentication authentication
    ) {
        List<ChatMessageResponse> response = chatbotService.getConversationMessages(
                conversationId,
                authentication.getName()
        );
        return ResponseEntity.ok(ApiResponse.success("Messages fetched", response));
    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("isAuthenticated()")
    public SseEmitter stream(
            @RequestParam @NotBlank(message = "Message is required") String message,
            @RequestParam(required = false) Long conversationId,
            @RequestParam(required = false) Long courseId,
            Authentication authentication
    ) {
        SseEmitter emitter = new SseEmitter(0L);

        CompletableFuture.runAsync(() -> {
            try {
                ChatAskRequest request = new ChatAskRequest();
                request.setMessage(message);
                request.setConversationId(conversationId);
                request.setCourseId(courseId);

                ChatReplyResponse response = chatbotService.ask(request, authentication.getName());
                for (String chunk : chatbotService.chunkForStreaming(response.getAssistantReply())) {
                    emitter.send(SseEmitter.event().name("chunk").data(chunk));
                }
                emitter.send(SseEmitter.event().name("done").data("[DONE]"));
                emitter.complete();
            } catch (Exception ex) {
                try {
                    emitter.send(SseEmitter.event().name("error").data(ex.getMessage()));
                } catch (IOException ignored) {
                    // Ignore secondary IO errors while sending error event.
                }
                emitter.completeWithError(ex);
            }
        });

        return emitter;
    }
}

