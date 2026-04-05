package com.edulearn.course.service;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EnrollmentEmailService {

    public void sendWelcomeEnrollmentEmail(String studentEmail, String courseTitle, BigDecimal amountPaid) {
        // Placeholder for real email integration (SMTP/provider API).
        log.info(
                "Welcome email queued: studentEmail={}, courseTitle={}, amountPaid={}",
                studentEmail,
                courseTitle,
                amountPaid
        );
    }
}

