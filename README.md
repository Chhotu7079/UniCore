# 🧑‍💻 Unicore – Learning Management System (LMS)

![Java](https://img.shields.io/badge/Java-17-orange?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/Database-MySQL-blue?logo=mysql)
![Maven](https://img.shields.io/badge/Build-Maven-yellow?logo=apache-maven)
![License](https://img.shields.io/badge/License-MIT-lightgrey)
![Contributions](https://img.shields.io/badge/Contributions-Welcome-brightgreen)

---

## 📘 Overview

**Unicore** is a full-featured **Learning Management System (LMS)** built using **Java Spring Boot**.  
It provides a **centralized digital environment** for managing courses, users, and learning content efficiently.  
The platform is designed to **streamline online education** through automated workflows, secure access control, and real-time performance tracking.

It offers **role-based access** for:
- 🧑‍💼 **Admins** – manage users, courses, and overall platform activities.  
- 👩‍🏫 **Instructors** – create, manage, and grade assignments, quizzes, and lectures.  
- 🎓 **Students** – enroll in courses, submit assignments, and track progress.  

Unicore promotes **collaboration**, **data-driven learning**, and **secure management** of educational resources, ensuring a seamless experience for all stakeholders.

---

## 🚀 Project Highlights

- **🧩 Role-Based Authentication:** Secure login and authorization using JWT and Spring Security.  
- **📚 Course Management:** Admins and instructors can create, update, and organize courses.  
- **🧠 Quiz & Assessment System:** Interactive quiz and grading features for evaluating students.  
- **📈 Performance Tracking:** Students can view their scores, submissions, and learning progress.  
- **📅 Attendance Module:** Digital attendance tracking for instructors and admins.  
- **💬 Communication Channel:** Enables announcements and course-related discussions.  
- **🔍 RESTful APIs:** Clean and scalable backend design for easy integration with front-end frameworks.  
- **🛡️ Secure Data Handling:** Uses BCrypt encryption and validation for user credentials.  

---

## 🏗️ Tech Stack

| Category | Technology |
|-----------|-------------|
| **Backend** | Java, Spring Boot, Spring Security, Hibernate/JPA |
| **Database** | MySQL |
| **Build Tool** | Maven |
| **API Testing** | Postman |
| **Version Control** | Git & GitHub |
| **Deployment (Optional)** | Docker / AWS EC2 / Render |

---

## ⚙️ System Architecture

            ┌────────────────────┐
            │      Frontend      │
            │ (React / Angular)  │
            └────────┬───────────┘
                     │ REST API
            ┌────────┴───────────┐
            │   Spring Boot App  │
            │  (Controllers,     │
            │  Services, DAO)    │
            └────────┬───────────┘
                     │ JPA / Hibernate
            ┌────────┴───────────┐
            │      MySQL DB      │
            └────────────────────┘


---

## 🧰 Installation & Setup

### 1️⃣ Clone the repository
```bash
git clone https://github.com/your-username/Unicore-LMS.git
cd chhotu-LMS
```
---

### Application Configuration
Below is the configuration used in the application.properties file for UniCore – Learning Management System.
These settings handle the server, database, mail, security, and logging configuration.
```bash
# =====================================================
# =============== SERVER CONFIGURATION =================
# =====================================================
server.port=8484
spring.application.name=UniCore-LMS

# =====================================================
# =============== DATABASE CONFIGURATION ===============
# =====================================================
spring.datasource.url=jdbc:mysql://localhost:3306/dbname?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password= add your password 
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# =====================================================
# ================= JPA / HIBERNATE ===================
# =====================================================
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=true

# =====================================================
# ================== MAIL CONFIG ======================
# =====================================================
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=add your email id
spring.mail.password=add your paassword  
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# =====================================================
# ================= SECURITY CONFIG ===================
# =====================================================
spring.security.user.name=admin
spring.security.user.password=admin123
spring.security.user.roles=ADMIN

# =====================================================
# ================== LOGGING CONFIG ===================
# =====================================================
logging.level.org.springframework=INFO
logging.level.com.LMS=DEBUG
logging.file.name=logs/lms_app.log

# =====================================================
# ================== SWAGGER CONFIG ===================
# =====================================================
# Swagger/OpenAPI Configuration
springdoc.api-docs.enabled=true
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=alpha
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.docExpansion=none

# =====================================================
# ================== JWT CONFIG (Optional) =============
# =====================================================
# jwt.secret=your_secret_key_here
# jwt.expiration=3600000  # 1 hour in milliseconds

```
---
Once your application is running, open:
👉 http://localhost:8484/swagger-ui.html

## 🧠 Future Enhancements

✅ Add AI-based quiz generation

✅ Integrate video lectures using cloud storage (AWS S3)

✅ Implement chat and discussion forums

✅ Generate detailed analytics dashboards for performance visualization

✅ Mobile-friendly responsive frontend


## 📄 License
This project is licensed under the MIT License – see the LICENSE
file for details.

## 💡 Author

👨‍💻 Chhotu Kumar
Java Full Stack Developer | Spring Boot | React | MySQL


