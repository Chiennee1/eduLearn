package com.edulearn.chatbot.service;

import com.edulearn.chatbot.config.ChatRateLimitProperties;
import com.edulearn.exception.BusinessException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRateLimiterService {

    private final ChatRateLimitProperties properties;

    private final Map<Long, Deque<Instant>> userRequestTimes = new ConcurrentHashMap<>();

    public void checkLimit(Long userId) {
        int maxRequests = Math.max(1, properties.getMaxRequests());
        Duration window = Duration.ofSeconds(Math.max(1, properties.getWindowSeconds()));

        Deque<Instant> requests = userRequestTimes.computeIfAbsent(userId, ignored -> new ArrayDeque<>());
        Instant now = Instant.now();
        Instant threshold = now.minus(window);

        synchronized (requests) {
            while (!requests.isEmpty() && requests.peekFirst().isBefore(threshold)) {
                requests.removeFirst();
            }
            if (requests.size() >= maxRequests) {
                throw new BusinessException("Chat rate limit exceeded. Please try again later.", HttpStatus.TOO_MANY_REQUESTS);
            }
            requests.addLast(now);
        }
    }
}

