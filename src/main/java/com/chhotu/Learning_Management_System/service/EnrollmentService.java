package com.chhotu.Learning_Management_System.service;

import com.chhotu.Learning_Management_System.dto.StudentDto;
import com.chhotu.Learning_Management_System.entity.Enrollment;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Interface for managing course enrollments.
 *
 * Provides methods for enrolling students into courses,
 * viewing enrolled students, and removing students from courses.
 */
public interface EnrollmentService {

    /**
     * Enroll a student in a specific course.
     *
     * @param enrollmentRequest The enrollment details (course and student).
     * @param request The current HTTP request containing logged-in user info.
     */
    void enrollInCourse(Enrollment enrollmentRequest, HttpServletRequest request);

    /**
     * View all students enrolled in a given course.
     *
     * @param courseId The course ID.
     * @param request The current HTTP request containing logged-in user info.
     * @return A list of enrolled students in DTO form.
     */
    List<StudentDto> viewEnrolledStudents(int courseId, HttpServletRequest request);

    /**
     * Remove a student from a specific course.
     *
     * @param courseId The course ID.
     * @param studentId The student ID to remove.
     * @param request The current HTTP request containing logged-in user info.
     */
    void removeEnrolledStudent(int courseId, int studentId, HttpServletRequest request);
}
