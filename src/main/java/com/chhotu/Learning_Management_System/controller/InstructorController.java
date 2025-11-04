
package com.chhotu.Learning_Management_System.controller;

import com.chhotu.Learning_Management_System.entity.Instructor;
import com.chhotu.Learning_Management_System.service.InstructorService;
import com.chhotu.Learning_Management_System.service.NotificationsService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing instructor-related operations.
 * Handles updating instructor profiles and fetching notifications.
 */
@RestController
@RequestMapping("/api/instructor")
public class InstructorController {

    private static final Logger logger = LoggerFactory.getLogger(InstructorController.class);

    private final InstructorService instructorService;
    private final NotificationsService notificationsService;

    /**
     * Constructor for injecting dependencies.
     */
    public InstructorController(InstructorService instructorService, NotificationsService notificationsService) {
        this.instructorService = instructorService;
        this.notificationsService = notificationsService;
        logger.info("InstructorController initialized");
    }

    /**
     * Updates the profile of an instructor.
     *
     * @param instructorId the ID of the instructor
     * @param instructor   the updated instructor details
     * @param request      the HTTP request for context (authentication, etc.)
     * @return success or error response
     */
    @PutMapping("/update_profile/{instructorId}")
    public ResponseEntity<String> updateInstructorProfile(
            @PathVariable int instructorId,
            @RequestBody Instructor instructor,
            HttpServletRequest request
    ) {
        logger.info("Received request to update instructor profile with ID: {}", instructorId);
        try {
            instructorService.save(instructorId, instructor, request);
            logger.info("Instructor (ID: {}) updated successfully.", instructorId);
            return ResponseEntity.ok("Instructor updated successfully.");
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to update instructor (ID: {}): {}", instructorId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error while updating instructor (ID: {})", instructorId, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while updating instructor.");
        }
    }

    /**
     * Retrieves all notifications for a specific instructor.
     *
     * @param userId  the instructor's user ID
     * @param request the HTTP request for context
     * @return list of all notifications
     */
    @GetMapping("/notifications/{userId}")
    public ResponseEntity<?> getAllNotifications(@PathVariable int userId, HttpServletRequest request) {
        logger.info("Fetching all notifications for userId: {}", userId);
        try {
            List<String> notifications = notificationsService.getAllNotifications(userId, request);
            logger.info("Fetched {} notifications for userId: {}", notifications.size(), userId);
            return ResponseEntity.ok(notifications);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to fetch notifications for userId {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error while fetching notifications for userId {}", userId, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while fetching notifications.");
        }
    }

    /**
     * Retrieves unread notifications for a specific instructor.
     *
     * @param userId  the instructor's user ID
     * @param request the HTTP request for context
     * @return list of unread notifications
     */
    @GetMapping("/unreadnotifications/{userId}")
    public ResponseEntity<?> getUnreadNotifications(@PathVariable int userId, HttpServletRequest request) {
        logger.info("Fetching unread notifications for userId: {}", userId);
        try {
            List<String> unreadNotifications = notificationsService.getAllUnreadNotifications(userId, request);
            logger.info("Fetched {} unread notifications for userId: {}", unreadNotifications.size(), userId);
            return ResponseEntity.ok(unreadNotifications);
        } catch (IllegalArgumentException e) {
            logger.warn("Failed to fetch unread notifications for userId {}: {}", userId, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error while fetching unread notifications for userId {}", userId, e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while fetching unread notifications.");
        }
    }
}
