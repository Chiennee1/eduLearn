package com.edulearn.course.service;

import com.edulearn.auth.entity.RoleName;
import com.edulearn.auth.entity.User;
import com.edulearn.auth.repository.UserRepository;
import com.edulearn.course.entity.Course;
import com.edulearn.exception.BusinessException;
import com.edulearn.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CoursePermissionService {

    private final UserRepository userRepository;

    public User getActor(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public boolean isAdmin(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.ADMIN);
    }

    public boolean isInstructor(User user) {
        return user.getRoles().stream().anyMatch(role -> role.getName() == RoleName.INSTRUCTOR);
    }

    public void requireInstructorOrAdmin(User user) {
        if (!isAdmin(user) && !isInstructor(user)) {
            throw new BusinessException("Only INSTRUCTOR or ADMIN can perform this action", HttpStatus.FORBIDDEN);
        }
    }

    public void requireCourseWriteAccess(User user, Course course) {
        if (isAdmin(user)) {
            return;
        }
        if (isInstructor(user) && course.getInstructor().getId().equals(user.getId())) {
            return;
        }
        throw new BusinessException("You do not have permission on this course", HttpStatus.FORBIDDEN);
    }
}

