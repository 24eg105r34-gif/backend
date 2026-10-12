package app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "notices")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private NoticeCategory category;

    @Column(name = "published_date", nullable = false)
    private LocalDate publishedDate = LocalDate.now();

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum NoticeCategory {
        Academic,
        Examination,
        General,
        Event,
        Emergency
    }

    public Notice() {}

    public Notice(Long id, String title, String description, NoticeCategory category, LocalDate publishedDate, LocalDate expiryDate, String createdBy, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.publishedDate = publishedDate != null ? publishedDate : LocalDate.now();
        this.expiryDate = expiryDate;
        this.createdBy = createdBy;
        this.active = active;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static NoticeBuilder builder() {
        return new NoticeBuilder();
    }

    public static class NoticeBuilder {
        private Long id;
        private String title;
        private String description;
        private NoticeCategory category;
        private LocalDate publishedDate = LocalDate.now();
        private LocalDate expiryDate;
        private String createdBy;
        private boolean active = true;
        private LocalDateTime createdAt = LocalDateTime.now();

        public NoticeBuilder id(Long id) { this.id = id; return this; }
        public NoticeBuilder title(String title) { this.title = title; return this; }
        public NoticeBuilder description(String desc) { this.description = desc; return this; }
        public NoticeBuilder category(NoticeCategory category) { this.category = category; return this; }
        public NoticeBuilder publishedDate(LocalDate date) { this.publishedDate = date; return this; }
        public NoticeBuilder expiryDate(LocalDate date) { this.expiryDate = date; return this; }
        public NoticeBuilder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public NoticeBuilder active(boolean active) { this.active = active; return this; }
        public NoticeBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Notice build() {
            return new Notice(id, title, description, category, publishedDate, expiryDate, createdBy, active, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public NoticeCategory getCategory() { return category; }
    public void setCategory(NoticeCategory category) { this.category = category; }

    public LocalDate getPublishedDate() { return publishedDate; }
    public void setPublishedDate(LocalDate publishedDate) { this.publishedDate = publishedDate; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        if (publishedDate == null) {
            publishedDate = LocalDate.now();
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
