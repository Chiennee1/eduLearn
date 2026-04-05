package com.edulearn.chatbot.service;

import com.edulearn.auth.entity.User;
import com.edulearn.auth.repository.UserRepository;
import com.edulearn.chatbot.dto.ChatAskRequest;
import com.edulearn.chatbot.dto.ChatConversationResponse;
import com.edulearn.chatbot.dto.ChatMessageResponse;
import com.edulearn.chatbot.dto.ChatReplyResponse;
import com.edulearn.chatbot.entity.ChatConversation;
import com.edulearn.chatbot.entity.ChatMessage;
import com.edulearn.chatbot.entity.ChatRole;
import com.edulearn.chatbot.repository.ChatConversationRepository;
import com.edulearn.chatbot.repository.ChatMessageRepository;
import com.edulearn.course.entity.Course;
import com.edulearn.course.repository.CourseRepository;
import com.edulearn.course.repository.EnrollmentRepository;
import com.edulearn.course.service.CoursePermissionService;
import com.edulearn.exception.BusinessException;
import com.edulearn.exception.ResourceNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatConversationRepository conversationRepository;
    private final ChatMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CoursePermissionService coursePermissionService;
    private final AnthropicClient anthropicClient;
    private final ChatRateLimiterService rateLimiterService;

    @Transactional
    public ChatReplyResponse ask(ChatAskRequest request, String actorEmail) {
        User actor = getActor(actorEmail);
        rateLimiterService.checkLimit(actor.getId());

        ChatConversation conversation = resolveConversation(actor, request);

        ChatMessage userMessage = messageRepository.save(ChatMessage.builder()
                .conversation(conversation)
                .role(ChatRole.USER)
                .content(request.getMessage().trim())
                .build());

        List<ChatMessage> history = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId());
        String systemPrompt = buildSystemPrompt(conversation.getCourse());
        AnthropicClient.AnthropicReply anthropicReply = anthropicClient
                .generateReply(systemPrompt, history, userMessage.getContent());

        ChatMessage assistantMessage = messageRepository.save(ChatMessage.builder()
                .conversation(conversation)
                .role(ChatRole.ASSISTANT)
                .content(anthropicReply.text())
                .tokensUsed(anthropicReply.outputTokens())
                .build());

        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.save(conversation);

        return ChatReplyResponse.builder()
                .conversationId(conversation.getId())
                .userMessageId(userMessage.getId())
                .assistantMessageId(assistantMessage.getId())
                .assistantReply(assistantMessage.getContent())
                .repliedAt(assistantMessage.getCreatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ChatConversationResponse> getMyConversations(String actorEmail) {
        User actor = getActor(actorEmail);
        return conversationRepository.findByUserIdOrderByUpdatedAtDesc(actor.getId())
                .stream()
                .map(conversation -> ChatConversationResponse.builder()
                        .id(conversation.getId())
                        .courseId(conversation.getCourse() != null ? conversation.getCourse().getId() : null)
                        .title(conversation.getTitle())
                        .createdAt(conversation.getCreatedAt())
                        .updatedAt(conversation.getUpdatedAt())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ChatMessageResponse> getConversationMessages(Long conversationId, String actorEmail) {
        User actor = getActor(actorEmail);
        ChatConversation conversation = conversationRepository.findByIdAndUserId(conversationId, actor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

        return messageRepository.findByConversationIdOrderByCreatedAtAsc(conversation.getId())
                .stream()
                .map(message -> ChatMessageResponse.builder()
                        .id(message.getId())
                        .role(message.getRole())
                        .content(message.getContent())
                        .tokensUsed(message.getTokensUsed())
                        .createdAt(message.getCreatedAt())
                        .build())
                .toList();
    }

    private ChatConversation resolveConversation(User actor, ChatAskRequest request) {
        if (request.getConversationId() != null) {
            ChatConversation conversation = conversationRepository
                    .findByIdAndUserId(request.getConversationId(), actor.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Conversation not found"));

            if (request.getCourseId() != null && conversation.getCourse() != null
                    && !conversation.getCourse().getId().equals(request.getCourseId())) {
                throw new BusinessException("Conversation is already bound to another course", HttpStatus.BAD_REQUEST);
            }
            return conversation;
        }

        Course course = resolveCourseContext(actor, request.getCourseId());
        String title = request.getMessage().trim();
        if (title.length() > 80) {
            title = title.substring(0, 80);
        }

        return conversationRepository.save(ChatConversation.builder()
                .user(actor)
                .course(course)
                .title(title)
                .build());
    }

    private Course resolveCourseContext(User actor, Long courseId) {
        if (courseId == null) {
            return null;
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        boolean isAdmin = coursePermissionService.isAdmin(actor);
        boolean isInstructor = coursePermissionService.isInstructor(actor)
                && course.getInstructor().getId().equals(actor.getId());
        if (isAdmin || isInstructor) {
            return course;
        }

        boolean enrolled = enrollmentRepository.existsByUserIdAndCourseId(actor.getId(), courseId);
        if (!enrolled) {
            throw new BusinessException("You must enroll this course to use course-context chat", HttpStatus.FORBIDDEN);
        }
        return course;
    }

    private User getActor(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private String buildSystemPrompt(Course course) {
        String base = "You are EduLearn AI tutor. Answer clearly, actionable, and concise. "
                + "If user asks for code, provide safe examples and explanations in Vietnamese by default.";

        if (course == null) {
            return base;
        }

        String description = course.getDescription() == null ? "N/A" : course.getDescription();
        return base + "\n\nCurrent course context:\n"
                + "- Title: " + course.getTitle() + "\n"
                + "- Level: " + course.getLevel() + "\n"
                + "- Language: " + course.getLanguage() + "\n"
                + "- Duration hours: " + course.getDurationHours() + "\n"
                + "- Description: " + description;
    }

    public List<String> chunkForStreaming(String reply) {
        String[] words = reply.split("\\s+");
        java.util.ArrayList<String> chunks = new java.util.ArrayList<>();
        StringBuilder builder = new StringBuilder();

        int counter = 0;
        for (String word : words) {
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(word);
            counter++;
            if (counter >= 8) {
                chunks.add(builder.toString());
                builder = new StringBuilder();
                counter = 0;
            }
        }
        if (builder.length() > 0) {
            chunks.add(builder.toString());
        }
        return chunks;
    }
}

