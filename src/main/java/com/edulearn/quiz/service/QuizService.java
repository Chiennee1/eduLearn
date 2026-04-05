package com.edulearn.quiz.service;

import com.edulearn.auth.entity.RoleName;
import com.edulearn.auth.entity.User;
import com.edulearn.auth.repository.UserRepository;
import com.edulearn.common.PageResponse;
import com.edulearn.course.entity.Lesson;
import com.edulearn.course.repository.EnrollmentRepository;
import com.edulearn.course.repository.LessonRepository;
import com.edulearn.course.service.CoursePermissionService;
import com.edulearn.exception.BusinessException;
import com.edulearn.exception.ResourceNotFoundException;
import com.edulearn.quiz.dto.QuizAttemptResponse;
import com.edulearn.quiz.dto.QuizCreateRequest;
import com.edulearn.quiz.dto.QuizOptionRequest;
import com.edulearn.quiz.dto.QuizOptionUpdateRequest;
import com.edulearn.quiz.dto.QuizOptionResponse;
import com.edulearn.quiz.dto.QuizQuestionRequest;
import com.edulearn.quiz.dto.QuizQuestionResponse;
import com.edulearn.quiz.dto.QuizQuestionUpdateRequest;
import com.edulearn.quiz.dto.QuizResponse;
import com.edulearn.quiz.dto.QuizSubmitAnswerRequest;
import com.edulearn.quiz.dto.QuizSubmitRequest;
import com.edulearn.quiz.dto.QuizUpdateRequest;
import com.edulearn.quiz.entity.AnswerOption;
import com.edulearn.quiz.entity.Question;
import com.edulearn.quiz.entity.QuestionType;
import com.edulearn.quiz.entity.Quiz;
import com.edulearn.quiz.entity.QuizAnswer;
import com.edulearn.quiz.entity.QuizAttempt;
import com.edulearn.quiz.repository.AnswerOptionRepository;
import com.edulearn.quiz.repository.QuestionRepository;
import com.edulearn.quiz.repository.QuizAnswerRepository;
import com.edulearn.quiz.repository.QuizAttemptRepository;
import com.edulearn.quiz.repository.QuizRepository;
import com.edulearn.quiz.spec.QuizAttemptSpecifications;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final AnswerOptionRepository answerOptionRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final LessonRepository lessonRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final CoursePermissionService coursePermissionService;

    @Transactional
    public QuizResponse create(Long lessonId, QuizCreateRequest request, String actorEmail) {
        User actor = getInstructorOrAdminActor(actorEmail);

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson not found"));
        coursePermissionService.requireCourseWriteAccess(actor, lesson.getSection().getCourse());

        Quiz quiz = Quiz.builder()
                .lesson(lesson)
                .title(request.getTitle().trim())
                .passScore(request.getPassScore() == null ? 70 : request.getPassScore())
                .timeLimitMins(request.getTimeLimitMins())
                .build();
        quiz = quizRepository.save(quiz);

        persistQuestions(quiz, request.getQuestions());
        return getById(quiz.getId());
    }

    @Transactional
    public QuizResponse update(Long quizId, QuizUpdateRequest request, String actorEmail) {
        User actor = getInstructorOrAdminActor(actorEmail);
        Quiz quiz = getQuizEntity(quizId);
        coursePermissionService.requireCourseWriteAccess(actor, quiz.getLesson().getSection().getCourse());

        quiz.setTitle(request.getTitle().trim());
        quiz.setPassScore(request.getPassScore());
        quiz.setTimeLimitMins(request.getTimeLimitMins());
        quizRepository.save(quiz);
        return getById(quizId);
    }

    @Transactional
    public void delete(Long quizId, String actorEmail) {
        User actor = getInstructorOrAdminActor(actorEmail);
        Quiz quiz = getQuizEntity(quizId);
        coursePermissionService.requireCourseWriteAccess(actor, quiz.getLesson().getSection().getCourse());
        quizRepository.delete(quiz);
    }

    @Transactional
    public QuizResponse updateQuestion(
            Long quizId,
            Long questionId,
            QuizQuestionUpdateRequest request,
            String actorEmail
    ) {
        User actor = getInstructorOrAdminActor(actorEmail);
        Quiz quiz = getQuizEntity(quizId);
        coursePermissionService.requireCourseWriteAccess(actor, quiz.getLesson().getSection().getCourse());

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        if (!question.getQuiz().getId().equals(quizId)) {
            throw new ResourceNotFoundException("Question not found");
        }

        question.setContent(request.getContent().trim());
        question.setType(request.getType());
        question.setPoints(request.getPoints());
        question.setOrderIndex(request.getOrderIndex() == null ? question.getOrderIndex() : request.getOrderIndex());
        questionRepository.save(question);

        validateQuestionOptionState(question);
        return getById(quizId);
    }

    @Transactional
    public QuizResponse updateOption(
            Long quizId,
            Long questionId,
            Long optionId,
            QuizOptionUpdateRequest request,
            String actorEmail
    ) {
        User actor = getInstructorOrAdminActor(actorEmail);
        Quiz quiz = getQuizEntity(quizId);
        coursePermissionService.requireCourseWriteAccess(actor, quiz.getLesson().getSection().getCourse());

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        if (!question.getQuiz().getId().equals(quizId)) {
            throw new ResourceNotFoundException("Question not found");
        }

        AnswerOption option = answerOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found"));
        if (!option.getQuestion().getId().equals(questionId)) {
            throw new ResourceNotFoundException("Option not found");
        }

        option.setContent(request.getContent().trim());
        if (request.getOrderIndex() != null) {
            option.setOrderIndex(request.getOrderIndex());
        }
        if (request.getCorrect() != null) {
            if (question.getType() == QuestionType.SINGLE && request.getCorrect()) {
                List<AnswerOption> options = answerOptionRepository.findByQuestionIdOrderByOrderIndexAscIdAsc(questionId);
                for (AnswerOption each : options) {
                    each.setCorrect(each.getId().equals(optionId));
                }
                answerOptionRepository.saveAll(options);
            } else {
                option.setCorrect(request.getCorrect());
                answerOptionRepository.save(option);
            }
        } else {
            answerOptionRepository.save(option);
        }

        validateQuestionOptionState(question);
        return getById(quizId);
    }

    @Transactional
    public void deleteQuestion(Long quizId, Long questionId, String actorEmail) {
        User actor = getInstructorOrAdminActor(actorEmail);
        Quiz quiz = getQuizEntity(quizId);
        coursePermissionService.requireCourseWriteAccess(actor, quiz.getLesson().getSection().getCourse());

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        if (!question.getQuiz().getId().equals(quizId)) {
            throw new ResourceNotFoundException("Question not found");
        }

        if (questionRepository.countByQuizId(quizId) <= 1) {
            throw new BusinessException("Quiz must have at least one question", HttpStatus.BAD_REQUEST);
        }
        if (quizAnswerRepository.existsByQuestion_Id(questionId)) {
            throw new BusinessException("Question already has attempt answers and cannot be deleted", HttpStatus.CONFLICT);
        }

        List<AnswerOption> options = answerOptionRepository.findByQuestionIdOrderByOrderIndexAscIdAsc(questionId);
        if (!options.isEmpty()) {
            answerOptionRepository.deleteAll(options);
        }
        questionRepository.delete(question);
    }

    @Transactional
    public void deleteOption(Long quizId, Long questionId, Long optionId, String actorEmail) {
        User actor = getInstructorOrAdminActor(actorEmail);
        Quiz quiz = getQuizEntity(quizId);
        coursePermissionService.requireCourseWriteAccess(actor, quiz.getLesson().getSection().getCourse());

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found"));
        if (!question.getQuiz().getId().equals(quizId)) {
            throw new ResourceNotFoundException("Question not found");
        }

        AnswerOption option = answerOptionRepository.findById(optionId)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found"));
        if (!option.getQuestion().getId().equals(questionId)) {
            throw new ResourceNotFoundException("Option not found");
        }

        if (quizAnswerRepository.existsBySelectedOption_Id(optionId)) {
            throw new BusinessException("Option already used in attempts and cannot be deleted", HttpStatus.CONFLICT);
        }

        long optionCount = answerOptionRepository.countByQuestionId(questionId);
        if (optionCount <= 1) {
            throw new BusinessException("Question must have at least one option", HttpStatus.BAD_REQUEST);
        }

        answerOptionRepository.delete(option);
        validateQuestionOptionState(question);
    }

    @Transactional(readOnly = true)
    public QuizResponse getById(Long quizId) {
        Quiz quiz = getQuizEntity(quizId);
        List<Question> questions = questionRepository.findByQuizIdOrderByOrderIndexAscIdAsc(quizId);

        List<QuizQuestionResponse> questionResponses = new ArrayList<>();
        for (Question question : questions) {
            List<AnswerOption> options = answerOptionRepository.findByQuestionIdOrderByOrderIndexAscIdAsc(question.getId());
            List<QuizOptionResponse> optionResponses = options.stream()
                    .map(option -> QuizOptionResponse.builder()
                            .id(option.getId())
                            .content(option.getContent())
                            .orderIndex(option.getOrderIndex())
                            .build())
                    .toList();

            questionResponses.add(QuizQuestionResponse.builder()
                    .id(question.getId())
                    .content(question.getContent())
                    .type(question.getType())
                    .points(question.getPoints())
                    .orderIndex(question.getOrderIndex())
                    .options(optionResponses)
                    .build());
        }

        return QuizResponse.builder()
                .id(quiz.getId())
                .lessonId(quiz.getLesson().getId())
                .title(quiz.getTitle())
                .passScore(quiz.getPassScore())
                .timeLimitMins(quiz.getTimeLimitMins())
                .questions(questionResponses)
                .build();
    }

    @Transactional
    public QuizAttemptResponse submit(Long quizId, QuizSubmitRequest request, String actorEmail) {
        User actor = getStudentActor(actorEmail);
        Quiz quiz = getQuizEntity(quizId);

        Long courseId = quiz.getLesson().getSection().getCourse().getId();
        if (!enrollmentRepository.existsByUserIdAndCourseId(actor.getId(), courseId)) {
            throw new BusinessException("You must enroll before submitting quiz", HttpStatus.FORBIDDEN);
        }

        List<Question> questions = questionRepository.findByQuizIdOrderByOrderIndexAscIdAsc(quizId);
        if (questions.isEmpty()) {
            throw new BusinessException("Quiz has no questions", HttpStatus.BAD_REQUEST);
        }

        Map<Long, Long> answerMap = new LinkedHashMap<>();
        for (QuizSubmitAnswerRequest answer : request.getAnswers()) {
            answerMap.put(answer.getQuestionId(), answer.getSelectedOptionId());
        }

        QuizAttempt attempt = QuizAttempt.builder()
                .user(actor)
                .quiz(quiz)
                .build();
        attempt = quizAttemptRepository.save(attempt);

        int totalPoints = 0;
        int earnedPoints = 0;
        List<QuizAnswer> attemptAnswers = new ArrayList<>();

        for (Question question : questions) {
            totalPoints += question.getPoints();
            Long selectedOptionId = answerMap.get(question.getId());
            AnswerOption selectedOption = null;

            if (selectedOptionId != null) {
                selectedOption = answerOptionRepository.findById(selectedOptionId)
                        .filter(option -> option.getQuestion().getId().equals(question.getId()))
                        .orElseThrow(() -> new BusinessException("Invalid option for question", HttpStatus.BAD_REQUEST));
                if (selectedOption.isCorrect()) {
                    earnedPoints += question.getPoints();
                }
            }

            attemptAnswers.add(QuizAnswer.builder()
                    .attempt(attempt)
                    .question(question)
                    .selectedOption(selectedOption)
                    .build());
        }

        int score = totalPoints == 0 ? 0 : (int) Math.round((earnedPoints * 100.0) / totalPoints);
        attempt.setScore(score);
        attempt.setPassed(score >= quiz.getPassScore());
        attempt.setSubmittedAt(LocalDateTime.now());
        attempt = quizAttemptRepository.save(attempt);

        quizAnswerRepository.saveAll(attemptAnswers);
        return toAttemptResponse(attempt);
    }

    @Transactional(readOnly = true)
    public PageResponse<QuizAttemptResponse> getMyHistory(
            String actorEmail,
            Long quizId,
            Boolean passed,
            LocalDateTime startedFrom,
            LocalDateTime startedTo,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        User actor = getStudentActor(actorEmail);

        Specification<QuizAttempt> specification = Specification.allOf(
                QuizAttemptSpecifications.hasUserId(actor.getId()),
                QuizAttemptSpecifications.hasQuizId(quizId),
                QuizAttemptSpecifications.hasPassed(passed),
                QuizAttemptSpecifications.startedAtFrom(startedFrom),
                QuizAttemptSpecifications.startedAtTo(startedTo)
        );

        Pageable pageable = PageRequest.of(page, size, buildHistorySort(sortBy, sortDir));
        Page<QuizAttempt> sourcePage = quizAttemptRepository.findAll(specification, pageable);
        List<QuizAttemptResponse> content = sourcePage.getContent().stream()
                .map(this::toAttemptResponse)
                .toList();
        Page<QuizAttemptResponse> mappedPage = new org.springframework.data.domain.PageImpl<>(
                content,
                pageable,
                sourcePage.getTotalElements()
        );
        return PageResponse.from(mappedPage);
    }

    private Quiz getQuizEntity(Long quizId) {
        return quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
    }

    private User getStudentActor(String actorEmail) {
        User actor = userRepository.findByEmail(actorEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isStudent = actor.getRoles().stream().anyMatch(role -> role.getName() == RoleName.STUDENT);
        if (!isStudent) {
            throw new BusinessException("Only STUDENT can perform this action", HttpStatus.FORBIDDEN);
        }
        return actor;
    }

    private User getInstructorOrAdminActor(String actorEmail) {
        User actor = coursePermissionService.getActor(actorEmail);
        coursePermissionService.requireInstructorOrAdmin(actor);
        return actor;
    }

    private void persistQuestions(Quiz quiz, List<QuizQuestionRequest> requests) {
        for (QuizQuestionRequest request : requests) {
            validateQuestionRequest(request);

            Question question = Question.builder()
                    .quiz(quiz)
                    .content(request.getContent().trim())
                    .type(request.getType())
                    .points(request.getPoints() == null ? 10 : request.getPoints())
                    .orderIndex(request.getOrderIndex() == null ? 0 : request.getOrderIndex())
                    .build();
            question = questionRepository.save(question);

            List<AnswerOption> options = new ArrayList<>();
            for (QuizOptionRequest optionRequest : request.getOptions()) {
                options.add(AnswerOption.builder()
                        .question(question)
                        .content(optionRequest.getContent().trim())
                        .correct(Boolean.TRUE.equals(optionRequest.getCorrect()))
                        .orderIndex(optionRequest.getOrderIndex() == null ? 0 : optionRequest.getOrderIndex())
                        .build());
            }
            answerOptionRepository.saveAll(options);
        }
    }

    private void validateQuestionRequest(QuizQuestionRequest request) {
        int correctCount = 0;
        for (QuizOptionRequest option : request.getOptions()) {
            if (Boolean.TRUE.equals(option.getCorrect())) {
                correctCount++;
            }
        }

        if (correctCount == 0) {
            throw new BusinessException("Each question must have at least one correct option", HttpStatus.BAD_REQUEST);
        }

        if (request.getType() == QuestionType.SINGLE && correctCount != 1) {
            throw new BusinessException("SINGLE question must have exactly one correct option", HttpStatus.BAD_REQUEST);
        }

        if (request.getType() == QuestionType.TRUE_FALSE && request.getOptions().size() != 2) {
            throw new BusinessException("TRUE_FALSE question must have exactly two options", HttpStatus.BAD_REQUEST);
        }
    }

    private void validateQuestionOptionState(Question question) {
        List<AnswerOption> options = answerOptionRepository.findByQuestionIdOrderByOrderIndexAscIdAsc(question.getId());
        long correctCount = options.stream().filter(AnswerOption::isCorrect).count();

        if (options.isEmpty()) {
            throw new BusinessException("Question must have options", HttpStatus.BAD_REQUEST);
        }
        if (correctCount == 0) {
            throw new BusinessException("Question must have at least one correct option", HttpStatus.BAD_REQUEST);
        }
        if (question.getType() == QuestionType.SINGLE && correctCount != 1) {
            throw new BusinessException("SINGLE question must have exactly one correct option", HttpStatus.BAD_REQUEST);
        }
        if (question.getType() == QuestionType.TRUE_FALSE && options.size() != 2) {
            throw new BusinessException("TRUE_FALSE question must have exactly two options", HttpStatus.BAD_REQUEST);
        }
    }

    private Sort buildHistorySort(String sortBy, String sortDir) {
        String normalizedSortBy = switch (sortBy == null ? "startedAt" : sortBy) {
            case "score" -> "score";
            case "submittedAt" -> "submittedAt";
            default -> "startedAt";
        };

        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, normalizedSortBy);
    }

    private QuizAttemptResponse toAttemptResponse(QuizAttempt attempt) {
        return QuizAttemptResponse.builder()
                .id(attempt.getId())
                .quizId(attempt.getQuiz().getId())
                .quizTitle(attempt.getQuiz().getTitle())
                .score(attempt.getScore())
                .passed(attempt.isPassed())
                .startedAt(attempt.getStartedAt())
                .submittedAt(attempt.getSubmittedAt())
                .build();
    }
}

