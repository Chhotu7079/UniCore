//package com.LMS.Learning_Management_System.dto;
//
//import entity.com.chhotu.Learning_Management_System.Question;
//import lombok.Getter;
//import lombok.Setter;
//
//import java.util.Date;
//import java.util.List;
//
//public class QuizDto {
//
//    private int quizId;
//
//    private String title;
//
//    private Date creation_date;
//    private int type;
//
//    private List<QuestionDto> questionList;
//
//    private int course_id;
//
//
//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//
//    public int getQuizId() {
//        return quizId;
//    }
//
//    public void setQuizId(int quizId) {
//        this.quizId = quizId;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public Date getCreation_date() {
//        return creation_date;
//    }
//
//    public void setCreation_date(Date creation_date) {
//        this.creation_date = creation_date;
//    }
//
//    public List<QuestionDto> getQuestionList() {
//        return questionList;
//    }
//
//    public void setQuestionList(List<QuestionDto> questionList) {
//        this.questionList = questionList;
//    }
//
//    public int getCourse_id() {
//        return course_id;
//    }
//
//    public void setCourse_id(int course_id) {
//        this.course_id = course_id;
//    }
//
//    public QuizDto(int quizId, String title, Date creation_date) {
//        this.quizId = quizId;
//        this.title = title;
//        this.creation_date = creation_date;
//        //this.questionList=questionList;
//    }
//    public QuizDto(){}
//
//}




package com.chhotu.Learning_Management_System.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object representing a Quiz entity.
 * This class is used to transfer quiz-related data between application layers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing quiz details along with associated questions")
public class QuizDto {

    @Schema(description = "Unique identifier for the quiz", example = "101")
    private int quizId;

    @Schema(description = "Title or name of the quiz", example = "Java Basics Quiz")
    private String title;

    @Schema(description = "Date when the quiz was created", example = "2025-10-31T10:15:30Z")
    private Date creationDate;

    @Schema(description = "Type of quiz (e.g., 1 = Practice, 2 = Assessment)", example = "1")
    private int type;

    @Schema(description = "List of questions associated with this quiz")
    private List<QuestionDto> questionList;

    @Schema(description = "Course ID to which this quiz belongs", example = "5")
    private int courseId;
}
