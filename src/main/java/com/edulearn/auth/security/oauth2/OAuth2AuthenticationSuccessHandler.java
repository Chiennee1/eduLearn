package com.edulearn.auth.security.oauth2;

import com.edulearn.auth.dto.TokenResponse;
import com.edulearn.auth.service.SocialAuthService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final SocialAuthService socialAuthService;

    @Value("${app.oauth2.redirect-uri:http://localhost:3000/auth/social/callback}")
    private String successRedirectUri;

    @Value("${app.oauth2.failure-uri:http://localhost:3000/login}")
    private String failureRedirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        if (!(authentication instanceof OAuth2AuthenticationToken oauth2Auth)) {
            response.sendRedirect(buildFailureRedirect("invalid_social_auth"));
            return;
        }

        OAuth2User oauth2User = oauth2Auth.getPrincipal();
        Map<String, Object> attributes = oauth2User.getAttributes();

        String email = stringAttr(attributes, "email");
        if (!StringUtils.hasText(email)) {
            response.sendRedirect(buildFailureRedirect("email_not_available"));
            return;
        }

        String fullName = resolveDisplayName(attributes, email);
        String avatarUrl = resolveAvatar(attributes);
        boolean emailVerified = Boolean.parseBoolean(String.valueOf(attributes.getOrDefault("email_verified", true)));

        try {
            TokenResponse tokenResponse = socialAuthService.loginOrRegisterSocialUser(
                    email,
                    fullName,
                    avatarUrl,
                    emailVerified);
            response.sendRedirect(buildSuccessRedirect(tokenResponse));
        } catch (Exception ex) {
            response.sendRedirect(buildFailureRedirect("social_login_failed"));
        }
    }

    private String buildSuccessRedirect(TokenResponse tokenResponse) {
        String fragment = "accessToken=" + encode(tokenResponse.getAccessToken())
                + "&refreshToken=" + encode(tokenResponse.getRefreshToken());
        return successRedirectUri + "#" + fragment;
    }

    private String buildFailureRedirect(String errorCode) {
        return UriComponentsBuilder
                .fromUriString(failureRedirectUri)
                .queryParam("socialError", errorCode)
                .build()
                .toUriString();
    }

    private String resolveDisplayName(Map<String, Object> attributes, String email) {
        String displayName = stringAttr(attributes, "name");
        if (StringUtils.hasText(displayName)) {
            return displayName;
        }

        String login = stringAttr(attributes, "login");
        if (StringUtils.hasText(login)) {
            return login;
        }

        int at = email.indexOf('@');
        return at > 0 ? email.substring(0, at) : email;
    }

    private String resolveAvatar(Map<String, Object> attributes) {
        String googleAvatar = stringAttr(attributes, "picture");
        if (StringUtils.hasText(googleAvatar)) {
            return googleAvatar;
        }
        return stringAttr(attributes, "avatar_url");
    }

    private String stringAttr(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        if (value == null) {
            return null;
        }
        String str = value.toString().trim();
        return str.isEmpty() ? null : str;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
