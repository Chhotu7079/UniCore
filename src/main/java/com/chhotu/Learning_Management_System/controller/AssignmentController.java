
package com.chhotu.Learning_Management_System.controller;

import com.chhotu.Learning_Management_System.dto.AssignmentDto;
import com.chhotu.Learning_Management_System.dto.GetFeedbackDto;
import com.chhotu.Learning_Management_System.dto.GradeAssignmentDto;
import com.chhotu.Learning_Management_System.dto.SaveAssignmentDto;
import com.chhotu.Learning_Management_System.service.AssignmentService;
import com.chhotu.Learning_Management_System.service.NotificationsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/assignment")
public class AssignmentController {

    private final AssignmentService assignmentService;
    private final NotificationsService notificationsService;

    /**
     * Constructor-based dependency injection for Assignment and Notification services.
     */
    public AssignmentController(AssignmentService assignmentService, NotificationsService notificationsService) {
        this.assignmentService = assignmentService;
        this.notificationsService = notificationsService;
        log.info("AssignmentController initialized successfully.");
    }

    /**
     * Creates a new assignment for a course.
     *
     * @param assignment contains assignment details
     * @param request    the current HTTP request
     * @return response message
     */
    @PostMapping("/add")
    public ResponseEntity<String> addAssignment(@RequestBody AssignmentDto assignment, HttpServletRequest request) {
        log.info("Received request to create assignment: {}", assignment.getAssignmentTitle());
        try {
            assignmentService.addAssignment(assignment, request);
            log.info("Assignment '{}' created successfully.", assignment.getAssignmentTitle());
            return ResponseEntity.ok("Assignment created successfully.");
        } catch (IllegalArgumentException e) {
            log.warn("Assignment creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while creating assignment: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while creating assignment.");
        }
    }

    /**
     * Uploads an assignment by a student.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadAssignment(@RequestBody AssignmentDto assignment, HttpServletRequest request) {
        log.info("Received assignment upload request for assignmentId: {}", assignment.getAssignmentId());
        try {
            assignmentService.uploadAssignment(assignment, request);
            log.info("Assignment {} uploaded successfully.", assignment.getAssignmentId());
            return ResponseEntity.ok("Assignment uploaded successfully.");
        } catch (IllegalArgumentException e) {
            log.warn("Assignment upload failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while uploading assignment: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while uploading assignment.");
        }
    }

    /**
     * Grades a student's assignment and sends a notification.
     */
    @PutMapping("/grade")
    public ResponseEntity<String> gradeAssignment(@RequestBody GradeAssignmentDto gradeAssignmentDto, HttpServletRequest request) {
        log.info("Received request to grade assignmentId: {} for studentId: {}",
                gradeAssignmentDto.getAssignmentId(), gradeAssignmentDto.getStudentId());
        try {
            assignmentService.gradeAssignment(
                    gradeAssignmentDto.getStudentId(),
                    gradeAssignmentDto.getAssignmentId(),
                    gradeAssignmentDto.getGrade(),
                    request
            );

            String message = "Your assignment (ID: " + gradeAssignmentDto.getAssignmentId() + ") has been graded.";
            notificationsService.sendNotification(message, gradeAssignmentDto.getStudentId());

            log.info("Assignment {} graded successfully for student {}.",
                    gradeAssignmentDto.getAssignmentId(), gradeAssignmentDto.getStudentId());
            return ResponseEntity.ok("Assignment has been graded successfully.");
        } catch (IllegalArgumentException e) {
            log.warn("Grading failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while grading assignment: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while grading assignment.");
        }
    }

    /**
     * Saves instructor feedback for a student's assignment.
     */
    @PutMapping("/saveFeedback")
    public ResponseEntity<String> saveAssignmentFeedback(@RequestBody SaveAssignmentDto saveAssignmentDto, HttpServletRequest request) {
        log.info("Received feedback for assignmentId: {} from instructor.", saveAssignmentDto.getAssignmentId());
        try {
            assignmentService.saveAssignmentFeedback(
                    saveAssignmentDto.getStudentId(),
                    saveAssignmentDto.getAssignmentId(),
                    saveAssignmentDto.getFeedback(),
                    request
            );
            log.info("Feedback saved successfully for assignmentId: {}", saveAssignmentDto.getAssignmentId());
            return ResponseEntity.ok("Assignment feedback saved successfully.");
        } catch (IllegalArgumentException e) {
            log.warn("Feedback save failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while saving feedback: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while saving feedback.");
        }
    }

    /**
     * Retrieves feedback for a specific assignment.
     */
    @GetMapping("/feedback")
    public ResponseEntity<String> getFeedback(@RequestBody GetFeedbackDto getFeedbackDto, HttpServletRequest request) {
        log.info("Fetching feedback for assignmentId: {}", getFeedbackDto.getAssignmentId());
        try {
            String feedback = assignmentService.getFeedback(getFeedbackDto.getAssignmentId(), request);
            log.info("Feedback retrieved successfully for assignmentId: {}", getFeedbackDto.getAssignmentId());
            return ResponseEntity.ok(feedback);
        } catch (IllegalArgumentException e) {
            log.warn("Feedback retrieval failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error while fetching feedback: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("An unexpected error occurred while fetching feedback.");
        }
    }

    /**
     * Retrieves all submissions for a specific assignment.
     */
    @GetMapping("/submissions/{assignmentId}")
    public ResponseEntity<List<String>> trackAssignmentSubmissions(@PathVariable int assignmentId, HttpServletRequest request) {
        log.info("Fetching submissions for assignmentId: {}", assignmentId);
        try {
            List<String> submissions = assignmentService.assignmentSubmissions(assignmentId, request);
            log.info("Retrieved {} submissions for assignmentId: {}", submissions.size(), assignmentId);
            return ResponseEntity.ok(submissions);
        } catch (IllegalArgumentException e) {
            log.warn("Submission retrieval failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Collections.singletonList(e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error while fetching submissions: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Collections.singletonList("An unexpected error occurred while fetching submissions."));
        }
    }
}
