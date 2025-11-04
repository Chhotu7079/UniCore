package com.chhotu.Learning_Management_System.service;

import com.chhotu.Learning_Management_System.dto.GradingDto;
import com.chhotu.Learning_Management_System.dto.QuestionDto;
import com.chhotu.Learning_Management_System.dto.QuizDto;
import com.chhotu.Learning_Management_System.entity.Course;
import com.chhotu.Learning_Management_System.entity.Quiz;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

public interface QuizService {

    int Create(Integer course_id, int type_id, HttpServletRequest request) throws Exception;

    String getActiveQuiz(int course_id, HttpServletRequest request);

    List<QuestionDto> getQuizQuestions(int id, HttpServletRequest request) throws Exception;

    String getType(int typeID);

    void addQuestion(QuestionDto questionDto, HttpServletRequest request) throws Exception;

    void generateQuestions(Quiz quiz,
                           int questionType,
                           Course course_id) throws Exception;

    QuizDto getQuizByID(int id, HttpServletRequest request);

    void createQuestionBank(int course_id, List<QuestionDto> questions, HttpServletRequest request) throws Exception;

    QuizDto getQuestionBank(int course_id, HttpServletRequest request) throws Exception;

    void gradeQuiz(GradingDto gradingDto, HttpServletRequest request) throws Exception;

    int quizFeedback(int quiz_id, int student_id, HttpServletRequest request) throws Exception;

    List<String> quizGrades(int quizId, HttpServletRequest request);
}
