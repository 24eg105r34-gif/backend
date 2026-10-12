# NEXUS CAMPUS — Intelligent Campus OS (Backend Service)

> **Enterprise University Operating System powered by Spring Boot 3, Spring Security 6, Hibernate JPA, and H2/PostgreSQL.**

Developed for **Anurag University**  
Architect & Lead Developer: **Peddi Kalyan (24EG105R34)**

---

## 🚀 Key Modules & Architecture

- **Spring Boot 3.3+ & Java 17/21**: Production-grade modular architecture.
- **Spring Security 6**: Role-based access control (`ROLE_ADMIN`, `ROLE_FACULTY`, `ROLE_STUDENT`) with BCrypt encryption.
- **Google OAuth Integration**: One-click Google Sign-In with automatic role provisioning and session synchronization via `GoogleAuthController`.
- **Conflict-Free Timetable Engine**: Academic schedule generator with room and faculty clash prevention.
- **75% Attendance Safeguard**: Automated percentage calculations with critical threshold alerts for students.
- **Grievance Redressal System**: Institutional ticketing system with real-time status transitions.
- **Thymeleaf UI Engine**: Responsive server-rendered UI styled with the NEXUS Design System.

---

## 🛠️ Getting Started Locally

### Prerequisites
- Java JDK 17 or higher
- Apache Maven 3.8+

### Running the Application
```bash
# Clone the repository
git clone https://github.com/24eg105r34-gif/backend.git
cd backend

# Run with demo data enabled
mvn spring-boot:run
```

The application will start on **`http://localhost:8081`**.

---

## 🔐 Default Demo Accounts

| Role | Username / Email | Password | Dashboard URL |
| :--- | :--- | :--- | :--- |
| **Administrator** | `24eg105r34@anurag.edu.in` | `admin123` | `/admin/dashboard` |
| **Faculty** | `prof.sharma@campus.edu` | `faculty123` | `/faculty/dashboard` |
| **Student** | `rahul.cse@campus.edu` | `student123` | `/student/dashboard` |

---

## 📦 Containerization & Cloud Deployment

This repository includes turnkey deployment configurations:
- **`Dockerfile`**: Multi-stage lightweight OpenJDK container.
- **`render.yaml`**: One-click deploy blueprint for Render Web Service.
- **`railway.json`**: Native deployment spec for Railway.app.
