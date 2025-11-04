//package com.LMS.Learning_Management_System.dto;
//
//import jakarta.servlet.http.HttpServletRequest;
//
//public class GradeAssignmentDto {
//    private int studentId;
//    private int assignmentId;
//    private float grade;
//
//    public int getAssignmentId() {
//        return assignmentId;
//    }
//
//    public void setAssignmentId(int assignmentId) {
//        this.assignmentId = assignmentId;
//    }
//
//    public int getStudentId() {
//        return studentId;
//    }
//
//    public void setStudentId(int studentId) {
//        this.studentId = studentId;
//    }
//
//    public float getGrade() {
//        return grade;
//    }
//
//    public void setGrade(float grade) {
//        this.grade = grade;
//    }
//
//
//}






package com.chhotu.Learning_Management_System.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data                   // Generates getters, setters, toString, equals, and hashCode
@NoArgsConstructor      // Generates a no-args constructor
@AllArgsConstructor     // Generates a constructor with all fields
public class GradeAssignmentDto {
    private int studentId;
    private int assignmentId;
    private float grade;
}


