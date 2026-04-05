package com.edulearn.auth.service;

import com.edulearn.auth.entity.RefreshToken;
import com.edulearn.auth.entity.User;
import com.edulearn.auth.repository.RefreshTokenRepository;
import com.edulearn.config.JwtConfig;
import com.edulearn.exception.BusinessException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final int TOKEN_BYTE_LENGTH = 64;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public RefreshToken createToken(User user) {
        refreshTokenRepository.revokeAllActiveTokensByUserId(user.getId());

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(generateTokenValue())
                .expiresAt(LocalDateTime.now().plusDays(jwtConfig.getRefreshTokenDays()))
                .revoked(false)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken verifyActiveToken(String tokenValue) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new BusinessException("Refresh token is invalid", HttpStatus.UNAUTHORIZED));

        if (refreshToken.isRevoked() || refreshToken.isExpired()) {
            throw new BusinessException("Refresh token is expired or revoked", HttpStatus.UNAUTHORIZED);
        }
        return refreshToken;
    }

    @Transactional
    public RefreshToken rotateToken(String tokenValue) {
        RefreshToken oldToken = verifyActiveToken(tokenValue);
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);
        return createToken(oldToken.getUser());
    }

    @Transactional
    public void revoke(String tokenValue) {
        refreshTokenRepository.findByToken(tokenValue).ifPresent(token -> {
            if (!token.isRevoked()) {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            }
        });
    }

    private String generateTokenValue() {
        byte[] random = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(random);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(random);
    }
}
