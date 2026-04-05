package com.edulearn.course.listener;

import com.edulearn.course.event.CourseEnrolledEvent;
import com.edulearn.course.service.EnrollmentEmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EnrollmentWelcomeEmailListener {

    private final EnrollmentEmailService enrollmentEmailService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCourseEnrolled(CourseEnrolledEvent event) {
        enrollmentEmailService.sendWelcomeEnrollmentEmail(
                event.getStudentEmail(),
                event.getCourseTitle(),
                event.getAmountPaid()
        );
    }
}

