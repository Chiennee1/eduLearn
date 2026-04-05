package com.edulearn.course.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CertificateResponse {

    private final Long id;
    private final Long enrollmentId;
    private final String certificateCode;
    private final String pdfUrl;
    private final LocalDateTime issuedAt;
}

