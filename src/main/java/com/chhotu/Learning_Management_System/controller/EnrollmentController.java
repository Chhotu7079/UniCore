
package com.chhotu.Learning_Management_System.controller;

import com.chhotu.Learning_Management_System.dto.StudentDto;
import com.chhotu.Learning_Management_System.entity.Enrollment;
import com.chhotu.Learning_Management_System.service.EnrollmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsible for handling student enrollment operations.
 * Includes endpoints to enroll students, view enrolled students, and remove enrollments.
 */
@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentController.class);

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
        logger.info("✅ EnrollmentController initialized successfully.");
    }

    /**
     * Enroll a student in a course.
     *
     * @param enrollment The enrollment object containing student and course details.
     * @param request    The HTTP request used to identify the logged-in user.
     * @return Response message indicating success or failure.
     */
    @PostMapping("/enroll")
    public ResponseEntity<String> enrollInCourse(@RequestBody Enrollment enrollment, HttpServletRequest request) {
        try {
            if (enrollment.getStudent() == null || enrollment.getCourse() == null) {
                throw new IllegalArgumentException("Student or Course information is missing in the request.");
            }

            logger.info("Attempting to enroll Student ID {} into Course ID {}",
                    enrollment.getStudent().getUserAccountId(),
                    enrollment.getCourse().getCourseId());

            enrollmentService.enrollInCourse(enrollment, request);

            logger.info("✅ Student ID {} successfully enrolled in Course ID {}",
                    enrollment.getStudent().getUserAccountId(),
                    enrollment.getCourse().getCourseId());

            return ResponseEntity.ok("Student enrolled successfully!");
        } catch (IllegalArgumentException e) {
            logger.error("❌ Enrollment failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieve all students enrolled in a specific course.
     *
     * @param courseId The ID of the course to fetch enrolled students.
     * @param request  The HTTP request for authentication context.
     * @return List of enrolled students or an error message.
     */
    @GetMapping("/view_enrolled_students/{courseId}")
    public ResponseEntity<?> viewEnrolledStudents(@PathVariable int courseId, HttpServletRequest request) {
        try {
            logger.info("Fetching enrolled students for Course ID {}", courseId);
            List<StudentDto> students = enrollmentService.viewEnrolledStudents(courseId, request);
            logger.info("Found {} enrolled students for Course ID {}", students.size(), courseId);
            return ResponseEntity.ok(students);
        } catch (IllegalArgumentException e) {
            logger.error("❌ Error fetching enrolled students for Course ID {}: {}", courseId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Remove a specific student from a specific course.
     *
     * @param studentId The ID of the student to remove.
     * @param courseId  The ID of the course.
     * @param request   The HTTP request for authentication context.
     * @return Response message indicating success or failure.
     */
    @DeleteMapping("/remove_enrolled_student/student_id/{studentId}/course_id/{courseId}")
    public ResponseEntity<String> removeEnrolledStudent(
            @PathVariable int studentId,
            @PathVariable int courseId,
            HttpServletRequest request) {
        try {
            logger.info("Attempting to remove Student ID {} from Course ID {}", studentId, courseId);
            enrollmentService.removeEnrolledStudent(courseId, studentId, request);
            logger.info("✅ Student ID {} removed successfully from Course ID {}", studentId, courseId);
            return ResponseEntity.ok("Student removed successfully.");
        } catch (IllegalArgumentException e) {
            logger.error("❌ Failed to remove Student ID {} from Course ID {}: {}", studentId, courseId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
