package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.dto.CourseDto;
import com.chhotu.Learning_Management_System.dto.StudentDto;
import com.chhotu.Learning_Management_System.entity.*;
import com.chhotu.Learning_Management_System.repository.*;
import com.chhotu.Learning_Management_System.entity.Course;
import com.chhotu.Learning_Management_System.entity.Instructor;
import com.chhotu.Learning_Management_System.entity.Users;
import com.chhotu.Learning_Management_System.repository.CourseRepository;
import com.chhotu.Learning_Management_System.repository.EnrollmentRepository;
import com.chhotu.Learning_Management_System.repository.InstructorRepository;
import com.chhotu.Learning_Management_System.service.CourseService;
import com.chhotu.Learning_Management_System.service.EnrollmentService;
import com.chhotu.Learning_Management_System.service.NotificationsService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation class for CourseService.
 * Handles all business logic related to course management.
 */
@Service
public class CourseServiceImpl implements CourseService {

    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentService enrollmentService;
    private final NotificationsService notificationsService;

    public CourseServiceImpl(InstructorRepository instructorRepository,
                             CourseRepository courseRepository,
                             EnrollmentRepository enrollmentRepository,
                             EnrollmentService enrollmentService,
                             NotificationsService notificationsService) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.enrollmentService = enrollmentService;
        this.notificationsService = notificationsService;
        logger.info("CourseServiceImpl initialized successfully");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCourse(Course course, HttpServletRequest request, int instructorId) {
        logger.info("Attempting to add a new course: {}", course.getCourseName());

        // Authenticate user
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (loggedInInstructor == null) {
            logger.error("Course creation failed: No user is logged in.");
            throw new IllegalArgumentException("No user is logged in.");
        }

        // Validate instructor role
        if (loggedInInstructor.getUserTypeId() == null ||
                loggedInInstructor.getUserTypeId().getUserTypeId() != 3) {
            logger.error("Unauthorized user type for course creation.");
            throw new IllegalArgumentException("Logged-in user is not an instructor.");
        }

        // Check instructor identity
        if (instructorId != loggedInInstructor.getUserId()) {
            logger.error("Instructor mismatch. Logged-in: {}, Provided: {}", loggedInInstructor.getUserId(), instructorId);
            throw new IllegalArgumentException("Instructor mismatch.");
        }

        // Prevent duplicate course names
        if (courseRepository.findByCourseName(course.getCourseName()) != null) {
            logger.warn("Duplicate course name detected: {}", course.getCourseName());
            throw new IllegalArgumentException("This course name already exists.");
        }

        // Validate instructor existence
        Instructor instructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> new IllegalArgumentException("Instructor not found."));

        // Assign instructor and creation date
        course.setInstructorId(instructor);
        course.setCreationDate(new Date());
        courseRepository.save(course);

        logger.info("Course '{}' added successfully by instructor '{}'", course.getCourseName(), instructor.getFirstName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CourseDto> getAllCourses(HttpServletRequest request) {
        logger.info("Fetching all available courses...");

        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        if (loggedInUser == null) {
            logger.error("Unauthorized attempt to fetch courses.");
            throw new IllegalArgumentException("No user is logged in.");
        }

        List<Course> courses = courseRepository.findAll();
        logger.info("Fetched {} courses.", courses.size());
        return convertToCourseDtoList(courses);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CourseDto getCourseById(int id, HttpServletRequest request) {
        logger.info("Fetching course details for ID: {}", id);

        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        if (loggedInUser == null) {
            logger.error("Unauthorized access: no logged-in user.");
            throw new IllegalArgumentException("No user is logged in.");
        }

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No course found with ID: " + id));

        // Check enrollment if student
        if (loggedInUser.getUserTypeId().getUserTypeId() == 2) {
            boolean enrolled = enrollmentRepository.findByCourse(course).stream()
                    .anyMatch(e -> e.getStudent().getUserAccountId() == loggedInUser.getUserId());
            if (!enrolled) {
                logger.error("Access denied: student {} not enrolled in course {}", loggedInUser.getUserId(), id);
                throw new IllegalArgumentException("You are not enrolled in this course.");
            }
        }

        logger.info("Course '{}' fetched successfully.", course.getCourseName());
        return new CourseDto(
                course.getCourseId(),
                course.getCourseName(),
                course.getDescription(),
                course.getDuration(),
                course.getMedia(),
                course.getInstructorId().getFirstName()
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateCourse(int courseId, Course updatedCourse, HttpServletRequest request) {
        logger.info("Updating course ID: {}", courseId);
        Course existingCourse = validateInstructorAndCourse(courseId, request);

        existingCourse.setCourseName(updatedCourse.getCourseName());
        existingCourse.setDescription(updatedCourse.getDescription());
        existingCourse.setDuration(updatedCourse.getDuration());

        courseRepository.save(existingCourse);
        logger.info("Course '{}' updated successfully.", updatedCourse.getCourseName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCourse(int courseId, HttpServletRequest request) {
        logger.info("Deleting course with ID: {}", courseId);
        Course existingCourse = validateInstructorAndCourse(courseId, request);
        courseRepository.delete(existingCourse);
        logger.info("Course with ID {} deleted successfully.", courseId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void uploadMediaFile(int courseId, MultipartFile file, HttpServletRequest request) {
        logger.info("Uploading media for course ID: {}", courseId);
        Course course = validateInstructorAndCourse(courseId, request);

        String uploadDir = "media/uploads/";
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
            logger.debug("Media upload directory created: {}", uploadDir);
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File destination = new File(uploadDir + fileName);

        try {
            file.transferTo(destination);
            logger.info("File '{}' uploaded successfully for course ID {}", fileName, courseId);
        } catch (IOException e) {
            logger.error("File upload failed for course {}: {}", courseId, e.getMessage());
            throw new RuntimeException("File upload failed.", e);
        }

        course.setMedia(fileName);
        courseRepository.save(course);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendNotificationsToEnrolledStudents(int courseId, HttpServletRequest request) {
        logger.info("Sending course update notifications for course ID: {}", courseId);
        List<StudentDto> students = enrollmentService.viewEnrolledStudents(courseId, request);
        String message = getCourseById(courseId, request).getCourseName() + " course is updated";

        for (StudentDto student : students) {
            notificationsService.sendNotification(message, student.getUserAccountId());
            logger.debug("Notification sent to student ID {}", student.getUserAccountId());
        }

        logger.info("Notifications sent successfully to all enrolled students of course {}", courseId);
    }

    /**
     * Validates that the logged-in instructor owns the given course.
     */
    private Course validateInstructorAndCourse(int courseId, HttpServletRequest request) {
        Users instructor = (Users) request.getSession().getAttribute("user");
        if (instructor == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }

        if (instructor.getUserTypeId() == null || instructor.getUserTypeId().getUserTypeId() != 3) {
            throw new IllegalArgumentException("Logged-in user is not an instructor.");
        }

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No course found with ID: " + courseId));

        if (course.getInstructorId() == null ||
                course.getInstructorId().getUserAccountId() != instructor.getUserId()) {
            throw new IllegalArgumentException("Unauthorized modification attempt.");
        }

        return course;
    }

    /**
     * Converts a list of Course entities to DTOs.
     */
    private List<CourseDto> convertToCourseDtoList(List<Course> courses) {
        return courses.stream()
                .map(course -> new CourseDto(
                        course.getCourseId(),
                        course.getCourseName(),
                        course.getDescription(),
                        course.getDuration(),
                        course.getMedia(),
                        course.getInstructorId().getFirstName()
                ))
                .collect(Collectors.toList());
    }
}
