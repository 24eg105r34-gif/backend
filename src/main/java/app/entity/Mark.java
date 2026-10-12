package app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "marks")
public class Mark {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_type", nullable = false, length = 30)
    private AssessmentType assessmentType;

    @Column(name = "max_marks", nullable = false)
    private Double maxMarks;

    @Column(name = "obtained_marks", nullable = false)
    private Double obtainedMarks;

    @Column(nullable = false)
    private Double percentage;

    @Column(length = 255)
    private String remarks;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum AssessmentType {
        MIDTERM,
        ASSIGNMENT,
        PRACTICAL,
        INTERNAL,
        SEMESTER
    }

    public Mark() {}

    public Mark(Long id, Student student, Subject subject, Faculty faculty, AssessmentType assessmentType, Double maxMarks, Double obtainedMarks, Double percentage, String remarks, LocalDateTime createdAt) {
        this.id = id;
        this.student = student;
        this.subject = subject;
        this.faculty = faculty;
        this.assessmentType = assessmentType;
        this.maxMarks = maxMarks;
        this.obtainedMarks = obtainedMarks;
        this.percentage = percentage;
        this.remarks = remarks;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        calculatePercentage();
    }

    public static MarkBuilder builder() {
        return new MarkBuilder();
    }

    public static class MarkBuilder {
        private Long id;
        private Student student;
        private Subject subject;
        private Faculty faculty;
        private AssessmentType assessmentType;
        private Double maxMarks;
        private Double obtainedMarks;
        private Double percentage;
        private String remarks;
        private LocalDateTime createdAt = LocalDateTime.now();

        public MarkBuilder id(Long id) { this.id = id; return this; }
        public MarkBuilder student(Student student) { this.student = student; return this; }
        public MarkBuilder subject(Subject subject) { this.subject = subject; return this; }
        public MarkBuilder faculty(Faculty faculty) { this.faculty = faculty; return this; }
        public MarkBuilder assessmentType(AssessmentType type) { this.assessmentType = type; return this; }
        public MarkBuilder maxMarks(Double maxMarks) { this.maxMarks = maxMarks; return this; }
        public MarkBuilder obtainedMarks(Double obtainedMarks) { this.obtainedMarks = obtainedMarks; return this; }
        public MarkBuilder percentage(Double percentage) { this.percentage = percentage; return this; }
        public MarkBuilder remarks(String remarks) { this.remarks = remarks; return this; }
        public MarkBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Mark build() {
            return new Mark(id, student, subject, faculty, assessmentType, maxMarks, obtainedMarks, percentage, remarks, createdAt);
        }
    }

    public void calculatePercentage() {
        if (maxMarks != null && maxMarks > 0 && obtainedMarks != null) {
            this.percentage = Math.round((obtainedMarks / maxMarks) * 10000.0) / 100.0;
        } else if (this.percentage == null) {
            this.percentage = 0.0;
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

    public AssessmentType getAssessmentType() { return assessmentType; }
    public void setAssessmentType(AssessmentType assessmentType) { this.assessmentType = assessmentType; }

    public Double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(Double maxMarks) { this.maxMarks = maxMarks; calculatePercentage(); }

    public Double getObtainedMarks() { return obtainedMarks; }
    public void setObtainedMarks(Double obtainedMarks) { this.obtainedMarks = obtainedMarks; calculatePercentage(); }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    @PreUpdate
    public void onSave() {
        calculatePercentage();
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
