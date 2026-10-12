package app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "event_registrations", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"event_id", "student_id"})
})
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", nullable = false)
    private CampusEvent campusEvent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate = LocalDateTime.now();

    public EventRegistration() {}

    public EventRegistration(Long id, CampusEvent campusEvent, Student student, LocalDateTime registrationDate) {
        this.id = id;
        this.campusEvent = campusEvent;
        this.student = student;
        this.registrationDate = registrationDate != null ? registrationDate : LocalDateTime.now();
    }

    public static EventRegistrationBuilder builder() {
        return new EventRegistrationBuilder();
    }

    public static class EventRegistrationBuilder {
        private Long id;
        private CampusEvent campusEvent;
        private Student student;
        private LocalDateTime registrationDate = LocalDateTime.now();

        public EventRegistrationBuilder id(Long id) { this.id = id; return this; }
        public EventRegistrationBuilder campusEvent(CampusEvent event) { this.campusEvent = event; return this; }
        public EventRegistrationBuilder student(Student student) { this.student = student; return this; }
        public EventRegistrationBuilder registrationDate(LocalDateTime date) { this.registrationDate = date; return this; }

        public EventRegistration build() {
            return new EventRegistration(id, campusEvent, student, registrationDate);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CampusEvent getCampusEvent() { return campusEvent; }
    public void setCampusEvent(CampusEvent campusEvent) { this.campusEvent = campusEvent; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }

    @PrePersist
    protected void onCreate() {
        if (registrationDate == null) {
            registrationDate = LocalDateTime.now();
        }
    }
}
