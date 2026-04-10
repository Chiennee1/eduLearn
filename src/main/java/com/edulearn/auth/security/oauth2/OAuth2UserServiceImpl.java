package com.edulearn.auth.security.oauth2;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Service
@RequiredArgsConstructor
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final String GITHUB_EMAIL_API = "https://api.github.com/user/emails";

    private final RestClient restClient = RestClient.builder().build();
    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        if (!"github".equalsIgnoreCase(registrationId)) {
            return oauth2User;
        }

        Map<String, Object> attributes = new LinkedHashMap<>(oauth2User.getAttributes());
        String email = stringAttr(attributes, "email");
        if (!StringUtils.hasText(email)) {
            GithubEmailResult githubEmail = fetchGithubPrimaryEmail(userRequest.getAccessToken().getTokenValue());
            attributes.put("email", githubEmail.email());
            attributes.put("email_verified", githubEmail.verified());
        }

        String nameAttributeKey = resolveNameAttributeKey(userRequest, attributes);
        return new DefaultOAuth2User(oauth2User.getAuthorities(), attributes, nameAttributeKey);
    }

    private GithubEmailResult fetchGithubPrimaryEmail(String accessToken) {
        try {
            List<GithubEmailResponse> emails = restClient.get()
                    .uri(GITHUB_EMAIL_API)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<GithubEmailResponse>>() {
                    });

            if (emails == null || emails.isEmpty()) {
                throw invalidUserInfo("No email found in GitHub profile");
            }

            return emails.stream()
                    .filter(e -> StringUtils.hasText(e.email()))
                    .sorted((a, b) -> Boolean.compare(Boolean.TRUE.equals(b.primary()),
                            Boolean.TRUE.equals(a.primary())))
                    .findFirst()
                    .map(email -> new GithubEmailResult(email.email(), Boolean.TRUE.equals(email.verified())))
                    .orElseThrow(() -> invalidUserInfo("No usable email found in GitHub profile"));
        } catch (OAuth2AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("github_email_fetch_failed"),
                    "Unable to fetch email from GitHub",
                    ex);
        }
    }

    private OAuth2AuthenticationException invalidUserInfo(String message) {
        return new OAuth2AuthenticationException(new OAuth2Error("invalid_user_info"), message);
    }

    private String resolveNameAttributeKey(OAuth2UserRequest userRequest, Map<String, Object> attributes) {
        String configuredNameKey = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        if (StringUtils.hasText(configuredNameKey) && attributes.containsKey(configuredNameKey)) {
            return configuredNameKey;
        }
        if (attributes.containsKey("sub")) {
            return "sub";
        }
        if (attributes.containsKey("id")) {
            return "id";
        }
        if (attributes.containsKey("email")) {
            return "email";
        }
        throw invalidUserInfo("Unable to resolve OAuth2 user identifier");
    }

    private String stringAttr(Map<String, Object> attributes, String key) {
        Object value = attributes.get(key);
        if (value == null) {
            return null;
        }
        String str = value.toString().trim();
        return str.isEmpty() ? null : str;
    }

    private record GithubEmailResult(String email, boolean verified) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record GithubEmailResponse(String email, Boolean primary, Boolean verified) {
    }
}
