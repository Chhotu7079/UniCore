package com.chhotu.Learning_Management_System.repository;


import com.chhotu.Learning_Management_System.entity.*;
import com.chhotu.Learning_Management_System.entity.Course;
import com.chhotu.Learning_Management_System.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByCourseId(Course course);
}
