
package com.chhotu.Learning_Management_System.controller;

import com.chhotu.Learning_Management_System.dto.CourseDto;
import com.chhotu.Learning_Management_System.entity.Course;
import com.chhotu.Learning_Management_System.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Controller class for handling Course-related operations.
 * Provides endpoints for CRUD operations and media uploads.
 */
@RestController
@RequestMapping("/api/course")
public class CourseController {

    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);

    private final CourseService courseService;

    // Constructor-based dependency injection
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
        logger.info("CourseController initialized successfully.");
    }

    /**
     * Create a new course.
     *
     * @param course  The Course object containing details.
     * @param request HttpServletRequest to identify the logged-in instructor.
     * @return ResponseEntity with status and message.
     */
    @PostMapping("/add_course")
    public ResponseEntity<String> addCourse(@RequestBody Course course, HttpServletRequest request) {
        try {
            logger.info("Request received to add course: {}", course.getCourseName());
            courseService.addCourse(course, request, course.getInstructorId().getUserAccountId());
            logger.info("Course '{}' added successfully.", course.getCourseName());
            return ResponseEntity.ok("Course created successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to create course: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieve course details by ID.
     */
    @GetMapping("/course_id/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable int id, HttpServletRequest request) {
        try {
            logger.info("Fetching course details for ID: {}", id);
            CourseDto courseDTO = courseService.getCourseById(id, request);
            logger.info("Course details retrieved successfully for ID: {}", id);
            return ResponseEntity.ok(courseDTO);
        } catch (IllegalArgumentException e) {
            logger.error("Error fetching course ID {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Get all available courses.
     */
    @GetMapping("/all_courses")
    public ResponseEntity<?> getAllCourses(HttpServletRequest request) {
        try {
            logger.info("Fetching all courses.");
            List<CourseDto> courseDTOList = courseService.getAllCourses(request);
            logger.info("Retrieved {} courses successfully.", courseDTOList.size());
            return ResponseEntity.ok(courseDTOList);
        } catch (IllegalArgumentException e) {
            logger.error("Error fetching all courses: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Update course details and notify enrolled students.
     */
    @PutMapping("/update/course_id/{courseId}")
    public ResponseEntity<String> updateCourse(
            @PathVariable int courseId,
            @RequestBody Course updatedCourse,
            HttpServletRequest request) {
        try {
            logger.info("Request received to update course ID: {}", courseId);
            courseService.sendNotificationsToEnrolledStudents(courseId, request);
            courseService.updateCourse(courseId, updatedCourse, request);
            logger.info("Course ID {} updated successfully.", courseId);
            return ResponseEntity.ok("Course updated successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to update course ID {}: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete a course by ID.
     */
    @DeleteMapping("/delete/course_id/{courseId}")
    public ResponseEntity<String> deleteCourse(@PathVariable int courseId, HttpServletRequest request) {
        try {
            logger.info("Request received to delete course ID: {}", courseId);
            courseService.deleteCourse(courseId, request);
            logger.info("Course ID {} deleted successfully.", courseId);
            return ResponseEntity.ok("Course deleted successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Failed to delete course ID {}: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Upload media file for a course.
     */
    @PostMapping("/upload_media/{courseId}")
    public ResponseEntity<String> uploadMedia(@PathVariable int courseId,
                                              @RequestParam("file") MultipartFile file,
                                              HttpServletRequest request) {
        try {
            logger.info("Uploading media for course ID: {} | File: {}", courseId, file.getOriginalFilename());
            courseService.uploadMediaFile(courseId, file, request);
            logger.info("File '{}' uploaded successfully for course ID: {}", file.getOriginalFilename(), courseId);
            return ResponseEntity.ok("File uploaded successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("Invalid media upload for course ID {}: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            logger.error("Unexpected error while uploading media for course ID {}: {}", courseId, e.getMessage());
            return ResponseEntity.internalServerError().body("Internal server error: " + e.getMessage());
        }
    }
}
