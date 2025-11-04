//package com.LMS.Learning_Management_System.dto;
//
//import entity.com.chhotu.Learning_Management_System.Grading;
//
//import java.util.List;
//
//public class GradingDto {
//    private int quiz_id;
//    private List<String> answers ;
//    private int student_id;
//    private int grades;
//    private List<Grading> allGrades;
//
//    public List<Grading> getAllGrades() {
//        return allGrades;
//    }
//
//    public void setAllGrades(List<Grading> allGrades) {
//        this.allGrades = allGrades;
//    }
//
//    public int getQuiz_id() {
//        return quiz_id;
//    }
//
//    public void setQuiz_id(int quiz_id) {
//        this.quiz_id = quiz_id;
//    }
//
//    public List<String> getAnswers() {
//        return answers;
//    }
//
//    public void setAnswers(List<String> answers) {
//        this.answers = answers;
//    }
//
//    public int getStudent_id() {
//        return student_id;
//    }
//
//    public void setStudent_id(int student_id) {
//        this.student_id = student_id;
//    }
//
//    public int getGrades() {
//        return grades;
//    }
//
//    public void setGrades(int grades) {
//        this.grades = grades;
//    }
//}








package com.chhotu.Learning_Management_System.dto;

import com.chhotu.Learning_Management_System.entity.Grading;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for handling grading-related data such as quiz submissions,
 * student answers, and grade results.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object for quiz grading details")
public class GradingDto {

    @Schema(description = "Unique ID of the quiz", example = "101")
    private int quizId;

    @Schema(description = "List of answers submitted by the student")
    private List<String> answers;

    @Schema(description = "Unique ID of the student", example = "501")
    private int studentId;

    @Schema(description = "Calculated grade for the quiz", example = "85")
    private int grades;

    @Schema(description = "List of all grading records (optional field)")
    private List<Grading> allGrades;
}
