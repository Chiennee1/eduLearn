package com.edulearn.auth.service;

import com.edulearn.auth.dto.LoginRequest;
import com.edulearn.auth.dto.RegisterRequest;
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
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public TokenResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email already exists", HttpStatus.CONFLICT);
        }

        Role defaultRole = roleRepository.findByName(RoleName.STUDENT)
                .orElseThrow(() -> new ResourceNotFoundException("Default role STUDENT not found"));

        User user = User.builder()
                .email(request.getEmail().trim().toLowerCase())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName().trim())
                .status(UserStatus.ACTIVE)
                .emailVerified(false)
                .roles(Set.of(defaultRole))
                .build();
        userRepository.save(user);

        UserPrincipal principal = UserPrincipal.from(user);
        String accessToken = jwtService.generateAccessToken(principal);
        RefreshToken refreshToken = refreshTokenService.createToken(user);
        return toTokenResponse(user, accessToken, jwtService.getAccessTokenExpiry(accessToken), refreshToken);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        ensureUserCanLogin(user);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
            );
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            String accessToken = jwtService.generateAccessToken(principal);
            RefreshToken refreshToken = refreshTokenService.createToken(user);
            return toTokenResponse(user, accessToken, jwtService.getAccessTokenExpiry(accessToken), refreshToken);
        } catch (DisabledException ex) {
            throw new BusinessException("Account is disabled", HttpStatus.FORBIDDEN);
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Transactional
    public TokenResponse refresh(String refreshTokenValue) {
        RefreshToken newRefreshToken = refreshTokenService.rotateToken(refreshTokenValue);
        User user = newRefreshToken.getUser();
        ensureUserCanLogin(user);

        String accessToken = jwtService.generateAccessToken(UserPrincipal.from(user));
        return toTokenResponse(user, accessToken, jwtService.getAccessTokenExpiry(accessToken), newRefreshToken);
    }

    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenService.revoke(refreshTokenValue);
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse me(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return toUserSummary(user);
    }

    private TokenResponse toTokenResponse(
            User user,
            String accessToken,
            java.time.LocalDateTime accessTokenExpiresAt,
            RefreshToken refreshToken
    ) {
        return TokenResponse.builder()
                .tokenType("Bearer")
                .accessToken(accessToken)
                .accessTokenExpiresAt(accessTokenExpiresAt)
                .refreshToken(refreshToken.getToken())
                .refreshTokenExpiresAt(refreshToken.getExpiresAt())
                .user(toUserSummary(user))
                .build();
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
