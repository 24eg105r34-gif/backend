package app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_submissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"assignment_id", "student_id"})
})
public class AssignmentSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "submission_text", columnDefinition = "TEXT")
    private String submissionText;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "submission_date", nullable = false)
    private LocalDateTime submissionDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SubmissionStatus status;

    @Column(name = "marks_obtained")
    private Double marksObtained;

    @Column(columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    public enum SubmissionStatus {
        PENDING,
        SUBMITTED,
        LATE,
        REVIEWED
    }

    public AssignmentSubmission() {}

    public AssignmentSubmission(Long id, Assignment assignment, Student student, String submissionText, String filePath, LocalDateTime submissionDate, SubmissionStatus status, Double marksObtained, String feedback, LocalDateTime reviewedAt) {
        this.id = id;
        this.assignment = assignment;
        this.student = student;
        this.submissionText = submissionText;
        this.filePath = filePath;
        this.submissionDate = submissionDate != null ? submissionDate : LocalDateTime.now();
        this.status = status;
        this.marksObtained = marksObtained;
        this.feedback = feedback;
        this.reviewedAt = reviewedAt;
    }

    public static AssignmentSubmissionBuilder builder() {
        return new AssignmentSubmissionBuilder();
    }

    public static class AssignmentSubmissionBuilder {
        private Long id;
        private Assignment assignment;
        private Student student;
        private String submissionText;
        private String filePath;
        private LocalDateTime submissionDate = LocalDateTime.now();
        private SubmissionStatus status;
        private Double marksObtained;
        private String feedback;
        private LocalDateTime reviewedAt;

        public AssignmentSubmissionBuilder id(Long id) { this.id = id; return this; }
        public AssignmentSubmissionBuilder assignment(Assignment assignment) { this.assignment = assignment; return this; }
        public AssignmentSubmissionBuilder student(Student student) { this.student = student; return this; }
        public AssignmentSubmissionBuilder submissionText(String text) { this.submissionText = text; return this; }
        public AssignmentSubmissionBuilder filePath(String path) { this.filePath = path; return this; }
        public AssignmentSubmissionBuilder submissionDate(LocalDateTime date) { this.submissionDate = date; return this; }
        public AssignmentSubmissionBuilder status(SubmissionStatus status) { this.status = status; return this; }
        public AssignmentSubmissionBuilder marksObtained(Double marks) { this.marksObtained = marks; return this; }
        public AssignmentSubmissionBuilder feedback(String feedback) { this.feedback = feedback; return this; }
        public AssignmentSubmissionBuilder reviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; return this; }

        public AssignmentSubmission build() {
            return new AssignmentSubmission(id, assignment, student, submissionText, filePath, submissionDate, status, marksObtained, feedback, reviewedAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getSubmissionText() { return submissionText; }
    public void setSubmissionText(String submissionText) { this.submissionText = submissionText; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDateTime submissionDate) { this.submissionDate = submissionDate; }

    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { this.status = status; }

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }

    @PrePersist
    protected void onCreate() {
        if (submissionDate == null) {
            submissionDate = LocalDateTime.now();
        }
    }
}
