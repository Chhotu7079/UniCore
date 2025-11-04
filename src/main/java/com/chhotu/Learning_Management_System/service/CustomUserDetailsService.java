package com.chhotu.Learning_Management_System.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Custom interface for loading user details during authentication.
 *
 * This interface defines the contract for retrieving user information
 * from the database based on the username (email).
 */
public interface CustomUserDetailsService {

    /**
     * Loads a user's details by their email (username).
     *
     * @param email The email (username) used to identify the user.
     * @return UserDetails object containing user information and authorities.
     * @throws UsernameNotFoundException if the user is not found in the database.
     */
    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;
}
