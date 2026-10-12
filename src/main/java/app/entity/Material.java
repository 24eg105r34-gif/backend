package app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "file_type", length = 50)
    private String fileType;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "upload_date", nullable = false)
    private LocalDateTime uploadDate = LocalDateTime.now();

    public Material() {}

    public Material(Long id, String title, String description, Subject subject, Faculty faculty, String fileName, String filePath, String fileType, Long fileSize, LocalDateTime uploadDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.subject = subject;
        this.faculty = faculty;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.uploadDate = uploadDate != null ? uploadDate : LocalDateTime.now();
    }

    public static MaterialBuilder builder() {
        return new MaterialBuilder();
    }

    public static class MaterialBuilder {
        private Long id;
        private String title;
        private String description;
        private Subject subject;
        private Faculty faculty;
        private String fileName;
        private String filePath;
        private String fileType;
        private Long fileSize;
        private LocalDateTime uploadDate = LocalDateTime.now();

        public MaterialBuilder id(Long id) { this.id = id; return this; }
        public MaterialBuilder title(String title) { this.title = title; return this; }
        public MaterialBuilder description(String description) { this.description = description; return this; }
        public MaterialBuilder subject(Subject subject) { this.subject = subject; return this; }
        public MaterialBuilder faculty(Faculty faculty) { this.faculty = faculty; return this; }
        public MaterialBuilder fileName(String fileName) { this.fileName = fileName; return this; }
        public MaterialBuilder filePath(String filePath) { this.filePath = filePath; return this; }
        public MaterialBuilder fileType(String fileType) { this.fileType = fileType; return this; }
        public MaterialBuilder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
        public MaterialBuilder uploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; return this; }

        public Material build() {
            return new Material(id, title, description, subject, faculty, fileName, filePath, fileType, fileSize, uploadDate);
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

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public LocalDateTime getUploadDate() { return uploadDate; }
    public void setUploadDate(LocalDateTime uploadDate) { this.uploadDate = uploadDate; }

    @PrePersist
    protected void onCreate() {
        if (uploadDate == null) {
            uploadDate = LocalDateTime.now();
        }
    }
}
