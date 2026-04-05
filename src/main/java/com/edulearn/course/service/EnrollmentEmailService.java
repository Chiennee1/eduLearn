package com.edulearn.course.service;

import com.edulearn.course.config.EnrollmentEmailProperties;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollmentEmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final EnrollmentEmailProperties emailProperties;

    public void sendWelcomeEnrollmentEmail(String studentEmail, String courseTitle, BigDecimal amountPaid) {
        if (!emailProperties.isEnabled()) {
            log.info(
                    "Email disabled, skipping welcome email: studentEmail={}, courseTitle={}, amountPaid={}",
                    studentEmail,
                    courseTitle,
                    amountPaid
            );
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            log.warn("Email sender bean is unavailable, skipping welcome email to {}", studentEmail);
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailProperties.getFrom());
        message.setTo(studentEmail);
        message.setSubject("[EduLearn] Enrolled Successfully: " + courseTitle);
        message.setText(buildEnrollmentBody(courseTitle, amountPaid));

        try {
            mailSender.send(message);
            log.info("Enrollment welcome email sent to {}", studentEmail);
        } catch (MailException ex) {
            log.error("Failed to send enrollment welcome email to {}", studentEmail, ex);
        }
    }

    private String buildEnrollmentBody(String courseTitle, BigDecimal amountPaid) {
        return "Xin chao ban,\n\n"
                + "Ban da dang ky thanh cong khoa hoc: " + courseTitle + "\n"
                + "So tien thanh toan: " + amountPaid + "\n\n"
                + "Chuc ban hoc tap hieu qua voi EduLearn!";
    }
}

