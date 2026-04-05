package com.edulearn.review.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewResponse {

    private final Long id;
    private final Long userId;
    private final String fullName;
    private final Byte rating;
    private final String content;
    private final long likeCount;
    private final boolean likedByMe;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}

