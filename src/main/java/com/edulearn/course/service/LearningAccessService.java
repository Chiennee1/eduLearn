package com.edulearn.course.service;

import com.edulearn.auth.entity.RoleName;
import com.edulearn.auth.entity.User;
import com.edulearn.auth.repository.UserRepository;
import com.edulearn.exception.BusinessException;
import com.edulearn.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LearningAccessService {

    private final UserRepository userRepository;

    public User getActor(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public void requireStudent(User user) {
        boolean isStudent = user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.STUDENT);
        if (!isStudent) {
            throw new BusinessException("Only STUDENT can perform this action", HttpStatus.FORBIDDEN);
        }
    }
}

