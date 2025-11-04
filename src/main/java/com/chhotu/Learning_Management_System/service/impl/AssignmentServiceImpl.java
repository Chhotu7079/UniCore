package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.dto.AssignmentDto;
import com.chhotu.Learning_Management_System.entity.*;
import com.chhotu.Learning_Management_System.repository.*;
import com.chhotu.Learning_Management_System.entity.*;
import com.chhotu.Learning_Management_System.repository.*;
import com.chhotu.Learning_Management_System.service.AssignmentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation class for AssignmentService interface.
 * Handles core business logic for managing assignments, submissions, grading, and feedback.
 * Ensures that only authorized instructors and enrolled students can perform respective actions.
 */
@Service
public class AssignmentServiceImpl implements AssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(AssignmentServiceImpl.class);

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AssignmentServiceImpl(AssignmentRepository assignmentRepository,
                                 SubmissionRepository submissionRepository,
                                 CourseRepository courseRepository,
                                 StudentRepository studentRepository,
                                 EnrollmentRepository enrollmentRepository) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    /**
     * Upload a student's assignment submission after validating enrollment and previous submissions.
     */
    @Override
    public void uploadAssignment(AssignmentDto assignment, HttpServletRequest request) {
        logger.info("Attempting to upload assignment: {}", assignment.getAssignmentTitle());

        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        if (loggedInUser == null) {
            logger.error("Unauthorized attempt to upload assignment - user not logged in.");
            throw new IllegalArgumentException("You are not logged in");
        }

        Course course = courseRepository.findById(assignment.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        Student student = studentRepository.findById(loggedInUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("You're not a student"));

        // Check enrollment
        boolean enrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (!enrolled) {
            logger.warn("User {} is not enrolled in course ID {}", loggedInUser.getUserId(), course.getCourseId());
            throw new IllegalArgumentException("You're not enrolled in this course");
        }

        // Check duplicate submission
        List<Submission> submissions = submissionRepository.findByStudentId(student);
        for (Submission s : submissions) {
            if (s.getAssignmentId().getAssignmentId() == assignment.getAssignmentId()) {
                logger.warn("Duplicate submission attempt by student ID {} for assignment {}", student.getUserAccountId(), assignment.getAssignmentId());
                throw new IllegalArgumentException("You've already submitted this assignment");
            }
        }

        // Save submission
        Assignment newAssignment = new Assignment();
        newAssignment.setAssignmentId(assignment.getAssignmentId());
        newAssignment.setDescription(assignment.getAssignmentDescription());
        newAssignment.setCourseID(course);
        newAssignment.setDueDate(new Date());
        newAssignment.setTitle(assignment.getAssignmentTitle());

        Submission submission = new Submission();
        submission.setAssignmentId(newAssignment);
        submission.setStudentId(student);
        submissionRepository.save(submission);

        logger.info("Assignment '{}' uploaded successfully by student ID {}", assignment.getAssignmentTitle(), student.getUserId().getUserId());
    }

    /**
     * Grade a student's assignment submission (only accessible by the course instructor).
     */
    @Override
    public void gradeAssignment(int studentID, int assigID, float grade, HttpServletRequest request) {
        logger.info("Grading assignment ID {} for student ID {}", assigID, studentID);

        Users instructor = (Users) request.getSession().getAttribute("user");
        if (instructor == null) {
            logger.error("Unauthorized grading attempt - no user logged in.");
            throw new IllegalArgumentException("You are not logged in");
        }

        Assignment assignment = assignmentRepository.findById(assigID)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (instructor.getUserId() != assignment.getCourseID().getInstructorId().getUserAccountId()) {
            logger.warn("Instructor ID {} unauthorized to grade assignment ID {}", instructor.getUserId(), assigID);
            throw new IllegalArgumentException("You're not the instructor of this course");
        }

        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<Submission> submissions = submissionRepository.findByStudentId(student);
        if (submissions.isEmpty()) {
            logger.warn("Student ID {} has no submissions for assignment {}", studentID, assigID);
            throw new IllegalArgumentException("Student has no submissions");
        }

        for (Submission s : submissions) {
            if (s.getAssignmentId().getAssignmentId() == assigID) {
                s.setGrade(grade);
                submissionRepository.save(s);
                logger.info("Grade {} assigned to student ID {} for assignment ID {}", grade, studentID, assigID);
                return;
            }
        }

        logger.error("Assignment ID {} not submitted by student ID {}", assigID, studentID);
        throw new IllegalArgumentException("Student didn't submit this assignment");
    }

    /**
     * Add feedback to a student's assignment submission.
     */
    @Override
    public void saveAssignmentFeedback(int studentID, int assigID, String feedback, HttpServletRequest request) {
        logger.info("Saving feedback for assignment ID {} for student ID {}", assigID, studentID);

        Users instructor = (Users) request.getSession().getAttribute("user");
        if (instructor == null) {
            logger.error("Unauthorized feedback attempt - no user logged in.");
            throw new IllegalArgumentException("You are not logged in");
        }

        Assignment assignment = assignmentRepository.findById(assigID)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        if (instructor.getUserId() != assignment.getCourseID().getInstructorId().getUserAccountId()) {
            logger.warn("Instructor ID {} not authorized to add feedback for assignment ID {}", instructor.getUserId(), assigID);
            throw new IllegalArgumentException("You're not the instructor of this course");
        }

        Student student = studentRepository.findById(studentID)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));

        List<Submission> submissions = submissionRepository.findByStudentId(student);
        if (submissions.isEmpty()) {
            logger.warn("Student ID {} has no submissions", studentID);
            throw new IllegalArgumentException("Student has no submissions");
        }

        for (Submission s : submissions) {
            if (s.getAssignmentId().getAssignmentId() == assigID) {
                s.setFeedback(feedback);
                submissionRepository.save(s);
                logger.info("Feedback added for student ID {} on assignment ID {}", studentID, assigID);
                return;
            }
        }

        logger.error("No submission found for student ID {} and assignment ID {}", studentID, assigID);
        throw new IllegalArgumentException("Student didn't submit this assignment");
    }

    /**
     * Retrieve feedback for the logged-in student's assignment.
     */
    @Override
    public String getFeedback(int assigID, HttpServletRequest request) {
        logger.info("Fetching feedback for assignment ID {}", assigID);

        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        if (loggedInUser == null) {
            throw new IllegalArgumentException("You are not logged in");
        }

        Assignment assignment = assignmentRepository.findById(assigID)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found"));

        Student student = studentRepository.findById(loggedInUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("You're not a student"));

        boolean enrolled = enrollmentRepository.existsByStudentAndCourse(student, assignment.getCourseID());
        if (!enrolled) {
            throw new IllegalArgumentException("You're not enrolled in this course");
        }

        List<Submission> submissions = submissionRepository.findByStudentId(student);
        if (submissions.isEmpty()) {
            throw new IllegalArgumentException("Student has no submissions");
        }

        for (Submission s : submissions) {
            if (s.getAssignmentId().getAssignmentId() == assigID) {
                logger.debug("Feedback retrieved successfully for student ID {}", student.getUserId().getUserId());
                return s.getFeedback() != null ? s.getFeedback() : "There is no feedback yet";
            }
        }

        logger.error("Feedback not found for student ID {} and assignment ID {}", student.getUserId().getUserId(), assigID);
        throw new IllegalArgumentException("Student didn't submit this assignment");
    }

    /**
     * Get all submissions for a given assignment (accessible only by the instructor of the course).
     */
    @Override
    public List<String> assignmentSubmissions(int assignmentId, HttpServletRequest request) {
        logger.info("Fetching submissions for assignment ID {}", assignmentId);

        if (!assignmentRepository.existsById(assignmentId)) {
            throw new IllegalArgumentException("Assignment with ID " + assignmentId + " not found.");
        }

        Assignment assignment = assignmentRepository.findById(assignmentId).get();
        Users instructor = (Users) request.getSession().getAttribute("user");

        if (instructor == null) {
            throw new IllegalArgumentException("No logged in user is found.");
        }
        if (instructor.getUserTypeId() == null || instructor.getUserTypeId().getUserTypeId() != 3) {
            throw new IllegalArgumentException("Logged-in user is not an instructor.");
        }
        if (assignment.getCourseID().getInstructorId().getUserAccountId() != instructor.getUserId()) {
            throw new IllegalArgumentException("You do not have access to these submissions.");
        }

        List<Submission> submissions = submissionRepository.findAllByAssignmentId(assignment);
        List<String> result = new ArrayList<>();

        for (Submission s : submissions) {
            result.add(s.getStudentId().getUserAccountId() + ": " + s.getGrade());
        }

        logger.info("Fetched {} submissions for assignment ID {}", result.size(), assignmentId);
        return result;
    }

    /**
     * Add a new assignment (instructor-only operation).
     */
    @Override
    public void addAssignment(AssignmentDto assignment, HttpServletRequest request) {
        logger.info("Attempting to add assignment '{}'", assignment.getAssignmentTitle());

        Users instructor = (Users) request.getSession().getAttribute("user");
        if (instructor == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }

        if (instructor.getUserTypeId() == null || instructor.getUserTypeId().getUserTypeId() != 3) {
            throw new IllegalArgumentException("Logged-in user is not an instructor.");
        }

        Course course = courseRepository.findById(assignment.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("No such CourseId"));

        if (instructor.getUserId() != course.getInstructorId().getUserAccountId()) {
            throw new IllegalArgumentException("You are not the Instructor of this course");
        }

        if (assignmentRepository.existsById(assignment.getAssignmentId())) {
            throw new IllegalArgumentException("Assignment already exists");
        }

        Assignment newAssignment = new Assignment();
        newAssignment.setDescription(assignment.getAssignmentDescription());
        newAssignment.setTitle(assignment.getAssignmentTitle());
        newAssignment.setDueDate(new Date());
        newAssignment.setCourseID(course);

        assignmentRepository.save(newAssignment);
        logger.info("Assignment '{}' successfully added to course '{}'", assignment.getAssignmentTitle(), course.getCourseName());
    }
}
