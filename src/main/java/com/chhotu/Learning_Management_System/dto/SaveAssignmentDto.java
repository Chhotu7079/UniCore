//package com.LMS.Learning_Management_System.dto;
//
//public class SaveAssignmentDto {
//
//    private int studentId;
//    private int assignmentId;
//    private String feedback;
//
//    public int getStudentId() {
//        return studentId;
//    }
//
//    public void setStudentId(int studentId) {
//        this.studentId = studentId;
//    }
//
//    public int getAssignmentId() {
//        return assignmentId;
//    }
//
//    public void setAssignmentId(int assignmentId) {
//        this.assignmentId = assignmentId;
//    }
//
//    public String getFeedback() {
//        return feedback;
//    }
//
//    public void setFeedback(String feedback) {
//        this.feedback = feedback;
//    }
//
//
//}



package com.chhotu.Learning_Management_System.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO used to capture feedback or updates for a student's assignment.
 * Primarily used by instructors to save feedback and grades for submissions.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for saving assignment feedback")
public class SaveAssignmentDto {

    @Schema(description = "Unique ID of the student", example = "102")
    private int studentId;

    @Schema(description = "Unique ID of the assignment", example = "205")
    private int assignmentId;

    @Schema(description = "Instructor feedback for the assignment", example = "Good work! Improve your explanation in section 2.")
    private String feedback;
}
