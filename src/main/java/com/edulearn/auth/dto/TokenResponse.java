package com.edulearn.auth.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {

    private final String tokenType;
    private final String accessToken;
    private final LocalDateTime accessTokenExpiresAt;
    private final String refreshToken;
    private final LocalDateTime refreshTokenExpiresAt;
    private final UserSummaryResponse user;
}
