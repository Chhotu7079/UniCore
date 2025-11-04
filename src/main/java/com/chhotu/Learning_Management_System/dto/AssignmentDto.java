//package com.LMS.Learning_Management_System.dto;
//
//public class AssignmentDto {
//    private int assignmentId;
//    private String assignmentTitle;
//    private String assignmentDescription;
//    private int courseId;
//
//    public int getAssignmentId() {
//        return assignmentId;
//    }
//
//    public void setAssignmentId(int assignmentId) {
//        this.assignmentId = assignmentId;
//    }
//
//    public String getAssignmentTitle() {
//        return assignmentTitle;
//    }
//
//    public void setAssignmentTitle(String assignmentTitle) {
//        this.assignmentTitle = assignmentTitle;
//    }
//
//    public String getAssignmentDescription() {
//        return assignmentDescription;
//    }
//
//    public void setAssignmentDescription(String assignmentDescription) {
//        this.assignmentDescription = assignmentDescription;
//    }
//
//    public int getCourseId() {
//        return courseId;
//    }
//
//    public void setCourseId(int courseId) {
//        this.courseId = courseId;
//    }
//
//}
//
//









package com.chhotu.Learning_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Data Transfer Object for handling assignment-related requests.
 * Used for creating, updating, and retrieving assignment information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDto implements Serializable {

    //private static final long serialVersionUID = 1L;

    /**
     * Unique identifier of the assignment.
     */
    private int assignmentId;

    /**
     * Title or name of the assignment.
     */
    private String assignmentTitle;

    /**
     * Detailed description or instructions for the assignment.
     */
    private String assignmentDescription;

    /**
     * Identifier of the course to which this assignment belongs.
     */
    private int courseId;

}
