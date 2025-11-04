package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.entity.Users;
import com.chhotu.Learning_Management_System.repository.UsersRepository;
import com.chhotu.Learning_Management_System.service.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

/**
 * Implementation of CustomUserDetailsService.
 *
 * This class bridges Spring Security with the application's user data stored
 * in the database. It fetches user information based on email and converts it
 * into a format compatible with Spring Security (UserDetails).
 */
@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsServiceImpl.class);

    private final UsersRepository usersRepository;

    @Autowired
    public CustomUserDetailsServiceImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
        logger.info("CustomUserDetailsServiceImpl initialized successfully.");
    }

    /**
     * Fetches a user's details from the database using their email and
     * converts them into a Spring Security-compatible UserDetails object.
     *
     * @param email The email (username) used for authentication.
     * @return UserDetails containing user's credentials and role.
     * @throws UsernameNotFoundException if no user with the given email exists.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);

        Users user = usersRepository.findByEmail(email);
        if (user == null) {
            logger.error("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        String userTypeName = user.getUserTypeId().getUserTypeName();
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + userTypeName.toUpperCase());

        logger.info("User loaded successfully: {} with role: {}", email, authority.getAuthority());

        // Wrap Users object into a Spring Security UserDetails object
        return new User(user.getEmail(), user.getPassword(), Collections.singletonList(authority));
    }
}
