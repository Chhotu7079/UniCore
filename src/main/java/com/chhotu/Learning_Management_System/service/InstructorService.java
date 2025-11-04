package com.chhotu.Learning_Management_System.service;

import com.chhotu.Learning_Management_System.entity.Instructor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * Service interface for managing Instructor-related operations.
 * Defines methods for fetching instructor details and updating instructor profiles.
 */
public interface InstructorService {

    /**
     * Finds an instructor by their ID.
     *
     * @param userId the ID of the instructor.
     * @return an Optional containing the Instructor if found.
     */
    Optional<Instructor> findById(int userId);

    /**
     * Updates instructor details after validating the logged-in user's identity.
     *
     * @param instructorId the ID of the instructor being updated.
     * @param instructor the updated instructor data.
     * @param request the current HTTP request containing logged-in user info.
     */
    void save(int instructorId, Instructor instructor, HttpServletRequest request);
}
