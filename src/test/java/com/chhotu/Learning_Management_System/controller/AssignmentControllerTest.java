//package com.LMS.Learning_Management_System.controller;
//
//import dto.com.chhotu.Learning_Management_System.AssignmentDto;
//import dto.com.chhotu.Learning_Management_System.GetFeedbackDto;
//import dto.com.chhotu.Learning_Management_System.GradeAssignmentDto;
//import dto.com.chhotu.Learning_Management_System.SaveAssignmentDto;
//import entity.com.chhotu.Learning_Management_System.Assignment;
//import entity.com.chhotu.Learning_Management_System.Course;
//import entity.com.chhotu.Learning_Management_System.Instructor;
//import service.com.chhotu.Learning_Management_System.AssignmentService;
//import service.com.chhotu.Learning_Management_System.CourseService;
//import service.com.chhotu.Learning_Management_System.NotificationsService;
//import jakarta.servlet.http.HttpServletRequest;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockHttpServletRequest;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//import static org.mockito.Mockito.times;
//
//public class AssignmentControllerTest {
//
//
//    @Mock
//    private AssignmentService assignmentService;
//
//
//    @Mock
//    private NotificationsService notificationsService;
//
//
//    @InjectMocks
//    private AssignmentController assigmentController;
//
//    private MockHttpServletRequest request;
//
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        request = new MockHttpServletRequest();
//
//
//
//    }
//
//    @Test
//    void uploadAssignmentTest() {
//        AssignmentDto assignmentDto = new AssignmentDto();
//        assignmentDto.setAssignmentId(1);
//        assignmentDto.setCourseId(1);
//
//        HttpServletRequest request = mock(HttpServletRequest.class);
//
//        doNothing().when(assignmentService).uploadAssignment(any(AssignmentDto.class), any(HttpServletRequest.class));
//
//        ResponseEntity<String> response = assigmentController.uploadAssignment(assignmentDto, request);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("Assignment uploaded successfully.", response.getBody());
//
//        verify(assignmentService, times(1)).uploadAssignment(eq(assignmentDto), eq(request));
//    }
//
//
//    @Test
//    void gradeAssignmentTest() {
//        GradeAssignmentDto gradeAssignmentDto = new GradeAssignmentDto();
//        gradeAssignmentDto.setAssignmentId(1);
//        gradeAssignmentDto.setStudentId(1);
//        gradeAssignmentDto.setGrade(100);
//
//        HttpServletRequest request = mock(HttpServletRequest.class);
//
//        doNothing().when(assignmentService).gradeAssignment(anyInt(), anyInt(), anyFloat(), any(HttpServletRequest.class));
//        doNothing().when(notificationsService).sendNotification(anyString(), anyInt());
//
//        ResponseEntity<String> response = assigmentController.gradeAssignment(gradeAssignmentDto, request);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("Assignment has been graded successfully.", response.getBody());
//
//        verify(assignmentService, times(1)).gradeAssignment(eq(1),eq(1), eq(100.0f), eq(request));
//        verify(notificationsService, times(1)).sendNotification(eq("Assignment 1 grade is uploaded"),eq(1));
//
//    }
//
//    @Test
//    void saveAssignmentFeedbackTest() {
//        SaveAssignmentDto saveAssignmentDto = new SaveAssignmentDto();
//        saveAssignmentDto.setAssignmentId(1);
//        saveAssignmentDto.setStudentId(1);
//        saveAssignmentDto.setFeedback("Good job");
//
//        HttpServletRequest request = mock(HttpServletRequest.class);
//
//        doNothing().when(assignmentService).saveAssignmentFeedback(anyInt(), anyInt(), anyString(), any(HttpServletRequest.class));
//
//        ResponseEntity<String> response = assigmentController.saveAssignmentFeedback(saveAssignmentDto, request);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("Assignment feedback is saved successfully.", response.getBody());
//
//        verify(assignmentService, times(1)).saveAssignmentFeedback(eq(1), eq(1), eq("done"), eq(request));
//    }
//
//
//    @Test
//    void getFeedbackTest() {
//        GetFeedbackDto getFeedbackDto = new GetFeedbackDto();
//        getFeedbackDto.setAssignmentId(1);
//
//        HttpServletRequest request = mock(HttpServletRequest.class);
//
//        when(assignmentService.getFeedback(anyInt(), any(HttpServletRequest.class)))
//                .thenReturn("This is a feedback message");
//
//        ResponseEntity<String> response = assigmentController.getFeedback(getFeedbackDto, request);
//
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals("This is a feedback message", response.getBody());
//
//        verify(assignmentService, times(1)).getFeedback(eq(1), eq(request));
//    }
//
//    @Test
//    void testTrackAssignmentSubmissions()
//    {
//        List<String> ans = new ArrayList<String>();
//
//        when(assignmentService.assignmentSubmissions(eq(1), eq(request))).thenReturn(ans);
//
//        ResponseEntity <?> response = assigmentController.trackAssignmentSubmissions(1, request);
//        assertEquals(200, response.getStatusCodeValue());
//        assertEquals(ans, response.getBody());
//        verify(assignmentService, times(1)).assignmentSubmissions(eq(1), eq(request));
//    }
//}






package com.chhotu.Learning_Management_System.controller;

import com.chhotu.Learning_Management_System.dto.AssignmentDto;
import com.chhotu.Learning_Management_System.dto.GetFeedbackDto;
import com.chhotu.Learning_Management_System.dto.GradeAssignmentDto;
import com.chhotu.Learning_Management_System.dto.SaveAssignmentDto;
import com.chhotu.Learning_Management_System.service.AssignmentService;
import com.chhotu.Learning_Management_System.service.NotificationsService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AssignmentController} using Mockito.
 */
public class AssignmentControllerTest {

    @Mock
    private AssignmentService assignmentService;

    @Mock
    private NotificationsService notificationsService;

    @InjectMocks
    private AssignmentController assignmentController;

    private MockHttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = new MockHttpServletRequest();
    }

    /**
     * Test uploading an assignment.
     */
    @Test
    void uploadAssignmentTest() {
        AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setAssignmentId(1);
        assignmentDto.setCourseId(1);

        doNothing().when(assignmentService).uploadAssignment(any(AssignmentDto.class), any(HttpServletRequest.class));

        ResponseEntity<String> response = assignmentController.uploadAssignment(assignmentDto, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Assignment uploaded successfully.", response.getBody());
        verify(assignmentService, times(1)).uploadAssignment(eq(assignmentDto), eq(request));
    }

    /**
     * Test grading an assignment and sending notification.
     */
    @Test
    void gradeAssignmentTest() {
        GradeAssignmentDto gradeAssignmentDto = new GradeAssignmentDto();
        gradeAssignmentDto.setAssignmentId(1);
        gradeAssignmentDto.setStudentId(1);
        gradeAssignmentDto.setGrade(100);

        doNothing().when(assignmentService).gradeAssignment(anyInt(), anyInt(), anyFloat(), any(HttpServletRequest.class));
        doNothing().when(notificationsService).sendNotification(anyString(), anyInt());

        ResponseEntity<String> response = assignmentController.gradeAssignment(gradeAssignmentDto, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Assignment has been graded successfully.", response.getBody());

        verify(assignmentService, times(1))
                .gradeAssignment(eq(1), eq(1), eq(100.0f), eq(request));
        verify(notificationsService, times(1))
                .sendNotification(eq("Your assignment (ID: 1) has been graded."), eq(1));
    }

    /**
     * Test saving feedback for an assignment.
     */
    @Test
    void saveAssignmentFeedbackTest() {
        SaveAssignmentDto saveAssignmentDto = new SaveAssignmentDto();
        saveAssignmentDto.setAssignmentId(1);
        saveAssignmentDto.setStudentId(1);
        saveAssignmentDto.setFeedback("Good job");

        doNothing().when(assignmentService)
                .saveAssignmentFeedback(anyInt(), anyInt(), anyString(), any(HttpServletRequest.class));

        ResponseEntity<String> response = assignmentController.saveAssignmentFeedback(saveAssignmentDto, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Assignment feedback saved successfully.", response.getBody());

        verify(assignmentService, times(1))
                .saveAssignmentFeedback(eq(1), eq(1), eq("Good job"), eq(request));
    }

    /**
     * Test fetching feedback for an assignment.
     */
    @Test
    void getFeedbackTest() {
        GetFeedbackDto getFeedbackDto = new GetFeedbackDto();
        getFeedbackDto.setAssignmentId(1);

        when(assignmentService.getFeedback(anyInt(), any(HttpServletRequest.class)))
                .thenReturn("This is a feedback message");

        ResponseEntity<String> response = assignmentController.getFeedback(getFeedbackDto, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("This is a feedback message", response.getBody());
        verify(assignmentService, times(1)).getFeedback(eq(1), eq(request));
    }

    /**
     * Test retrieving submissions for an assignment.
     */
    @Test
    void trackAssignmentSubmissionsTest() {
        List<String> submissions = new ArrayList<>();
        submissions.add("Submission 1");
        submissions.add("Submission 2");

        when(assignmentService.assignmentSubmissions(eq(1), eq(request))).thenReturn(submissions);

        ResponseEntity<?> response = assignmentController.trackAssignmentSubmissions(1, request);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(submissions, response.getBody());
        verify(assignmentService, times(1)).assignmentSubmissions(eq(1), eq(request));
    }
}
