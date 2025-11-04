
package com.chhotu.Learning_Management_System.controller;

import com.chhotu.Learning_Management_System.entity.Student;
import com.chhotu.Learning_Management_System.service.NotificationsService;
import com.chhotu.Learning_Management_System.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing student profile updates and notifications.
 */
@Slf4j
@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;
    private final NotificationsService notificationsService;

    public StudentController(StudentService studentService, NotificationsService notificationsService) {
        this.studentService = studentService;
        this.notificationsService = notificationsService;
    }

    /**
     * Updates a student's profile details.
     *
     * @param studentId ID of the student
     * @param student   Updated student entity
     * @param request   HTTP request for user context
     * @return Success or error response
     */
    @PutMapping("/update-profile/{studentId}")
    public ResponseEntity<String> updateStudentProfile(
            @PathVariable int studentId,
            @RequestBody Student student,
            HttpServletRequest request
    ) {
        try {
            log.info("Updating profile for student ID: {}", studentId);
            studentService.save(studentId, student, request);
            return ResponseEntity.ok("Student updated successfully.");
        } catch (IllegalArgumentException e) {
            log.warn("Invalid data while updating student {}: {}", studentId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while updating student {}: {}", studentId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to update student profile.");
        }
    }

    /**
     * Retrieves all notifications for a given user.
     *
     * @param userId  User ID
     * @param request HTTP request for context
     * @return List of all notifications
     */
    @GetMapping("/notifications/all/{userId}")
    public ResponseEntity<?> getAllNotifications(@PathVariable int userId, HttpServletRequest request) {
        try {
            log.info("Fetching all notifications for user ID: {}", userId);
            List<String> notifications = notificationsService.getAllNotifications(userId, request);
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            log.error("Error fetching all notifications for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to retrieve notifications.");
        }
    }

    /**
     * Retrieves only unread notifications for a given user.
     *
     * @param userId  User ID
     * @param request HTTP request for context
     * @return List of unread notifications
     */
    @GetMapping("/notifications/unread/{userId}")
    public ResponseEntity<?> getUnreadNotifications(@PathVariable int userId, HttpServletRequest request) {
        try {
            log.info("Fetching unread notifications for user ID: {}", userId);
            List<String> unreadNotifications = notificationsService.getAllUnreadNotifications(userId, request);
            return ResponseEntity.ok(unreadNotifications);
        } catch (Exception e) {
            log.error("Error fetching unread notifications for user {}: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to retrieve unread notifications.");
        }
    }
}
