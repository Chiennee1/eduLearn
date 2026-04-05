package com.edulearn.course.service;

import com.edulearn.course.dto.CertificateResponse;
import com.edulearn.course.entity.CourseCertificate;
import com.edulearn.course.entity.Enrollment;
import com.edulearn.course.repository.CourseCertificateRepository;
import com.edulearn.exception.ResourceNotFoundException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CertificateService {

    private final CourseCertificateRepository courseCertificateRepository;

    @Transactional
    public CourseCertificate issueIfMissing(Enrollment enrollment) {
        return courseCertificateRepository.findByEnrollmentId(enrollment.getId())
                .orElseGet(() -> {
                    CourseCertificate certificate = CourseCertificate.builder()
                            .enrollment(enrollment)
                            .certificateCode(UUID.randomUUID().toString())
                            .build();
                    return courseCertificateRepository.save(certificate);
                });
    }

    @Transactional(readOnly = true)
    public CertificateResponse getByEnrollmentId(Long enrollmentId) {
        CourseCertificate certificate = courseCertificateRepository.findByEnrollmentId(enrollmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        return toResponse(certificate);
    }

    CertificateResponse toResponse(CourseCertificate certificate) {
        return CertificateResponse.builder()
                .id(certificate.getId())
                .enrollmentId(certificate.getEnrollment().getId())
                .certificateCode(certificate.getCertificateCode())
                .pdfUrl(certificate.getPdfUrl())
                .issuedAt(certificate.getIssuedAt())
                .build();
    }
}

