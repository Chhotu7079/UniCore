package com.chhotu.Learning_Management_System.service.impl;

import com.chhotu.Learning_Management_System.entity.Student;
import com.chhotu.Learning_Management_System.entity.Users;
import com.chhotu.Learning_Management_System.repository.StudentRepository;
import com.chhotu.Learning_Management_System.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StudentServiceImpl implements StudentService {

    private static final Logger logger = LoggerFactory.getLogger(StudentServiceImpl.class);
    private final StudentRepository studentRepository;

    public StudentServiceImpl(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
        logger.info("StudentServiceImpl initialized successfully.");
    }

    /**
     * Retrieves a student by their ID.
     *
     * @param userId the ID of the student.
     * @return Optional containing the student if found.
     */
    @Override
    public Optional<Student> findById(int userId) {
        logger.info("Fetching student with ID: {}", userId);
        Optional<Student> student = studentRepository.findById(userId);

        if (student.isPresent()) {
            logger.debug("Student found: {}", student.get().getFirstName());
        } else {
            logger.warn("No student found with ID: {}", userId);
        }

        return student;
    }

    /**
     * Saves or updates a student's profile after validating the logged-in user.
     *
     * @param studentId ID of the student to update.
     * @param student   Updated student details.
     * @param request   HTTP request to access the session user.
     */
    @Override
    public void save(int studentId, Student student, HttpServletRequest request) {
        logger.info("Attempting to save student with ID: {}", studentId);

        Users loggedInUser = (Users) request.getSession().getAttribute("user");
        if (loggedInUser == null) {
            logger.error("No user is logged in while attempting to update student.");
            throw new IllegalArgumentException("User is not logged in.");
        }

        if (studentId != loggedInUser.getUserId()) {
            logger.warn("Unauthorized attempt to update another student's data. Logged-in ID: {}, Target ID: {}",
                    loggedInUser.getUserId(), studentId);
            throw new IllegalArgumentException("You are not authorized to perform this action.");
        }

        Student existingStudent = studentRepository.getReferenceById(studentId);
        existingStudent.setFirstName(student.getFirstName());
        existingStudent.setLastName(student.getLastName());

        studentRepository.save(existingStudent);
        logger.info("Student with ID: {} has been successfully updated.", studentId);
    }
}
