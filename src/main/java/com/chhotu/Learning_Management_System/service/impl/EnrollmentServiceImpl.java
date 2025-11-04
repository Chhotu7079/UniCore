package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.dto.StudentDto;
import com.chhotu.Learning_Management_System.entity.*;
import com.chhotu.Learning_Management_System.repository.*;
import com.chhotu.Learning_Management_System.entity.Course;
import com.chhotu.Learning_Management_System.entity.Enrollment;
import com.chhotu.Learning_Management_System.entity.Student;
import com.chhotu.Learning_Management_System.entity.Users;
import com.chhotu.Learning_Management_System.repository.CourseRepository;
import com.chhotu.Learning_Management_System.repository.EnrollmentRepository;
import com.chhotu.Learning_Management_System.repository.StudentRepository;
import com.chhotu.Learning_Management_System.service.EnrollmentService;
import com.chhotu.Learning_Management_System.service.NotificationsService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of EnrollmentService.
 *
 * Handles enrollment-related business logic such as registering students in courses,
 * viewing enrolled students, and removing enrollments. It also validates permissions
 * based on user roles (Admin/Instructor/Student).
 */
@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private static final Logger logger = LoggerFactory.getLogger(EnrollmentServiceImpl.class);

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final NotificationsService notificationsService;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository,
                                 NotificationsService notificationsService) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.notificationsService = notificationsService;
        logger.info("EnrollmentServiceImpl initialized successfully.");
    }

    /**
     * Enroll a student into a course after validating their identity and eligibility.
     */
    @Override
    public void enrollInCourse(Enrollment enrollmentRequest, HttpServletRequest request) {
        logger.debug("Attempting to enroll student in course: {}", enrollmentRequest.getCourse().getCourseId());

        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        if (loggedInUser == null) {
            logger.error("No user is logged in.");
            throw new IllegalArgumentException("No user is logged in.");
        }

        if (loggedInUser.getUserId() != enrollmentRequest.getStudent().getUserAccountId()) {
            logger.warn("Student ID mismatch. Logged-in user: {}, Request: {}",
                    loggedInUser.getUserId(), enrollmentRequest.getStudent().getUserAccountId());
            throw new IllegalArgumentException("Student ID mismatch. Please provide the correct ID.");
        }

        Student student = studentRepository.findById(enrollmentRequest.getStudent().getUserAccountId())
                .orElseThrow(() -> {
                    logger.error("No student found with ID: {}", enrollmentRequest.getStudent().getUserAccountId());
                    return new IllegalArgumentException("No student found with the given ID.");
                });

        int courseId = enrollmentRequest.getCourse().getCourseId();
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> {
                    logger.error("No course found with ID: {}", courseId);
                    return new IllegalArgumentException("No course found with the given ID: " + courseId);
                });

        boolean isEnrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (isEnrolled) {
            logger.warn("Student {} is already enrolled in course {}", student.getUserAccountId(), courseId);
            throw new IllegalArgumentException("Student is already enrolled in this course.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudent(student);
        enrollment.setCourse(course);
        enrollment.setEnrollmentDate(new java.util.Date());
        enrollmentRepository.save(enrollment);

        logger.info("Student {} successfully enrolled in course {}", student.getUserAccountId(), courseId);

        notificationsService.sendNotification(
                "Student with ID " + student.getUserAccountId() + " enrolled in course " + courseId,
                course.getInstructorId().getUserAccountId()
        );
    }

    /**
     * Retrieve a list of students enrolled in a particular course.
     */
    @Override
    public List<StudentDto> viewEnrolledStudents(int courseId, HttpServletRequest request) {
        logger.debug("Fetching enrolled students for course ID: {}", courseId);
        Course course = validateCourseAccessForInstructorOrAdmin(courseId, request);

        List<Enrollment> enrollments = enrollmentRepository.findByCourse(course);
        List<Student> students = enrollments.stream()
                .map(Enrollment::getStudent)
                .collect(Collectors.toList());

        logger.info("Found {} enrolled students for course ID: {}", students.size(), courseId);
        return convertToDtoList(students);
    }

    /**
     * Remove a student's enrollment from a specific course.
     */
    @Override
    public void removeEnrolledStudent(int courseId, int studentId, HttpServletRequest request) {
        logger.debug("Attempting to remove student {} from course {}", studentId, courseId);

        Course course = validateInstructorOwnership(courseId, request);
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("No student found with the given ID."));

        boolean isEnrolled = enrollmentRepository.existsByStudentAndCourse(student, course);
        if (!isEnrolled) {
            logger.warn("Student {} is not enrolled in course {}", studentId, courseId);
            throw new IllegalArgumentException("This student is not enrolled in this course.");
        }

        Enrollment enrollment = enrollmentRepository.findByStudentAndCourse(student, course);
        enrollmentRepository.deleteById(enrollment.getEnrollmentId());

        logger.info("Successfully removed student {} from course {}", studentId, courseId);
    }

    /**
     * Converts a list of Student entities to StudentDto objects.
     */
    private List<StudentDto> convertToDtoList(List<Student> students) {
        return students.stream()
                .map(student -> new StudentDto(
                        student.getUserAccountId(),
                        student.getFirstName(),
                        student.getLastName()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Validates if the logged-in user is allowed to view enrolled students.
     * Admins and course instructors can access.
     */
    private Course validateCourseAccessForInstructorOrAdmin(int courseId, HttpServletRequest request) {
        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        if (loggedInUser == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No course found with the given ID: " + courseId));

        if (loggedInUser.getUserTypeId() == null) {
            throw new IllegalArgumentException("User type is missing.");
        }

        int userType = loggedInUser.getUserTypeId().getUserTypeId();
        if (userType != 1 && userType != 3) { // 1 = Admin, 3 = Instructor
            throw new IllegalArgumentException("Access denied. Only Admins or Instructors can view this.");
        }

        if (userType == 3 && course.getInstructorId().getUserAccountId() != loggedInUser.getUserId()) {
            throw new IllegalArgumentException("You are not the instructor of this course.");
        }

        return course;
    }

    /**
     * Validates if the logged-in instructor is the actual owner of the course.
     */
    private Course validateInstructorOwnership(int courseId, HttpServletRequest request) {
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (loggedInInstructor == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }

        if (loggedInInstructor.getUserTypeId() == null ||
                loggedInInstructor.getUserTypeId().getUserTypeId() != 3) {
            throw new IllegalArgumentException("Logged-in user is not an instructor.");
        }

        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No course found with the given ID: " + courseId));

        if (existingCourse.getInstructorId() == null ||
                existingCourse.getInstructorId().getUserAccountId() != loggedInInstructor.getUserId()) {
            throw new IllegalArgumentException("You are not the instructor of this course.");
        }

        return existingCourse;
    }
}
