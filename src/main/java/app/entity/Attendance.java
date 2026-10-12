package app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "subject_id", "attendance_date"})
})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    @Column(length = 255)
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum AttendanceStatus {
        PRESENT,
        ABSENT
    }

    public Attendance() {}

    public Attendance(Long id, Student student, Subject subject, Faculty faculty, LocalDate attendanceDate, AttendanceStatus status, String remarks, LocalDateTime createdAt) {
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.faculty = faculty;
        this.attendanceDate = attendanceDate;
        this.status = status;
        this.remarks = remarks;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static AttendanceBuilder builder() {
        return new AttendanceBuilder();
    }

    public static class AttendanceBuilder {
        private Long id;
        private Student student;
        private Subject subject;
        private Faculty faculty;
        private LocalDate attendanceDate;
        private AttendanceStatus status;
        private String remarks;
        private LocalDateTime createdAt = LocalDateTime.now();

        public AttendanceBuilder id(Long id) { this.id = id; return this; }
        public AttendanceBuilder student(Student student) { this.student = student; return this; }
        public AttendanceBuilder subject(Subject subject) { this.subject = subject; return this; }
        public AttendanceBuilder faculty(Faculty faculty) { this.faculty = faculty; return this; }
        public AttendanceBuilder attendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; return this; }
        public AttendanceBuilder status(AttendanceStatus status) { this.status = status; return this; }
        public AttendanceBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public AttendanceBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Attendance build() {
            return new Attendance(id, student, subject, faculty, attendanceDate, status, remarks, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
