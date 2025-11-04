//package com.LMS.Learning_Management_System.dto;
//
//public class GetFeedbackDto {
//    private int assignmentId;
//
//    public int getAssignmentId() {
//        return assignmentId;
//    }
//
//    public void setAssignmentId(int assignmentId) {
//        this.assignmentId = assignmentId;
//    }
//
//}



package com.chhotu.Learning_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor      // Generates a no-args constructor
@AllArgsConstructor     // Generates a constructor with all fields
public class GetFeedbackDto {
    private int assignmentId;
}
