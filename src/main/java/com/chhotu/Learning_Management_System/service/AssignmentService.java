package com.chhotu.Learning_Management_System.service;

import com.chhotu.Learning_Management_System.dto.AssignmentDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface AssignmentService {

    void uploadAssignment(AssignmentDto assignment, HttpServletRequest request);

    void gradeAssignment(int studentID, int assigID, float grade, HttpServletRequest request);

    void saveAssignmentFeedback(int studentID, int assigID, String feedback, HttpServletRequest request);

    String getFeedback(int assigID, HttpServletRequest request);

    List<String> assignmentSubmissions(int assignmentId, HttpServletRequest request);

    void addAssignment(AssignmentDto assignment, HttpServletRequest request);
}
