package com.edulearn.course.event;

import java.math.BigDecimal;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CourseEnrolledEvent extends ApplicationEvent {

    private final Long enrollmentId;
    private final String studentEmail;
    private final String courseTitle;
    private final BigDecimal amountPaid;

    public CourseEnrolledEvent(
            Object source,
            Long enrollmentId,
            String studentEmail,
            String courseTitle,
            BigDecimal amountPaid
    ) {
        super(source);
        this.enrollmentId = enrollmentId;
        this.studentEmail = studentEmail;
        this.courseTitle = courseTitle;
        this.amountPaid = amountPaid;
    }
}

