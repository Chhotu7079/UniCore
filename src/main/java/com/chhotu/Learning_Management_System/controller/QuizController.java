
package com.chhotu.Learning_Management_System.controller;

import com.chhotu.Learning_Management_System.dto.GradingDto;
import com.chhotu.Learning_Management_System.dto.QuestionDto;
import com.chhotu.Learning_Management_System.dto.QuizDto;
import com.chhotu.Learning_Management_System.service.QuizService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controller for managing quizzes, including creation, question banks,
 * grading, and tracking student quiz performance.
 */
@Slf4j
@RestController
@RequestMapping("/api/quiz")
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    /**
     * Get a quiz by its ID.
     */
    @GetMapping("/quiz_id/{id}")
    public ResponseEntity<?> getQuizById(@PathVariable int id, HttpServletRequest request) {
        try {
            log.info("Fetching quiz with ID: {}", id);
            QuizDto quizDTO = quizService.getQuizByID(id, request);
            return ResponseEntity.ok(quizDTO);
        } catch (Exception e) {
            log.error("Error fetching quiz with ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get the active quiz for a specific course.
     */
    @GetMapping("/active_quiz/{courseId}")
    public ResponseEntity<?> getActiveQuiz(@PathVariable("courseId") int courseId, HttpServletRequest request) {
        try {
            log.info("Fetching active quiz for course ID: {}", courseId);
            String quizId = quizService.getActiveQuiz(courseId, request);
            return ResponseEntity.ok(quizId);
        } catch (Exception e) {
            log.error("Error fetching active quiz for course ID {}: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Create a new quiz for a course.
     */
    @PostMapping("/add_quiz")
    public ResponseEntity<?> addQuiz(@RequestBody QuizDto quizDto, HttpServletRequest request) {
        try {
            log.info("Creating quiz for course ID: {}", quizDto.getCourseId());
            int quizId = quizService.Create(quizDto.getCourseId(), quizDto.getType(), request);
            return ResponseEntity.ok("Quiz created successfully. Use this ID: " + quizId + " to enter the quiz");
        } catch (Exception e) {
            log.error("Error creating quiz: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Create a question bank for a specific course.
     */
    @PostMapping("/add_questions_bank")
    public ResponseEntity<?> addQuestionsBank(@RequestBody QuizDto quizDto, HttpServletRequest request) {
        try {
            log.info("Creating question bank for course ID: {}", quizDto.getCourseId());
            quizService.createQuestionBank(quizDto.getCourseId(), quizDto.getQuestionList(), request);
            return ResponseEntity.ok("Question bank created successfully for the course ID: " + quizDto.getCourseId());
        } catch (Exception e) {
            log.error("Error creating question bank: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Add a new question to a quiz.
     */
    @PostMapping("/add_questions")
    public ResponseEntity<?> addQuestions(@RequestBody QuestionDto questionDto, HttpServletRequest request) {
        try {
            log.info("Adding question to course ID: {}", questionDto.getCourseId());
            quizService.addQuestion(questionDto, request);
            return ResponseEntity.ok("Question added successfully for the course ID: " + questionDto.getCourseId());
        } catch (Exception e) {
            log.error("Error adding question: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all questions in the question bank for a quiz.
     */
    @GetMapping("/get_question_bank/{id}")
    public ResponseEntity<?> getQuestionBank(@PathVariable int id, HttpServletRequest request) {
        try {
            log.info("Fetching question bank for quiz ID: {}", id);
            QuizDto quizDto = quizService.getQuestionBank(id, request);
            return ResponseEntity.ok(quizDto.getQuestionList());
        } catch (Exception e) {
            log.error("Error fetching question bank: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Grade a quiz submission.
     */
    @PostMapping("/grade_quiz")
    public ResponseEntity<?> gradeQuiz(@RequestBody GradingDto gradingDto, HttpServletRequest request) {
        try {
            log.info("Grading quiz for student ID: {}", gradingDto.getStudentId());
            quizService.gradeQuiz(gradingDto, request);
            return ResponseEntity.ok("Quiz has been graded for the student");
        } catch (Exception e) {
            log.error("Error grading quiz: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get a student's grade for a quiz.
     */
    @GetMapping("/get_quiz_grade/{quizId}/student/{studentId}")
    public ResponseEntity<?> getQuizGradeByStudent(@PathVariable int quizId, @PathVariable int studentId, HttpServletRequest request) {
        try {
            log.info("Fetching quiz grade for quiz ID {} and student ID {}", quizId, studentId);
            int grade = quizService.quizFeedback(quizId, studentId, request);
            return ResponseEntity.ok(grade);
        } catch (Exception e) {
            log.error("Error fetching quiz grade: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all questions in a quiz.
     */
    @GetMapping("/get_quiz_questions/{id}")
    public ResponseEntity<?> getQuizQuestions(@PathVariable int id, HttpServletRequest request) {
        try {
            log.info("Fetching quiz questions for quiz ID: {}", id);
            return ResponseEntity.ok(quizService.getQuizQuestions(id, request));
        } catch (Exception e) {
            log.error("Error fetching quiz questions: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Track quiz grades for a given quiz.
     */
    @GetMapping("/grades/{quizId}")
    public ResponseEntity<List<String>> trackQuizGrades(@PathVariable int quizId, HttpServletRequest request) {
        try {
            log.info("Tracking grades for quiz ID: {}", quizId);
            List<String> submissions = quizService.quizGrades(quizId, request);
            return ResponseEntity.ok(submissions);
        } catch (IllegalArgumentException e) {
            log.error("Error tracking quiz grades: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonList(e.getMessage()));
        }
    }
}
