package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.entity.Instructor;
import com.chhotu.Learning_Management_System.entity.Users;
import com.chhotu.Learning_Management_System.repository.InstructorRepository;
import com.chhotu.Learning_Management_System.service.InstructorService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.Optional;

/**
 * Implementation of the InstructorService interface.
 *
 * Provides methods for retrieving and updating instructor information.
 * Includes authentication and authorization checks to ensure only
 * the logged-in instructor can modify their own profile.
 */
@Service
public class InstructorServiceImpl implements InstructorService {

    private static final Logger logger = LoggerFactory.getLogger(InstructorServiceImpl.class);

    private final InstructorRepository instructorRepository;

    public InstructorServiceImpl(InstructorRepository instructorRepository) {
        this.instructorRepository = instructorRepository;
        logger.info("InstructorServiceImpl initialized successfully.");
    }

    /**
     * Fetches an Instructor by their user ID.
     *
     * @param userId ID of the instructor.
     * @return Optional containing the Instructor if found.
     */
    @Override
    public Optional<Instructor> findById(int userId) {
        logger.debug("Fetching instructor with ID: {}", userId);
        Optional<Instructor> instructor = instructorRepository.findById(userId);

        if (instructor.isPresent()) {
            logger.info("Instructor found: {} {}",
                    instructor.get().getFirstName(), instructor.get().getLastName());
        } else {
            logger.warn("No instructor found with ID: {}", userId);
        }
        return instructor;
    }

    /**
     * Updates the instructor profile after validating authorization.
     *
     * @param instructorId ID of the instructor being updated.
     * @param instructor Instructor entity containing updated data.
     * @param request Current HTTP request containing the logged-in user session.
     */
    @Override
    public void save(int instructorId, Instructor instructor, HttpServletRequest request) {
        logger.debug("Attempting to update instructor with ID: {}", instructorId);

        // Get the currently logged-in user from the session
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (loggedInInstructor == null) {
            logger.error("Unauthorized attempt — no user logged in.");
            throw new IllegalArgumentException("User is not logged in.");
        }

        // Check if the logged-in user matches the instructor being updated
        if (instructorId != loggedInInstructor.getUserId()) {
            logger.warn("Authorization failed — logged-in user {} tried to update instructor {}",
                    loggedInInstructor.getUserId(), instructorId);
            throw new IllegalArgumentException("You are not authorized to perform this action.");
        }

        // Retrieve existing instructor record from DB
        Instructor existingInstructor = instructorRepository.findById(instructorId)
                .orElseThrow(() -> {
                    logger.error("Instructor with ID {} not found in database.", instructorId);
                    return new IllegalArgumentException("Instructor not found with ID: " + instructorId);
                });

        // Update relevant fields
        existingInstructor.setFirstName(instructor.getFirstName());
        existingInstructor.setLastName(instructor.getLastName());

        // Save changes
        instructorRepository.save(existingInstructor);
        logger.info("Instructor profile updated successfully for ID: {}", instructorId);
    }
}
