package com.chhotu.Learning_Management_System.repository;

import com.chhotu.Learning_Management_System.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Integer> {
}
