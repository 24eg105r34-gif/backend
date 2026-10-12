package app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "complaints")
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ComplaintCategory category;

    @Column(nullable = false, length = 150)
    private String subject;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplaintStatus status = ComplaintStatus.PENDING;

    @Column(name = "admin_response", columnDefinition = "TEXT")
    private String adminResponse;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public enum ComplaintCategory {
        Infrastructure,
        Classroom,
        Laboratory,
        Library,
        Internet,
        Transport,
        Hostel,
        Other
    }

    public enum ComplaintStatus {
        PENDING,
        IN_PROGRESS,
        RESOLVED,
        REJECTED
    }

    public Complaint() {}

    public Complaint(Long id, Student student, ComplaintCategory category, String subject, String description, ComplaintStatus status, String adminResponse, LocalDateTime createdAt, LocalDateTime resolvedAt) {
        this.id = id;
        this.student = student;
        this.category = category;
        this.subject = subject;
        this.description = description;
        this.status = status != null ? status : ComplaintStatus.PENDING;
        this.adminResponse = adminResponse;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.resolvedAt = resolvedAt;
    }

    public static ComplaintBuilder builder() {
        return new ComplaintBuilder();
    }

    public static class ComplaintBuilder {
        private Long id;
        private Student student;
        private ComplaintCategory category;
        private String subject;
        private String description;
        private ComplaintStatus status = ComplaintStatus.PENDING;
        private String adminResponse;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime resolvedAt;

        public ComplaintBuilder id(Long id) { this.id = id; return this; }
        public ComplaintBuilder student(Student student) { this.student = student; return this; }
        public ComplaintBuilder category(ComplaintCategory category) { this.category = category; return this; }
        public ComplaintBuilder subject(String subject) { this.subject = subject; return this; }
        public ComplaintBuilder description(String desc) { this.description = desc; return this; }
        public ComplaintBuilder status(ComplaintStatus status) { this.status = status; return this; }
        public ComplaintBuilder adminResponse(String resp) { this.adminResponse = resp; return this; }
        public ComplaintBuilder createdAt(LocalDateTime date) { this.createdAt = date; return this; }
        public ComplaintBuilder resolvedAt(LocalDateTime date) { this.resolvedAt = date; return this; }

        public Complaint build() {
            return new Complaint(id, student, category, subject, description, status, adminResponse, createdAt, resolvedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public String getAdminResponse() { return adminResponse; }
    public void setAdminResponse(String adminResponse) { this.adminResponse = adminResponse; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    @PrePersist
    protected void onCreate() {
        if (status == null) {
            status = ComplaintStatus.PENDING;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
