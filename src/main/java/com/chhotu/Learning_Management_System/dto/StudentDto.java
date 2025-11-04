//package com.LMS.Learning_Management_System.dto;
//
//public class StudentDto {
//    private int userAccountId;
//    private String firstName;
//    private String lastName;
//
//    public StudentDto( int userAccountId, String firstName, String lastName) {
//        this.lastName = lastName;
//        this.firstName = firstName;
//        this.userAccountId = userAccountId;
//    }
//
//    public int getUserAccountId() {
//        return userAccountId;
//    }
//
//    public void setUserAccountId(int userAccountId) {
//        this.userAccountId = userAccountId;
//    }
//
//    public String getFirstName() {
//        return firstName;
//    }
//
//    public void setFirstName(String firstName) {
//        this.firstName = firstName;
//    }
//
//    public String getLastName() {
//        return lastName;
//    }
//
//    public void setLastName(String lastName) {
//        this.lastName = lastName;
//    }
//}




package com.chhotu.Learning_Management_System.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for transferring basic student information
 * without exposing sensitive or unnecessary data from the Student entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing basic student details")
public class StudentDto {

    @Schema(description = "Unique ID of the student (maps to user account ID)", example = "101")
    private int userAccountId;

    @Schema(description = "First name of the student", example = "Aarav")
    private String firstName;

    @Schema(description = "Last name of the student", example = "Sharma")
    private String lastName;
}
