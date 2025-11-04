package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.dto.*;
import com.chhotu.Learning_Management_System.entity.*;
import com.chhotu.Learning_Management_System.repository.*;
import com.chhotu.Learning_Management_System.service.*;
import com.chhotu.Learning_Management_System.dto.GradingDto;
import com.chhotu.Learning_Management_System.dto.QuestionDto;
import com.chhotu.Learning_Management_System.dto.QuizDto;
import com.chhotu.Learning_Management_System.dto.StudentDto;
import com.chhotu.Learning_Management_System.entity.*;
import com.chhotu.Learning_Management_System.repository.*;
import com.chhotu.Learning_Management_System.service.EnrollmentService;
import com.chhotu.Learning_Management_System.service.NotificationsService;
import com.chhotu.Learning_Management_System.service.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QuizServiceImpl implements QuizService {

    private static final Logger logger = LoggerFactory.getLogger(QuizServiceImpl.class);

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final QuestionRepository questionRepository;
    private final ObjectMapper objectMapper;
    private final StudentRepository studentRepository;
    private final GradingRepository gradingRepository;
    private final QuestionTypeRepository questionTypeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final NotificationsService notificationsService;
    private final EnrollmentService enrollmentService;

    List<Question> quizQuestions = new ArrayList<>();
    List<Question> questionBank = new ArrayList<>();

    public QuizServiceImpl(
            QuizRepository quizRepository,
            CourseRepository courseRepository,
            QuestionRepository questionRepository,
            ObjectMapper objectMapper,
            StudentRepository studentRepository,
            GradingRepository gradingRepository,
            QuestionTypeRepository questionTypeRepository,
            EnrollmentRepository enrollmentRepository,
            NotificationsService notificationsService,
            EnrollmentService enrollmentService) {
        this.quizRepository = quizRepository;
        this.courseRepository = courseRepository;
        this.questionRepository = questionRepository;
        this.objectMapper = objectMapper;
        this.studentRepository = studentRepository;
        this.gradingRepository = gradingRepository;
        this.questionTypeRepository = questionTypeRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.notificationsService = notificationsService;
        this.enrollmentService = enrollmentService;
    }

    @Override
    public int Create(Integer course_id, int type_id, HttpServletRequest request) throws Exception {
        logger.info("Creating quiz for course ID: {}", course_id);

        Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found"));

        if (loggedInInstructor == null) {
            logger.error("Attempted quiz creation without login");
            throw new IllegalArgumentException("No logged in user is found.");
        }
        if (loggedInInstructor.getUserTypeId() == null || loggedInInstructor.getUserTypeId().getUserTypeId() != 3) {
            throw new IllegalArgumentException("Logged-in user is not an instructor.");
        }
        if (course.getInstructorId().getUserAccountId() != loggedInInstructor.getUserId()) {
            throw new IllegalArgumentException("Instructor does not have access for this course.");
        }

        if (type_id > 3 || type_id < 1) throw new Exception("Invalid question type.");

        Quiz quiz = new Quiz();
        quiz.setCourse(course);
        quiz.setTitle("quiz" + (quizRepository.findAll().size() + 1));
        quiz.setQuestionCount(5);
        quiz.setRandomized(true);
        quiz.setCreationDate(new Date());

        generateQuestions(quiz, type_id, course);
        quizRepository.save(quiz);
        logger.info("Quiz created successfully with ID: {}", quiz.getQuizId());

        List<StudentDto> enrolledStudents = enrollmentService.viewEnrolledStudents(course_id, request);
        for (StudentDto student : enrolledStudents) {
            notificationsService.sendNotification("A new quiz (ID: " + quiz.getQuizId() +
                    ") is available for course: " + course.getCourseName(), student.getUserAccountId());
        }

        return quiz.getQuizId();
    }

    @Override
    public String getActiveQuiz(int course_id, HttpServletRequest request) {
        logger.info("Fetching active quiz for course ID: {}", course_id);
        Users loggedInUser = (Users) request.getSession().getAttribute("user");

        if (loggedInUser == null) throw new IllegalArgumentException("No user is logged in.");

        boolean instructor = courseRepository.findByInstructorId(loggedInUser.getUserId(), course_id);
        if (loggedInUser.getUserTypeId().getUserTypeId() == 3 && !instructor)
            throw new IllegalArgumentException("You don't have permission to enter this quiz.");

        List<Quiz> quizzes = quizRepository.getQuizzesByCourseId(course_id);
        StringBuilder ids = new StringBuilder();

        for (Quiz quiz : quizzes) {
            if (quiz.getCreationDate().getTime() + 15 * 60 * 1000 > new Date().getTime()) {
                ids.append("Quiz with ID: ").append(quiz.getQuizId())
                        .append(" has time left: ")
                        .append(((quiz.getCreationDate().getTime() + (15 * 60 * 1000) - new Date().getTime()) / (60 * 1000)))
                        .append(" mins\n");
            }
        }
        return ids.isEmpty() ? "No active quizzes." : ids.toString();
    }

    @Override
    public List<QuestionDto> getQuizQuestions(int id, HttpServletRequest request) throws Exception {
        logger.info("Fetching questions for quiz ID: {}", id);
        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with ID: " + id));

        if (loggedInUser == null)
            throw new IllegalArgumentException("No user is logged in.");

        boolean instructor = courseRepository.findByInstructorId(loggedInUser.getUserId(), quiz.getCourse().getCourseId());
        if (loggedInUser.getUserTypeId().getUserTypeId() == 3 && !instructor)
            throw new IllegalArgumentException("You don't have permission for this quiz.");

        if (loggedInUser.getUserTypeId().getUserTypeId() == 2) {
            boolean enrolled = enrollmentRepository.existsByStudentAndCourse(
                    studentRepository.findById(loggedInUser.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException("No student found.")),
                    quiz.getCourse());
            if (!enrolled) throw new IllegalArgumentException("You are not enrolled in this course.");
            if (quiz.getCreationDate().getTime() + 15 * 60 * 1000 < new Date().getTime())
                throw new IllegalArgumentException("The quiz has ended.");
        }

        quizQuestions = questionRepository.findQuestionsByQuizId(id);
        List<QuestionDto> questions = new ArrayList<>();
        for (Question q : quizQuestions) {
            QuestionDto dto = new QuestionDto();
            dto.setOptions(q.getOptions());
            dto.setType(q.getQuestionType().getTypeId());
            dto.setQuestionText(q.getQuestionText());
            dto.setCorrectAnswer(q.getCorrectAnswer());
            dto.setCourseId(q.getCourseId().getCourseId());
            dto.setQuestionId(q.getQuestionId());
            questions.add(dto);
        }
        logger.info("{} questions returned for quiz ID {}", questions.size(), id);
        return questions;
    }

    @Override
    public String getType(int typeID) {
        return switch (typeID) {
            case 1 -> "MCQ";
            case 2 -> "TRUE_FALSE";
            default -> "SHORT_ANSWER";
        };
    }

    @Override
    public void addQuestion(QuestionDto questionDto, HttpServletRequest request) throws Exception {
        logger.info("Adding new question for course ID: {}", questionDto.getCourseId());
        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        Course course = courseRepository.findById(questionDto.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("No course found."));

        if (loggedInUser == null)
            throw new IllegalArgumentException("No user is logged in.");

        boolean instructor = courseRepository.findByInstructorId(loggedInUser.getUserId(), course.getCourseId());
        if (loggedInUser.getUserTypeId().getUserTypeId() == 3 && !instructor)
            throw new IllegalArgumentException("Unauthorized instructor.");
        if (loggedInUser.getUserTypeId().getUserTypeId() == 2)
            throw new IllegalArgumentException("Students cannot add questions.");

        if (questionRepository.findById(questionDto.getQuestionId()).isPresent())
            throw new Exception("Question already exists.");

        Question question = new Question();
        question.setQuestionText(questionDto.getQuestionText());
        question.setCourseId(course);
        question.setCorrectAnswer(questionDto.getCorrectAnswer());

        QuestionType questionType = questionTypeRepository.findById(questionDto.getType())
                .orElseThrow(() -> new EntityNotFoundException("Invalid QuestionType."));
        question.setQuestionType(questionType);

        try {
            question.setOptions(objectMapper.writeValueAsString(questionDto.getOptions()));
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert options to JSON", e);
        }

        questionRepository.save(question);
        logger.info("Question saved successfully for course ID: {}", questionDto.getCourseId());
    }


    @Override
    public void generateQuestions(Quiz quiz, int questionType, Course course_id) throws Exception {
        logger.info("Generating questions for quiz ID: {}, type: {}, course ID: {}", quiz.getQuizId(), questionType, course_id.getCourseId());

        List<Question> allQuestions = questionRepository.findQuestionsByCourseIdAndQuestionType(course_id.getCourseId(), questionType);
        List<Question> emptyQuestions = questionRepository.findEmptyQuestionsByCourseIdAndQuestionType(course_id.getCourseId(), questionType);

        if (allQuestions.size() < 5) {
            logger.error("Not enough questions available for course ID: {}", course_id.getCourseId());
            throw new Exception("No enough Questions to create quiz!\n");
        }

        if (emptyQuestions.size() < 5) {
            logger.error("Not enough unassigned questions found for type {} in course ID: {}", questionType, course_id.getCourseId());
            throw new Exception("No enough unassigned questions to create new quiz! number: " + emptyQuestions.size() + " type " + questionType + "\n");
        }

        Random random = new Random();
        Set<Integer> selectedIndices = new HashSet<>();
        int count = 0;

        while (count < 5) {
            int randomNumber = random.nextInt(allQuestions.size());
            if (!selectedIndices.contains(randomNumber)) {
                selectedIndices.add(randomNumber);
                Question selectedQuestion = allQuestions.get(randomNumber);
                selectedQuestion.setQuiz(quiz);
                count++;
            }
        }

        logger.info("Successfully generated {} random questions for quiz ID: {}", count, quiz.getQuizId());
    }

    @Override
    public QuizDto getQuizByID(int id, HttpServletRequest request) {
        logger.info("Fetching quiz by ID: {}", id);
        Users loggedInUser = (Users) request.getSession().getAttribute("user");

        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No quiz found with the given ID: " + id));

        if (loggedInUser == null) {
            logger.warn("Attempted to access quiz without login.");
            throw new IllegalArgumentException("No user is logged in.");
        }

        boolean instructor = courseRepository.findByInstructorId(loggedInUser.getUserId(), quiz.getCourse().getCourseId());
        if (loggedInUser.getUserTypeId().getUserTypeId() == 3 && !instructor) {
            logger.warn("Unauthorized instructor access attempt for quiz ID: {}", id);
            throw new IllegalArgumentException("You don't have permission to enter this quiz.");
        } else if (loggedInUser.getUserTypeId().getUserTypeId() == 2) {
            boolean enrolled = enrollmentRepository.existsByStudentAndCourse(
                    studentRepository.findById(loggedInUser.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException("No student found with this ID!")),
                    quiz.getCourse());
            if (!enrolled) {
                logger.warn("Unauthorized student access attempt for quiz ID: {}", id);
                throw new IllegalArgumentException("You don't have permission to enter this course.");
            }
        }

        logger.info("Quiz fetched successfully for quiz ID: {}", id);
        QuizDto quizDto = new QuizDto();
        quizDto.setQuizId(quiz.getQuizId());
        quizDto.setTitle(quiz.getTitle());
        quizDto.setCreationDate(quiz.getCreationDate());
        return quizDto;

    }

    @Override
    public void createQuestionBank(int course_id, List<QuestionDto> questions, HttpServletRequest request) throws Exception {
        logger.info("Creating question bank for course ID: {}", course_id);

        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new EntityNotFoundException("No such Course"));
        Users loggedInUser = (Users) request.getSession().getAttribute("user");

        if (loggedInUser == null) {
            logger.error("Unauthorized attempt to create question bank.");
            throw new IllegalArgumentException("No user is logged in.");
        }

        boolean instructor = courseRepository.findByInstructorId(loggedInUser.getUserId(), course_id);
        if (loggedInUser.getUserTypeId().getUserTypeId() == 3 && !instructor) {
            logger.warn("Instructor ID {} attempted unauthorized course access.", loggedInUser.getUserId());
            throw new IllegalArgumentException("You don't have permission to enter this course.");
        }

        if (loggedInUser.getUserTypeId().getUserTypeId() == 2) {
            throw new Exception("You don't have access to this feature!");
        }

        for (QuestionDto dto : questions) {
            Question question = questionRepository.findById(dto.getQuestionId())
                    .orElse(new Question());

            question.setQuestionText(dto.getQuestionText());
            try {
                String optionsAsString = objectMapper.writeValueAsString(dto.getOptions());
                question.setOptions(optionsAsString);
            } catch (Exception e) {
                logger.error("Failed to convert question options to JSON for question ID: {}", dto.getQuestionId(), e);
                throw new RuntimeException("Failed to convert options to JSON", e);
            }
            question.setCorrectAnswer(dto.getCorrectAnswer());
            question.setCourseId(course);

            QuestionType questionType = questionTypeRepository.findById(dto.getType())
                    .orElseThrow(() -> new EntityNotFoundException("No such QuestionType " + dto.getType()));
            question.setQuestionType(questionType);

            questionRepository.save(question);
        }

        logger.info("Question bank created/updated successfully for course ID: {}", course_id);
    }

    @Override
    public QuizDto getQuestionBank(int course_id, HttpServletRequest request) throws Exception {
        logger.info("Fetching question bank for course ID: {}", course_id);
        Users loggedInUser = (Users) request.getSession().getAttribute("user");

        if (loggedInUser == null) {
            throw new IllegalArgumentException("No user is logged in.");
        }

        boolean instructor = courseRepository.findByInstructorId(loggedInUser.getUserId(), course_id);
        Course course = courseRepository.findById(course_id)
                .orElseThrow(() -> new IllegalArgumentException("No course found with the given ID: " + course_id));

        if (loggedInUser.getUserTypeId().getUserTypeId() == 3 && !instructor) {
            throw new IllegalArgumentException("You don't have permission to enter this course.");
        } else if (loggedInUser.getUserTypeId().getUserTypeId() == 2) {
            throw new IllegalArgumentException("You don't have permission to enter this feature!");
        }

        questionBank = questionRepository.findQuestionsByCourseId(course_id);
        if (questionBank.isEmpty()) throw new Exception("This course doesn't have any questions!");

        List<QuestionDto> questionDtos = new ArrayList<>();
        for (Question q : questionBank) {
            QuestionDto dto = new QuestionDto();
            dto.setQuestionId(q.getQuestionId());
            dto.setCorrectAnswer(q.getCorrectAnswer());
            dto.setQuestionText(q.getQuestionText());
            dto.setType(q.getQuestionType().getTypeId());
            dto.setCourseId(q.getCourseId().getCourseId());
            dto.setOptions(q.getOptions());
            questionDtos.add(dto);
        }

        QuizDto quizDto = new QuizDto();
        quizDto.setQuestionList(questionDtos);

        logger.info("Fetched {} questions for course ID: {}", questionDtos.size(), course_id);
        return quizDto;
    }
    @Override
    public void gradeQuiz(GradingDto gradingDto, HttpServletRequest request) throws Exception {
        logger.info("Starting quiz grading for Quiz ID: {}", gradingDto.getQuizId());

        Optional<Quiz> optionalQuiz = Optional.ofNullable(
                quizRepository.findById(gradingDto.getQuizId())
                        .orElseThrow(() -> new EntityNotFoundException("No such Quiz"))
        );
        Quiz quiz = optionalQuiz.get();
        Users loggedInUser = (Users) request.getSession().getAttribute("user");

        if (loggedInUser == null) {
            logger.error("No user is logged in while attempting to grade quiz.");
            throw new IllegalArgumentException("No user is logged in.");
        }

        boolean enrolled = enrollmentRepository.existsByStudentAndCourse(
                studentRepository.findById(loggedInUser.getUserId())
                        .orElseThrow(() -> new IllegalArgumentException("No Student found with this ID!")),
                quiz.getCourse()
        );

        if (loggedInUser.getUserTypeId().getUserTypeId() == 2) {
            if (!enrolled) {
                logger.warn("Unauthorized attempt: student {} not enrolled in course {}.",
                        loggedInUser.getUserId(), quiz.getCourse().getCourseId());
                throw new IllegalArgumentException("You don't have permission to enter this course.");
            }
            if (quiz.getCreationDate().getTime() + 15 * 60 * 1000 < new Date().getTime()) {
                logger.warn("Quiz {} expired before grading attempt by student {}.",
                        quiz.getQuizId(), loggedInUser.getUserId());
                throw new IllegalArgumentException("The quiz has been finished!");
            }
            if (gradingRepository.boolFindGradeByQuizAndStudentID(quiz.getQuizId(), loggedInUser.getUserId()).orElse(false)) {
                logger.warn("Duplicate submission attempt detected for Quiz ID: {}", quiz.getQuizId());
                throw new Exception("You have submitted a response earlier!");
            }
        } else {
            logger.error("Unauthorized role attempted to submit quiz: {}", loggedInUser.getUserTypeId() .getUserTypeId());
            throw new Exception("You are not authorized to submit quizzes!");
        }

        Student student = studentRepository.findById(loggedInUser.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("No Student found with this ID!"));

        // Fetch all quiz questions
        List<Question> gradedQuestions = questionRepository.findQuestionsByQuizId(gradingDto.getQuizId());
        List<String> answersList = gradingDto.getAnswers();
        int grade = 0;

        logger.info("Comparing answers for grading...");
        for (int i = 0; i < gradedQuestions.size(); i++) {
            if (Objects.equals(gradedQuestions.get(i).getCorrectAnswer(), answersList.get(i))) {
                grade++;
            }
        }

        Grading grading = new Grading();
        grading.setGrade(grade);
        grading.setQuiz_id(quiz);
        grading.setStudent_id(student);
        gradingRepository.save(grading);

        int id = quiz.getQuizId();
        logger.info("Quiz ID {} graded successfully with grade: {}", id, grade);
        notificationsService.sendNotification("Quiz " + id + " has been graded", loggedInUser.getUserId());
    }



    @Override
    public int quizFeedback(int quiz_id, int student_id, HttpServletRequest request) throws Exception {
        logger.info("Fetching quiz feedback for Quiz ID: {}, Student ID: {}", quiz_id, student_id);

        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        Quiz quiz = quizRepository.findById(quiz_id)
                .orElseThrow(() -> new IllegalArgumentException("No quiz found with the given ID: " + quiz_id));

        if (loggedInUser == null) {
            logger.error("Attempt to fetch feedback without being logged in.");
            throw new IllegalArgumentException("No user is logged in.");
        }

        boolean instructor = courseRepository.findByInstructorId(loggedInUser.getUserId(),
                quiz.getCourse().getCourseId());

        if (loggedInUser.getUserTypeId().getUserTypeId() == 3) {
            if (!instructor) {
                logger.warn("Instructor {} not authorized to access quiz {}", loggedInUser.getUserId(), quiz_id);
                throw new IllegalArgumentException("You don't have permission to enter this quiz.");
            }
        } else if (loggedInUser.getUserTypeId().getUserTypeId() == 2) {
            boolean enrolled = enrollmentRepository.existsByStudentAndCourse(
                    studentRepository.findById(loggedInUser.getUserId())
                            .orElseThrow(() -> new IllegalArgumentException("No student found with this ID!")),
                    quiz.getCourse()
            );

            if (!enrolled) {
                logger.warn("Unauthorized feedback request: student {} not enrolled.", loggedInUser.getUserId());
                throw new IllegalArgumentException("You don't have permission to enter this course.");
            }

            if (loggedInUser.getUserId() != student_id) {
                logger.error("Student {} attempted to access another student's grade!", loggedInUser.getUserId());
                throw new Exception("You are not authorized to check other student's grades!");
            }
        }

        int grade = gradingRepository.findGradeByQuizAndStudentID(quiz_id, student_id);
        if (grade == -1) {
            logger.warn("Quiz {} not graded yet for student {}.", quiz_id, student_id);
            throw new Exception("Quiz hasn't been graded yet");
        }

        logger.info("Quiz feedback retrieved successfully for Quiz ID: {}, Grade: {}", quiz_id, grade);
        return grade;
    }

    // =====================================
    // quizGrades - Returns all grades for a given quiz (Instructor only)
    // =====================================
    @Override
    public List<String> quizGrades(int quizId, HttpServletRequest request) {
        logger.info("Fetching all quiz grades for Quiz ID: {}", quizId);

        if (quizRepository.existsById(quizId)) {
            Quiz quiz = quizRepository.findById(quizId).get();
            List<Grading> quizGrades = gradingRepository.findAllByQuizId(quiz);
            Users loggedInInstructor = (Users) request.getSession().getAttribute("user");
            int instructorId = quiz.getCourse().getInstructorId().getUserAccountId();

            if (loggedInInstructor == null) {
                logger.error("Attempt to fetch quiz grades without login.");
                throw new IllegalArgumentException("No logged in user is found.");
            } else if (loggedInInstructor.getUserTypeId() == null ||
                    loggedInInstructor.getUserTypeId().getUserTypeId() != 3) {
                logger.warn("Unauthorized access: User {} is not an instructor.", loggedInInstructor.getUserId());
                throw new IllegalArgumentException("Logged-in user is not an instructor.");
            } else if (instructorId != loggedInInstructor.getUserId()) {
                logger.error("Instructor {} tried to access grades of a quiz they don’t own.", loggedInInstructor.getUserId());
                throw new IllegalArgumentException("Logged-in instructor does not have access for this quiz grades.");
            }

            List<String> grades = new ArrayList<>();
            for (Grading grading : quizGrades) {
                Student student = grading.getStudent_id();
                String studentGrade = "(ID)" + student.getUserAccountId() + ": (Grade)" + grading.getGrade();
                grades.add(studentGrade);
            }

            logger.info("Successfully fetched {} grades for Quiz ID: {}", grades.size(), quizId);
            return grades;
        } else {
            logger.error("Quiz with ID {} not found.", quizId);
            throw new IllegalArgumentException("Quiz with ID " + quizId + " not found.");
        }
    }
}