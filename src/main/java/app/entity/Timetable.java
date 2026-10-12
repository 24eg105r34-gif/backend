package app.entity;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity
@Table(name = "timetable")
public class Timetable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false, length = 15)
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;

    @Column(nullable = false, length = 50)
    private String room;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "academic_year", nullable = false)
    private Integer year;

    @Column(length = 10, nullable = false)
    private String section;

    public enum DayOfWeek {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY
    }

    public Timetable() {}

    public Timetable(Long id, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Subject subject, Faculty faculty, String room, Department department, Integer year, String section) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.subject = subject;
        this.faculty = faculty;
        this.room = room;
        this.department = department;
        this.year = year;
        this.section = section;
    }

    public static TimetableBuilder builder() {
        return new TimetableBuilder();
    }

    public static class TimetableBuilder {
        private Long id;
        private DayOfWeek dayOfWeek;
        private LocalTime startTime;
        private LocalTime endTime;
        private Subject subject;
        private Faculty faculty;
        private String room;
        private Department department;
        private Integer year;
        private String section;

        public TimetableBuilder id(Long id) { this.id = id; return this; }
        public TimetableBuilder dayOfWeek(DayOfWeek day) { this.dayOfWeek = day; return this; }
        public TimetableBuilder startTime(LocalTime start) { this.startTime = start; return this; }
        public TimetableBuilder endTime(LocalTime end) { this.endTime = end; return this; }
        public TimetableBuilder subject(Subject subject) { this.subject = subject; return this; }
        public TimetableBuilder faculty(Faculty faculty) { this.faculty = faculty; return this; }
        public TimetableBuilder room(String room) { this.room = room; return this; }
        public TimetableBuilder department(Department department) { this.department = department; return this; }
        public TimetableBuilder year(Integer year) { this.year = year; return this; }
        public TimetableBuilder section(String section) { this.section = section; return this; }

        public Timetable build() {
            return new Timetable(id, dayOfWeek, startTime, endTime, subject, faculty, room, department, year, section);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }

    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
}
