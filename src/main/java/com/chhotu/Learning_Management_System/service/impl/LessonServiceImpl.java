package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.dto.LessonDto;
import com.chhotu.Learning_Management_System.entity.*;
import com.chhotu.Learning_Management_System.repository.*;
import com.chhotu.Learning_Management_System.entity.*;
import com.chhotu.Learning_Management_System.repository.*;
import com.chhotu.Learning_Management_System.service.LessonService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LessonServiceImpl implements LessonService {

    private static final Logger logger = LoggerFactory.getLogger(LessonServiceImpl.class);

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonAttendanceRepository lessonAttendanceRepository;
    private final StudentRepository studentRepository;

    public LessonServiceImpl(LessonRepository lessonRepository,
                             CourseRepository courseRepository,
                             EnrollmentRepository enrollmentRepository,
                             LessonAttendanceRepository lessonAttendanceRepository,
                             StudentRepository studentRepository) {
        this.lessonRepository = lessonRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonAttendanceRepository = lessonAttendanceRepository;
        this.studentRepository = studentRepository;
    }

    /**
     * Adds a new lesson to a course (Instructor only).
     */
    @Override
    public void addLesson(Lesson lesson, HttpServletRequest request) {
        logger.info("Attempting to add lesson for courseId={}", lesson.getCourseId().getCourseId());
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");

        if (loggedInInstructor == null)
            throw new IllegalArgumentException("No user is logged in.");
        if (loggedInInstructor.getUserTypeId() == null || loggedInInstructor.getUserTypeId().getUserTypeId() != 3)
            throw new IllegalArgumentException("Logged-in user is not an instructor.");

        Course course = courseRepository.findById(lesson.getCourseId().getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("No such CourseId"));

        if (loggedInInstructor.getUserId() != course.getInstructorId().getUserAccountId())
            throw new IllegalArgumentException("You are not the Instructor of this course");

        if (lesson.getOTP() == null || lesson.getOTP().isEmpty())
            throw new IllegalArgumentException("OTP value cannot be null");

        lesson.setCreationTime(new Date(System.currentTimeMillis()));
        lesson.setCourseId(course);
        lessonRepository.save(lesson);
        logger.info("Lesson added successfully by Instructor ID {}", loggedInInstructor.getUserId());
    }

    /**
     * Retrieves all lessons for a specific course (Instructor or Enrolled Student).
     */
    @Override
    public List<LessonDto> getLessonsByCourseId(int courseId, HttpServletRequest request) {
        logger.info("Fetching lessons for courseId={}", courseId);
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No such CourseId"));
        Users loggedInUser = (Users) request.getSession().getAttribute("user");

        if (loggedInUser == null)
            throw new IllegalArgumentException("No user is logged in.");

        if (loggedInUser.getUserTypeId().getUserTypeId() == 2) {
            boolean enrolled = enrollmentRepository.existsByStudentAndCourse(
                    studentRepository.findById(loggedInUser.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException("No student found with this ID!")),
                    course);
            if (!enrolled)
                throw new IllegalArgumentException("You are not enrolled in this course.");
        }

        List<Lesson> lessons = lessonRepository.findByCourseId(course);
        logger.info("Found {} lessons for courseId={}", lessons.size(), courseId);
        return convertToCoueDtoList(lessons, courseId);
    }

    /**
     * Fetch a single lesson by its ID.
     */
    @Override
    public LessonDto getLessonById(int lessonId, HttpServletRequest request) {
        logger.info("Fetching lesson with ID {}", lessonId);
        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        if (loggedInUser == null)
            throw new IllegalArgumentException("No user is logged in.");

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("No such LessonId: " + lessonId));

        return new LessonDto(
                lesson.getLessonId(),
                lesson.getCourseId().getCourseId(),
                lesson.getLessonName(),
                lesson.getLessonDescription(),
                lesson.getLessonOrder(),
                lesson.getOTP(),
                lesson.getContent(),
                lesson.getCreationTime()
        );
    }

    /**
     * Updates lesson details (Instructor only).
     */
    @Override
    public void updateLesson(int lessonId, Lesson updatedLesson, HttpServletRequest request) {
        logger.info("Updating lesson ID {}", lessonId);
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");

        if (loggedInInstructor == null)
            throw new IllegalArgumentException("No user is logged in.");
        if (loggedInInstructor.getUserTypeId() == null || loggedInInstructor.getUserTypeId().getUserTypeId() != 3)
            throw new IllegalArgumentException("Logged-in user is not an instructor.");

        Course course = courseRepository.findById(updatedLesson.getCourseId().getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("No such CourseId"));

        if (loggedInInstructor.getUserId() != course.getInstructorId().getUserAccountId())
            throw new IllegalArgumentException("You are not the Instructor of this course");

        Lesson existingLesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + lessonId));

        existingLesson.setLessonName(updatedLesson.getLessonName());
        existingLesson.setLessonDescription(updatedLesson.getLessonDescription());
        existingLesson.setLessonOrder(updatedLesson.getLessonOrder());
        existingLesson.setContent(updatedLesson.getContent());
        existingLesson.setOTP(updatedLesson.getOTP());

        lessonRepository.save(existingLesson);
        logger.info("Lesson ID {} updated successfully", lessonId);
    }

    /**
     * Deletes a lesson (Instructor only).
     */
    @Override
    public void deleteLesson(int lessonId, int courseId, HttpServletRequest request) {
        logger.info("Deleting lessonId={} from courseId={}", lessonId, courseId);
        Course course = check_course_before_logic(courseId, request);
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");

        if (course.getInstructorId().getUserAccountId() != loggedInInstructor.getUserId())
            throw new IllegalArgumentException("You are not the Instructor of this course");

        lessonRepository.deleteById(lessonId);
        logger.info("Lesson ID {} deleted successfully", lessonId);
    }

    @Override
    public void studentEnterLesson(int courseId, int lessonId, String otp, HttpServletRequest request) {
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if(loggedInInstructor ==null)
        {
            throw new IllegalArgumentException("No user is logged in.");
        }
        Course existingCourse = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No course found with the given ID: " + courseId));

        List<Enrollment>enrollments = enrollmentRepository.findByCourse(existingCourse);
        int flag=0;
        for (Enrollment enrollment : enrollments) {
            if(enrollment.getStudent().getUserAccountId() == loggedInInstructor.getUserId()){
                flag = 1;
            }
        }
        if(flag==0)
            throw new IllegalArgumentException("You are not enrolled to this course.");

        Lesson existingLesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new IllegalArgumentException("Lesson not found with ID: " + lessonId));
        if (!Objects.equals(existingLesson.getOTP(), otp))
        {
            throw new IllegalArgumentException("OTP does not match.");
        }
        // part for attendance tracking
        Student student = new Student();
        student.setUserAccountId(loggedInInstructor.getUserId());
        boolean enteredAlready = lessonAttendanceRepository.existsByLessonIdAndStudentId( existingLesson,student);
        if(enteredAlready){
            return;
        }
        LessonAttendance lessonAttendance= new LessonAttendance();
        lessonAttendance.setLessonId(existingLesson);
        lessonAttendance.setStudentId(student);
        lessonAttendanceRepository.save(lessonAttendance);


    }


    /**
     * Retrieves attendance list for a lesson (Instructor only).
     */
    @Override
    public List<String> lessonAttendance(int lessonId, HttpServletRequest request) {
        logger.info("Fetching attendance for lessonId={}", lessonId);
        if (!lessonRepository.existsById(lessonId))
            throw new IllegalArgumentException("Lesson with ID " + lessonId + " not found.");

        Lesson lesson = lessonRepository.findById(lessonId).get();
        List<LessonAttendance> attendances = lessonAttendanceRepository.findAllByLessonId(lesson);
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");

        if (loggedInInstructor == null)
            throw new IllegalArgumentException("No logged-in user is found.");
        if (loggedInInstructor.getUserTypeId() == null || loggedInInstructor.getUserTypeId().getUserTypeId() != 3)
            throw new IllegalArgumentException("Logged-in user is not an instructor.");
        if (lesson.getCourseId().getInstructorId().getUserAccountId() != loggedInInstructor.getUserId())
            throw new IllegalArgumentException("Instructor does not have access for this lesson attendance.");

        List<String> studentIds = attendances.stream()
                .map(a -> Integer.toString(a.getStudentId().getUserAccountId()))
                .collect(Collectors.toList());

        logger.info("Attendance retrieved for lessonId={} with {} students", lessonId, studentIds.size());
        return studentIds;
    }

    // Helper: Convert Lesson -> DTO
    private List<LessonDto> convertToCoueDtoList(List<Lesson> lessons, int courseId) {
        return lessons.stream()
                .map(lesson -> new LessonDto(
                        lesson.getLessonId(),
                        courseId,
                        lesson.getLessonName(),
                        lesson.getLessonDescription(),
                        lesson.getLessonOrder(),
                        lesson.getOTP(),
                        lesson.getContent(),
                        lesson.getCreationTime()))
                .collect(Collectors.toList());
    }

    // Helper: Check if user is authorized instructor for the course
    private Course check_course_before_logic(int courseId, HttpServletRequest request) {
        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        if (loggedInInstructor == null)
            throw new IllegalArgumentException("No user is logged in.");
        if (loggedInInstructor.getUserTypeId() == null || loggedInInstructor.getUserTypeId().getUserTypeId() != 3)
            throw new IllegalArgumentException("Logged-in user is not an instructor.");

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("No course found with the given ID: " + courseId));

        if (course.getInstructorId() == null ||
                course.getInstructorId().getUserAccountId() != loggedInInstructor.getUserId())
            throw new IllegalArgumentException("You are not authorized for this course.");

        return course;
    }
}
