package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.entity.*;
import com.chhotu.Learning_Management_System.repository.*;
import com.chhotu.Learning_Management_System.entity.*;
import com.chhotu.Learning_Management_System.repository.*;
import com.chhotu.Learning_Management_System.service.UsersService;
import com.chhotu.Learning_Management_System.util.UserSignUpRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Implementation of the {@link UsersService} interface.
 * Handles user registration, authentication, and role-based account creation logic.
 */
@Service
public class UsersServiceImpl implements UsersService {

    private static final Logger logger = LoggerFactory.getLogger(UsersServiceImpl.class);

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersTypeRepository usersTypeRepository;
    private final StudentRepository studentRepository;
    private final AdminRepository adminRepository;
    private final InstructorRepository instructorRepository;

    public UsersServiceImpl(UsersRepository usersRepository,
                            PasswordEncoder passwordEncoder,
                            UsersTypeRepository usersTypeRepository,
                            StudentRepository studentRepository,
                            AdminRepository adminRepository,
                            InstructorRepository instructorRepository) {
        this.passwordEncoder = passwordEncoder;
        this.usersRepository = usersRepository;
        this.usersTypeRepository = usersTypeRepository;
        this.studentRepository = studentRepository;
        this.adminRepository = adminRepository;
        this.instructorRepository = instructorRepository;
        logger.info("UsersServiceImpl initialized successfully");
    }

    /**
     * Creates a new user account in the system.
     * Only users with the ADMIN role (userTypeId = 1) are allowed to perform this operation.
     *
     * @param signUpRequest contains new user details such as email, password, and user type
     * @param request the HTTP request containing the logged-in admin session
     */
    @Override
    public void save(UserSignUpRequest signUpRequest, HttpServletRequest request) {
        logger.info("Attempting to create a new user with email: {}", signUpRequest.getEmail());

        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        if (loggedInUser == null) {
            logger.error("Unauthorized access: No admin is logged in.");
            throw new IllegalArgumentException("Admin must logged in to create a new user");
        }

        if (loggedInUser.getUserTypeId().getUserTypeId() != 1) {
            logger.error("Unauthorized access: Non-admin user {} tried to create an account", loggedInUser.getEmail());
            throw new IllegalArgumentException("Admin only can create account");
        }

        if (usersRepository.findByEmail(signUpRequest.getEmail()) != null) {
            logger.warn("Duplicate email detected: {}", signUpRequest.getEmail());
            throw new IllegalArgumentException("Email already in use");
        }

        UsersType userType = usersTypeRepository.findById(signUpRequest.getUserTypeId())
                .orElseThrow(() -> new EntityNotFoundException("User Type not found"));
        logger.info("User type found: {}", userType.getUserTypeName());

        Users newUser = new Users(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                userType
        );
        newUser.setRegistrationDate(new Date());
        usersRepository.save(newUser);
        logger.info("New user saved successfully: {}", newUser.getEmail());

        // Assign role-specific entity
        if (newUser.getUserTypeId().getUserTypeId() == 1) {
            adminRepository.save(new Admin(newUser));
            logger.info("Admin entity created for user: {}", newUser.getEmail());
        }
        else if (newUser.getUserTypeId().getUserTypeId() == 2) {
            studentRepository.save(new Student(newUser));
            logger.info("Student entity created for user: {}", newUser.getEmail());
        } else {
            instructorRepository.save(new Instructor(newUser));
            logger.info("Instructor entity created for user: {}", newUser.getEmail());
        }
    }

    /**
     * Finds a user by email address.
     *
     * @param email user's email address
     * @return Users entity if found, otherwise null
     */
    @Override
    public Users findByEmail(String email) {
        logger.debug("Fetching user with email: {}", email);
        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            throw new EntityNotFoundException("User not found with email: " + email);
        }
        return user;
    }

    /**
     * Validates the provided raw password against the encoded one.
     *
     * @param rawPassword plain text password
     * @param encodedPassword hashed password stored in DB
     * @return true if passwords match, false otherwise
     */
    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword) {
        logger.debug("Validating user password...");
        boolean isValid = passwordEncoder.matches(rawPassword, encodedPassword);
        logger.info("Password validation result: {}", isValid ? "SUCCESS" : "FAILURE");
        return isValid;
    }
}
