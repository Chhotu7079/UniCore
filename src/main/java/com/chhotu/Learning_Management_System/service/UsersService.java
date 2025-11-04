package com.chhotu.Learning_Management_System.service;

import com.chhotu.Learning_Management_System.entity.Users;
import com.chhotu.Learning_Management_System.util.UserSignUpRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface UsersService {
    void save(UserSignUpRequest signUpRequest, HttpServletRequest request);
    Users findByEmail(String email);
    boolean validatePassword(String rawPassword, String encodedPassword);
}
