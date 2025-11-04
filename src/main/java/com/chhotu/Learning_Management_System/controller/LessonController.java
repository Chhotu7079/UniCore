
package com.chhotu.Learning_Management_System.controller;

import com.chhotu.Learning_Management_System.dto.LessonDto;
import com.chhotu.Learning_Management_System.entity.Lesson;
import com.chhotu.Learning_Management_System.service.LessonService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * Controller for managing course lessons.
 * Handles adding, updating, deleting, fetching lessons, and tracking attendance.
 */
@RestController
@RequestMapping("/api/lesson")
public class LessonController {

    private static final Logger logger = LoggerFactory.getLogger(LessonController.class);

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
        logger.info("LessonController initialized successfully");
    }

    /**
     * Adds a new lesson to a course.
     */
    @PostMapping("/add_lesson")
    public ResponseEntity<String> addLesson(@RequestBody Lesson lesson, HttpServletRequest request) {
        logger.info("Received request to add lesson for courseId: {}",
                lesson.getCourseId() != null ? lesson.getCourseId().getCourseId() : "N/A");
        try {
            lessonService.addLesson(lesson, request);
            logger.info("Lesson '{}' added successfully.", lesson.getLessonName());
            return ResponseEntity.ok("Lesson added successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to add lesson: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while adding lesson", e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while adding the lesson.");
        }
    }

    /**
     * Retrieves all lessons for a given course.
     */
    @GetMapping("/get_all_lessons/{courseId}")
    public ResponseEntity<?> getAllLessons(@PathVariable int courseId, HttpServletRequest request) {
        logger.info("Fetching all lessons for courseId: {}", courseId);
        try {
            List<LessonDto> lessons = lessonService.getLessonsByCourseId(courseId, request);
            logger.info("Retrieved {} lessons for courseId {}", lessons.size(), courseId);
            return ResponseEntity.ok(lessons);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to fetch lessons for courseId {}: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while fetching lessons for courseId {}", courseId, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while fetching lessons.");
        }
    }

    /**
     * Retrieves a lesson by its ID.
     */
    @GetMapping("/lesson_id/{lessonId}")
    public ResponseEntity<?> getLessonById(@PathVariable int lessonId, HttpServletRequest request) {
        logger.info("Fetching lesson by ID: {}", lessonId);
        try {
            LessonDto lesson = lessonService.getLessonById(lessonId, request);
            logger.info("Lesson fetched successfully for lessonId: {}", lessonId);
            return ResponseEntity.ok(lesson);
        } catch (IllegalArgumentException e) {
            logger.warn("Lesson fetch failed for lessonId {}: {}", lessonId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while fetching lesson {}", lessonId, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while fetching the lesson.");
        }
    }

    /**
     * Updates an existing lesson.
     */
    @PutMapping("/update/lesson_id/{lessonId}")
    public ResponseEntity<String> updateLesson(
            @PathVariable int lessonId,
            @RequestBody Lesson updatedLesson,
            HttpServletRequest request) {
        logger.info("Received request to update lessonId: {}", lessonId);
        try {
            lessonService.updateLesson(lessonId, updatedLesson, request);
            logger.info("Lesson updated successfully for lessonId: {}", lessonId);
            return ResponseEntity.ok("Lesson updated successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("Lesson update failed for lessonId {}: {}", lessonId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while updating lesson {}", lessonId, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while updating the lesson.");
        }
    }

    /**
     * Deletes a lesson from a course.
     */
    @DeleteMapping("/delete/lesson_id/{lessonId}/course_id/{courseId}")
    public ResponseEntity<String> deleteLesson(
            @PathVariable int lessonId,
            @PathVariable int courseId,
            HttpServletRequest request) {
        logger.info("Deleting lessonId: {} from courseId: {}", lessonId, courseId);
        try {
            lessonService.deleteLesson(lessonId, courseId, request);
            logger.info("Lesson (ID: {}) deleted successfully from course (ID: {}).", lessonId, courseId);
            return ResponseEntity.ok("Lesson deleted successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to delete lessonId {} from courseId {}: {}", lessonId, courseId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while deleting lessonId {} from courseId {}", lessonId, courseId, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while deleting the lesson.");
        }
    }

    /**
     * Handles student entry into a lesson using OTP verification.
     */
    @PostMapping("/student_enter_lesson/course_id/{courseId}/lesson_id/{lessonId}/otp/{otp}")
    public ResponseEntity<String> studentEnterLesson(
            @PathVariable int courseId,
            @PathVariable int lessonId,
            @PathVariable String otp,
            HttpServletRequest request) {
        logger.info("Student attempting to enter lessonId: {} in courseId: {} with OTP: {}", lessonId, courseId, otp);
        try {
            lessonService.studentEnterLesson(courseId, lessonId, otp, request);
            logger.info("Student successfully entered lesson (ID: {})", lessonId);
            return ResponseEntity.ok("Student entered lesson successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("Failed student entry for lessonId {}: {}", lessonId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during student entry for lessonId {}", lessonId, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while entering the lesson.");
        }
    }

    /**
     * Tracks lesson attendance for a given lesson ID.
     */
    @GetMapping("/attendances/{lessonId}")
    public ResponseEntity<List<String>> trackLessonAttendances(@PathVariable int lessonId, HttpServletRequest request) {
        logger.info("Tracking attendance for lessonId: {}", lessonId);
        try {
            List<String> submissions = lessonService.lessonAttendance(lessonId, request);
            logger.info("Attendance retrieved for lessonId: {} ({} entries)", lessonId, submissions.size());
            return ResponseEntity.ok(submissions);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to retrieve attendance for lessonId {}: {}", lessonId, e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonList(e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error while tracking attendance for lessonId {}", lessonId, e);
            return ResponseEntity.internalServerError().body(Collections.singletonList("An unexpected error occurred while fetching attendance."));
        }
    }
}
