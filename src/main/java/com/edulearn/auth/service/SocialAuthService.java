package com.edulearn.auth.service;

import com.edulearn.auth.dto.TokenResponse;
import com.edulearn.auth.dto.UserSummaryResponse;
import com.edulearn.auth.entity.RefreshToken;
import com.edulearn.auth.entity.Role;
import com.edulearn.auth.entity.RoleName;
import com.edulearn.auth.entity.User;
import com.edulearn.auth.entity.UserStatus;
import com.edulearn.auth.repository.RoleRepository;
import com.edulearn.auth.repository.UserRepository;
import com.edulearn.auth.security.UserPrincipal;
import com.edulearn.exception.BusinessException;
import com.edulearn.exception.ResourceNotFoundException;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SocialAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public TokenResponse loginOrRegisterSocialUser(
            String email,
            String fullName,
            String avatarUrl,
            boolean emailVerified) {
        if (!StringUtils.hasText(email)) {
            throw new BusinessException("Email is required for social login", HttpStatus.BAD_REQUEST);
        }

        String normalizedEmail = email.trim().toLowerCase();
        User user = userRepository.findByEmail(normalizedEmail)
                .map(existing -> updateExistingUser(existing, fullName, avatarUrl, emailVerified))
                .orElseGet(() -> createSocialUser(normalizedEmail, fullName, avatarUrl, emailVerified));

        ensureUserCanLogin(user);

        String accessToken = jwtService.generateAccessToken(UserPrincipal.from(user));
        RefreshToken refreshToken = refreshTokenService.createToken(user);
        return TokenResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresAt(jwtService.getAccessTokenExpiry(accessToken))
                .refreshToken(refreshToken.getToken())
                .refreshTokenExpiresAt(refreshToken.getExpiresAt())
                .user(toUserSummary(user))
                .build();
    }

    private User updateExistingUser(User user, String fullName, String avatarUrl, boolean emailVerified) {
        boolean dirty = false;

        if (StringUtils.hasText(fullName) && !fullName.equals(user.getFullName())) {
            user.setFullName(fullName);
            dirty = true;
        }
        if (StringUtils.hasText(avatarUrl) && !avatarUrl.equals(user.getAvatarUrl())) {
            user.setAvatarUrl(avatarUrl);
            dirty = true;
        }
        if (emailVerified && !user.isEmailVerified()) {
            user.setEmailVerified(true);
            dirty = true;
        }
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            user.setRoles(Set.of(defaultStudentRole()));
            dirty = true;
        }

        return dirty ? userRepository.save(user) : user;
    }

    private User createSocialUser(String normalizedEmail, String fullName, String avatarUrl, boolean emailVerified) {
        Role defaultRole = defaultStudentRole();
        String displayName = StringUtils.hasText(fullName)
                ? fullName.trim()
                : normalizedEmail.substring(0, normalizedEmail.indexOf('@'));

        User user = User.builder()
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
                .fullName(displayName)
                .avatarUrl(avatarUrl)
                .status(UserStatus.ACTIVE)
                .emailVerified(emailVerified)
                .roles(Set.of(defaultRole))
                .build();

        return userRepository.save(user);
    }

    private Role defaultStudentRole() {
        return roleRepository.findByName(RoleName.STUDENT)
                .orElseThrow(() -> new ResourceNotFoundException("Default role STUDENT not found"));
    }

    private UserSummaryResponse toUserSummary(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toSet()))
                .build();
    }

    private void ensureUserCanLogin(User user) {
        if (user.getStatus() == UserStatus.BANNED) {
            throw new BusinessException("Account has been banned", HttpStatus.FORBIDDEN);
        }
        if (user.getStatus() == UserStatus.INACTIVE) {
            throw new BusinessException("Account is inactive", HttpStatus.FORBIDDEN);
        }
    }
}
