package com.edulearn.auth.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSummaryResponse {

    private final Long id;
    private final String email;
    private final String fullName;
    private final Set<String> roles;
}
