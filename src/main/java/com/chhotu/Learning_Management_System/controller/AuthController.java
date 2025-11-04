
package com.chhotu.Learning_Management_System.controller;

import com.chhotu.Learning_Management_System.dto.LoginRequest;
import com.chhotu.Learning_Management_System.entity.Users;
import com.chhotu.Learning_Management_System.service.UsersService;
import com.chhotu.Learning_Management_System.util.UserSignUpRequest;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsersService usersService;

    /**
     * Constructor-based dependency injection for UsersService.
     */
    public AuthController(UsersService usersService) {
        this.usersService = usersService;
        log.info("AuthController initialized successfully.");
    }

    /**
     * Handles new user registration requests.
     *
     * @param signUpRequest contains user registration data
     * @param request       the current HTTP request
     * @return success or error message
     */
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody UserSignUpRequest signUpRequest, HttpServletRequest request) {
        log.info("Received signup request for email: {}", signUpRequest.getEmail());
        try {
            usersService.save(signUpRequest, request);
            log.info("User registered successfully with email: {}", signUpRequest.getEmail());
            return ResponseEntity.ok("User registered successfully");
        } catch (IllegalArgumentException e) {
            log.warn("Signup failed due to invalid data: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (EntityNotFoundException e) {
            log.error("Signup failed, entity not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during signup: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred during signup.");
        }
    }

    /**
     * Handles user login requests.
     *
     * @param request      the current HTTP request
     * @param loginRequest contains email and password
     * @return success or error message
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(HttpServletRequest request, @RequestBody LoginRequest loginRequest) {
        log.info("Login attempt for email: {}", loginRequest.getEmail());
        try {
            Users user = usersService.findByEmail(loginRequest.getEmail());
            if (user == null) {
                log.warn("Login failed - user not found for email: {}", loginRequest.getEmail());
                return ResponseEntity.badRequest().body("Invalid email");
            }

            // Validate password
            if (usersService.validatePassword(loginRequest.getPassword(), user.getPassword())) {
                // Store user in session
                request.getSession().setAttribute("user", user);

                // Set Spring Security authentication context
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), null, List.of());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("User {} logged in successfully.", user.getEmail());
                return ResponseEntity.ok("Login successful. Welcome, " + user.getEmail());
            } else {
                log.warn("Invalid credentials for email: {}", loginRequest.getEmail());
                return ResponseEntity.badRequest().body("Invalid email or password.");
            }
        } catch (UsernameNotFoundException e) {
            log.warn("Login failed - UsernameNotFoundException for email: {}", loginRequest.getEmail());
            return ResponseEntity.badRequest().body("Invalid email or password.");
        } catch (Exception e) {
            log.error("Unexpected error during login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred during login.");
        }
    }

    /**
     * Handles user logout requests.
     *
     * @return success message after logout
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        log.info("User logged out successfully.");
        return ResponseEntity.ok("Successfully logged out");
    }
}

