package com.edulearn.course.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.email")
public class EnrollmentEmailProperties {

    private boolean enabled = false;
    private String from = "noreply@edulearn.com";
}

