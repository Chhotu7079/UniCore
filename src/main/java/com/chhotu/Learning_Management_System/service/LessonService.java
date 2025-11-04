package com.chhotu.Learning_Management_System.service;

import com.chhotu.Learning_Management_System.dto.LessonDto;
import com.chhotu.Learning_Management_System.entity.Lesson;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface LessonService {

    void addLesson(Lesson lesson, HttpServletRequest request);

    List<LessonDto> getLessonsByCourseId(int courseId, HttpServletRequest request);

    LessonDto getLessonById(int lessonId, HttpServletRequest request);

    void updateLesson(int lessonId, Lesson updatedLesson, HttpServletRequest request);

    void deleteLesson(int lessonId, int courseId, HttpServletRequest request);

    void studentEnterLesson(int courseId, int lessonId, String otp, HttpServletRequest request);

    List<String> lessonAttendance(int lessonId, HttpServletRequest request);

}
