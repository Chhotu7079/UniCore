//package com.LMS.Learning_Management_System.dto;
//
//
//import java.util.Date;
//
//public class LessonDto {
//    private int lessonId;
//    private int courseId;
//    private String lessonName;
//    private String lessonDescription;
//    private int lessonOrder;
//    private String OTP;
//    private String content;
//    private Date creationTime;
//    public LessonDto() {
//
//    }
//
//    public LessonDto(int lessonId, int courseId , String lessonName, String lessonDescription, int lessonOrder, String OTP, String content, Date creationTime) {
//        this.lessonId = lessonId;
//        this.courseId = courseId;
//        this.lessonName = lessonName;
//        this.lessonDescription = lessonDescription;
//        this.lessonOrder = lessonOrder;
//        this.OTP = OTP;
//        this.content = content;
//        this.creationTime = creationTime;
//    }
//
//
//    public int getLessonId() {
//        return lessonId;
//    }
//
//    public void setLessonId(int lessonId) {
//        this.lessonId = lessonId;
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
//    public String getLessonName() {
//        return lessonName;
//    }
//
//    public void setLessonName(String lessonName) {
//        this.lessonName = lessonName;
//    }
//
//    public String getLessonDescription() {
//        return lessonDescription;
//    }
//
//    public void setLessonDescription(String lessonDescription) {
//        this.lessonDescription = lessonDescription;
//    }
//
//    public int getLessonOrder() {
//        return lessonOrder;
//    }
//
//    public void setLessonOrder(int lessonOrder) {
//        this.lessonOrder = lessonOrder;
//    }
//
//    public String getOTP() {
//        return OTP;
//    }
//
//    public void setOTP(String OTP) {
//        this.OTP = OTP;
//    }
//
//    public String getContent() {
//        return content;
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//    }
//
//    public Date getCreationTime() {
//        return creationTime;
//    }
//
//    public void setCreationTime(Date creationTime) {
//        this.creationTime = creationTime;
//    }
//}



package com.chhotu.Learning_Management_System.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Data Transfer Object (DTO) for transferring lesson-related information
 * between different layers of the application.
 *
 * It contains minimal lesson data such as lesson name, order, content,
 * and creation timestamp to avoid exposing unnecessary details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO representing essential lesson details")
public class LessonDto {

    @Schema(description = "Unique identifier for the lesson", example = "101")
    private int lessonId;

    @Schema(description = "Associated course ID for the lesson", example = "12")
    private int courseId;

    @Schema(description = "Name of the lesson", example = "Introduction to Java")
    private String lessonName;

    @Schema(description = "Brief description of the lesson", example = "Covers basic Java syntax and OOP concepts")
    private String lessonDescription;

    @Schema(description = "Order of the lesson within the course", example = "1")
    private int lessonOrder;

    @Schema(description = "One-time password or access code for the lesson (if applicable)")
    private String otp;

    @Schema(description = "Main lesson content or link to learning material")
    private String content;

    @Schema(description = "Timestamp when the lesson was created", example = "2025-10-31T12:30:00Z")
    private Date creationTime;
}
