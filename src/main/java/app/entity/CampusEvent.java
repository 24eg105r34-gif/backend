package app.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "events")
public class CampusEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(nullable = false, length = 100)
    private String organizer;

    @Column(nullable = false)
    private Integer capacity;

    @Column(name = "registration_deadline", nullable = false)
    private LocalDate registrationDeadline;

    @Column(name = "registered_count", nullable = false)
    private Integer registeredCount = 0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public CampusEvent() {}

    public CampusEvent(Long id, String title, String description, LocalDate eventDate, LocalTime startTime, LocalTime endTime, String location, String organizer, Integer capacity, LocalDate registrationDeadline, Integer registeredCount, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.organizer = organizer;
        this.capacity = capacity;
        this.registrationDeadline = registrationDeadline;
        this.registeredCount = registeredCount != null ? registeredCount : 0;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static CampusEventBuilder builder() {
        return new CampusEventBuilder();
    }

    public static class CampusEventBuilder {
        private Long id;
        private String title;
        private String description;
        private LocalDate eventDate;
        private LocalTime startTime;
        private LocalTime endTime;
        private String location;
        private String organizer;
        private Integer capacity;
        private LocalDate registrationDeadline;
        private Integer registeredCount = 0;
        private LocalDateTime createdAt = LocalDateTime.now();

        public CampusEventBuilder id(Long id) { this.id = id; return this; }
        public CampusEventBuilder title(String title) { this.title = title; return this; }
        public CampusEventBuilder description(String desc) { this.description = desc; return this; }
        public CampusEventBuilder eventDate(LocalDate date) { this.eventDate = date; return this; }
        public CampusEventBuilder startTime(LocalTime time) { this.startTime = time; return this; }
        public CampusEventBuilder endTime(LocalTime time) { this.endTime = time; return this; }
        public CampusEventBuilder location(String loc) { this.location = loc; return this; }
        public CampusEventBuilder organizer(String org) { this.organizer = org; return this; }
        public CampusEventBuilder capacity(Integer cap) { this.capacity = cap; return this; }
        public CampusEventBuilder registrationDeadline(LocalDate dl) { this.registrationDeadline = dl; return this; }
        public CampusEventBuilder registeredCount(Integer rc) { this.registeredCount = rc; return this; }
        public CampusEventBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public CampusEvent build() {
            return new CampusEvent(id, title, description, eventDate, startTime, endTime, location, organizer, capacity, registrationDeadline, registeredCount, createdAt);
        }
    }

    public int getRemainingSeats() {
        return Math.max(0, capacity - (registeredCount != null ? registeredCount : 0));
    }

    public boolean isFull() {
        return getRemainingSeats() <= 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getOrganizer() { return organizer; }
    public void setOrganizer(String organizer) { this.organizer = organizer; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public LocalDate getRegistrationDeadline() { return registrationDeadline; }
    public void setRegistrationDeadline(LocalDate registrationDeadline) { this.registrationDeadline = registrationDeadline; }

    public Integer getRegisteredCount() { return registeredCount; }
    public void setRegisteredCount(Integer registeredCount) { this.registeredCount = registeredCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        if (registeredCount == null) {
            registeredCount = 0;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
