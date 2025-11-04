package com.chhotu.Learning_Management_System.service;

import com.chhotu.Learning_Management_System.entity.Student;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

public interface StudentService {

    /**
     * Find a student by their user ID.
     *
     * @param userId ID of the student.
     * @return Optional containing the student if found.
     */
    Optional<Student> findById(int userId);

    /**
     * Save or update student information.
     *
     * @param studentId ID of the student to update.
     * @param student Updated student object.
     * @param request HTTP request to get logged-in user.
     */
    void save(int studentId, Student student, HttpServletRequest request);
}
