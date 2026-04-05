package com.edulearn.course.repository;

import com.edulearn.course.entity.CourseCertificate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseCertificateRepository extends JpaRepository<CourseCertificate, Long> {

    Optional<CourseCertificate> findByEnrollmentId(Long enrollmentId);

    boolean existsByEnrollmentId(Long enrollmentId);
}

