# ASU CSE360 HW4 - Student Question and Answer System (sQaaS™)
## Staff Role Implementation
# Anthony Ridings

Screencast link ---> https://drive.google.com/file/d/1dx3e8SnPChbrsP-PIYuI4N9Db2xcqSDO/view?usp=sharing

**Course:** CSE 360 - Introduction to Software Engineering  
**Semester:** Fall 2024  
**Assignment:** Homework 4 - Staff Role Epic Implementation  
**Repository:** https://github.com/CrackParrot11/ASU-CSE360-HW4

---

## 📋 Table of Contents
- [Project Overview](#project-overview)
- [Staff Role Features](#staff-role-features)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Building the Project](#building-the-project)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Documentation](#documentation)
- [User Stories Implemented](#user-stories-implemented)
- [Database Schema](#database-schema)
- [Contributors](#contributors)

---

## 🎯 Project Overview

This project implements a **Student Question and Answer System (sQaaS™)** with a focus on the **Staff Role** functionality. The Staff role enables designated users to monitor and assess interactions within the Q&A system, helping to identify potential issues early and maintain a positive learning environment.

### Key Components
- **Admin Role**: User management, invitation codes, role assignment
- **Student Role**: Ask questions, provide answers, review system
- **Reviewer Role**: Post reviews on answers, provide expert guidance
- **Staff Role** ⭐ **(NEW - HW4)**: Monitor system activity, flag content, post reviews, add notes

---

## ✨ Staff Role Features

The Staff Role implementation includes the following capabilities:

### 1. **System Overview Dashboard**
- View system-wide statistics (questions, answers, reviews, private messages)
- Monitor resolution rates and system health
- Track recent activity highlights

### 2. **Question Review Interface**
- View all questions with filtering options (All/Unresolved/Resolved/Flagged)
- Access detailed question information including answers and private feedback
- Add internal staff notes to questions for tracking
- Flag inappropriate questions for instructor review

### 3. **Answer Review Interface**
- View all answers with sorting options (Recent/Upvoted/By Reviewers)
- Post reviews on answers to guide quality standards
- Flag problematic answers for instructor attention
- View question context for each answer

### 4. **Reviews Monitoring**
- Dedicated tab to view all reviews posted in the system
- Monitor review quality across all reviewers
- Track which answers have been reviewed
- Quick access to question threads from reviews

### 5. **Private Feedback Monitoring**
- Review all private feedback messages between users
- Identify potential conflicts or communication issues
- View conversation context and participants

### 6. **Alerts & Flagging System**
- Automated alerts for unanswered questions
- Alerts for high-activity unresolved questions
- View all flagged content with reasons
- Quick access to flagged items for review

---

## 📁 Project Structure
```
ASU-CSE360-HW4/
├── Dev/
│   └── FoundationCode/
│       ├── src/
│       │   ├── application/
│       │   │   ├── AdminHomePage.java
│       │   │   ├── AdminSetupPage.java
│       │   │   ├── Answer.java
│       │   │   ├── AnswerFeedback.java
│       │   │   ├── FirstPage.java
│       │   │   ├── InputValidator.java
│       │   │   ├── InvitationPage.java
│       │   │   ├── PasswordEvaluator.java
│       │   │   ├── PasswordEvaluationTestingAutomation.java
│       │   │   ├── QASystemTest.java ⭐ (Updated with 13 new tests)
│       │   │   ├── Question.java
│       │   │   ├── Review.java
│       │   │   ├── ReviewReply.java
│       │   │   ├── SetupAccountPage.java
│       │   │   ├── SetupLoginSelectionPage.java
│       │   │   ├── SpellChecker.java
│       │   │   ├── StaffHomePage.java ⭐ (NEW - Complete Staff Interface)
│       │   │   ├── StartCSE360.java
│       │   │   ├── StudentQAPage.java
│       │   │   ├── User.java
│       │   │   ├── UserDatabaseUI.java
│       │   │   ├── UserHomePage.java
│       │   │   ├── UserLoginPage.java
│       │   │   └── WelcomeLoginPage.java
│       │   ├── databasePart1/
│       │   │   └── DatabaseHelper.java ⭐ (Updated with Staff methods)
│       │   └── styles.css
│       ├── doc/ ⭐ (Javadoc HTML documentation)
│       │   ├── index.html
│       │   ├── application/
│       │   └── databasePart1/
│       ├── lib/
│       │   └── h2-2.2.220.jar
│       ├── .classpath
│       └── .project
├── README.md
└── .gitignore
```
👥 Contributors
Developer: [Anthony Ridings]
Course: CSE 360 - Introduction to Software Engineering
Institution: Arizona State University
Semester: Fall 2024

📄 License
This project is created for educational purposes as part of CSE 360 coursework at Arizona State University.

🔗 Related Links

GitHub Repository: https://github.com/CrackParrot11/ASU-CSE360-HW4
Source Code: https://github.com/CrackParrot11/ASU-CSE360-HW4/tree/Dev/FoundationCode
Javadoc: https://github.com/CrackParrot11/ASU-CSE360-HW4/tree/Dev/FoundationCode/doc


❓ Troubleshooting
Common Issues
Problem: JavaFX classes not found

Solution: Ensure JavaFX SDK is added to build path and VM arguments are set correctly

Problem: H2 Database error

Solution: Verify h2-2.2.220.jar is in classpath. Delete ~/FoundationDatabase.mv.db to reset database

Problem: JUnit tests fail

Solution: Ensure JUnit 5 is in build path. Clean and rebuild project

Problem: Cannot access Staff Dashboard

Solution: Ensure user role is set to "Staff" in database. Log out and log back in

Problem: Module errors

Solution: Update VM arguments with correct JavaFX module path


📞 Support
For issues or questions:

Check the Javadoc documentation
Review this README
Check course discussion board
Contact course instructor or TA


Last Updated: November 2025
Version: 1.0.0 (HW4 Staff Role Implementation)
