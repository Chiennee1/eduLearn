package com.edulearn.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.logging.execution")
public class ExecutionLogProperties {

    private boolean enabled = true;
    private long warnThresholdMs = 500;
}

