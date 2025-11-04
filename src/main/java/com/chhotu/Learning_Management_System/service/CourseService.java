package com.chhotu.Learning_Management_System.service;

import com.chhotu.Learning_Management_System.dto.CourseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;
import com.chhotu.Learning_Management_System.entity.Course;

import java.util.List;

/**
 * Interface defining operations for course management.
 */
public interface CourseService {

    /**
     * Adds a new course for the logged-in instructor.
     */
    void addCourse(Course course, HttpServletRequest request, int instructorId);

    /**
     * Retrieves all available courses.
     */
    List<CourseDto> getAllCourses(HttpServletRequest request);

    /**
     * Retrieves course details by ID.
     */
    CourseDto getCourseById(int id, HttpServletRequest request);

    /**
     * Updates an existing course.
     */
    void updateCourse(int courseId, Course updatedCourse, HttpServletRequest request);

    /**
     * Deletes a course by ID.
     */
    void deleteCourse(int courseId, HttpServletRequest request);

    /**
     * Uploads and associates a media file with a course.
     */
    void uploadMediaFile(int courseId, MultipartFile file, HttpServletRequest request);

    /**
     * Sends update notifications to all students enrolled in a course.
     */
    void sendNotificationsToEnrolledStudents(int courseId, HttpServletRequest request);
}
