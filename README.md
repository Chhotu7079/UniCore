# 🎓 Unicore – Learning Management System (LMS)

![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue?logo=mysql)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)
![Build Tool](https://img.shields.io/badge/Build-Maven-red?logo=apachemaven)

---

## 🧩 Overview

**Unicore** is a **web-based Learning Management System (LMS)** built using **Java Spring Boot** that simplifies online learning, course management, and student performance tracking.  
It offers **role-based access control** for **Admins**, **Instructors**, and **Students**, allowing educational institutions to manage all learning activities in one secure platform.

---

## 🚀 Features

### 👥 1. User Management
- **Role-Based Access Control** – Supports Admin, Instructor, and Student roles.  
- **Secure Registration & Login** – Authentication handled via Spring Security.  
- **Admin-Only User Creation** – Admins can create new users (validated at service level).  
- **Profile Management** – Users can view and update their profile information.

### 📚 2. Course Management
- **Course Creation & Management** – Instructors can create and manage courses with files, videos, and PDFs.  
- **Enrollment Handling** – Students can enroll in available courses.  
- **Attendance Tracking** – Instructors generate OTPs for each session to verify student attendance.

### 🧾 3. Assessments & Grading
- **Quiz Management** – Supports MCQs, True/False, and Short Answer questions.  
- **Assignment Submission** – Students upload assignments for grading.  
- **Grading & Feedback** – Instructors review and grade student work.

### 📈 4. Performance Tracking
- **Student Dashboard** – Tracks quiz performance, assignment grades, and attendance.  
- **Instructor Analytics** – Monitor student progress and performance trends.

### 🔔 5. Notifications
- **System Alerts** – Real-time notifications for enrollments, grades, and updates.  
- **Email Notifications** – Integrated via JavaMailSender.

### 🧮 6. Bonus Features
- **Excel Report Generation** – Generate performance reports using Apache POI.  
- **Visual Analytics** – Display student progress and course completion through charts.

---

## 🧱 Technical Stack

| Layer | Technology |
|-------|-------------|
| **Backend** | Java 17, Spring Boot 3.x |
| **Database** | MySQL / PostgreSQL |
| **Authentication** | Spring Security (BCrypt PasswordEncoder) |
| **Email Service** | JavaMailSender |
| **Reporting** | Apache POI |
| **Testing** | JUnit |
| **Build Tool** | Maven |

---

## 🧰 System Architecture

