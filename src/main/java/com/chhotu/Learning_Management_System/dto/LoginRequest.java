//package com.LMS.Learning_Management_System.dto;
//
//public class LoginRequest {
//    private String email;
//    private String password;
//
//    // Getters and setters
//    public String getEmail() {
//        return email;
//    }
//
//    public void setEmail(String email) {
//        this.email = email;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//}


package com.chhotu.Learning_Management_System.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object (DTO) for handling user login requests.
 * This class captures the credentials provided by a user during authentication.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for user login containing email and password")
public class LoginRequest {

    @Schema(description = "Registered email address of the user", example = "student@example.com")
    private String email;

    @Schema(description = "User's password for authentication", example = "securePassword123")
    private String password;
}
