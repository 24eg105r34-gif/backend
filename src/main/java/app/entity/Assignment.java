package app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assignments")
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(name = "max_marks", nullable = false)
    private Double maxMarks;

    @Column(name = "supporting_material_path")
    private String supportingMaterialPath;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Assignment() {}

    public Assignment(Long id, String title, String description, Subject subject, Faculty faculty, LocalDateTime deadline, Double maxMarks, String supportingMaterialPath, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.faculty = faculty;
        this.deadline = deadline;
        this.maxMarks = maxMarks;
        this.supportingMaterialPath = supportingMaterialPath;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static AssignmentBuilder builder() {
        return new AssignmentBuilder();
    }

    public static class AssignmentBuilder {
        private Long id;
        private String title;
        private String description;
        private Subject subject;
        private Faculty faculty;
        private LocalDateTime deadline;
        private Double maxMarks;
        private String supportingMaterialPath;
        private LocalDateTime createdAt = LocalDateTime.now();

        public AssignmentBuilder id(Long id) { this.id = id; return this; }
        public AssignmentBuilder title(String title) { this.title = title; return this; }
        public AssignmentBuilder description(String description) { this.description = description; return this; }
        public AssignmentBuilder subject(Subject subject) { this.subject = subject; return this; }
        public AssignmentBuilder faculty(Faculty faculty) { this.faculty = faculty; return this; }
        public AssignmentBuilder deadline(LocalDateTime deadline) { this.deadline = deadline; return this; }
        public AssignmentBuilder maxMarks(Double maxMarks) { this.maxMarks = maxMarks; return this; }
        public AssignmentBuilder supportingMaterialPath(String path) { this.supportingMaterialPath = path; return this; }
        public AssignmentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Assignment build() {
            return new Assignment(id, title, description, subject, faculty, deadline, maxMarks, supportingMaterialPath, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }

    public Double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(Double maxMarks) { this.maxMarks = maxMarks; }

    public String getSupportingMaterialPath() { return supportingMaterialPath; }
    public void setSupportingMaterialPath(String supportingMaterialPath) { this.supportingMaterialPath = supportingMaterialPath; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
