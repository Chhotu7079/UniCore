//package com.LMS.Learning_Management_System.dto;
//
//import com.fasterxml.jackson.databind.JsonNode;
//
//public class QuestionDto {
//    private int question_id;
//    private String question_text;
//    private int type;
//    private String options;
//    private int course_id;
//    private String correct_answer;
//
//    public int getQuestion_id() {
//        return question_id;
//    }
//
//    public void setQuestion_id(int question_id) {
//        this.question_id = question_id;
//    }
//
//    public String getQuestion_text() {
//        return question_text;
//    }
//
//    public void setQuestion_text(String question_text) {
//        this.question_text = question_text;
//    }
//
//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public String getOptions() {
//        return options;
//    }
//
//    public void setOptions(String options) {
//        this.options = options;
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
//    public String getCorrect_answer() {
//        return correct_answer;
//    }
//
//    public void setCorrect_answer(String correct_answer) {
//        this.correct_answer = correct_answer;
//    }
//}


package com.chhotu.Learning_Management_System.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for transferring question-related data between layers.
 * Represents a question belonging to a specific course in the LMS.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO for representing question data")
public class QuestionDto {

    @Schema(description = "Unique identifier of the question", example = "101")
    private int questionId;

    @Schema(description = "Text or content of the question", example = "What is Java Virtual Machine?")
    private String questionText;

    @Schema(description = "Type of question (e.g., 1 = MCQ, 2 = True/False, 3 = Descriptive)", example = "1")
    private int type;

    @Schema(description = "Options for the question in JSON format (if applicable)", example = "[\"Option A\", \"Option B\", \"Option C\", \"Option D\"]")
    private String options;

    @Schema(description = "Associated course ID for the question", example = "5")
    private int courseId;

    @Schema(description = "Correct answer to the question", example = "Option A")
    private String correctAnswer;
}

