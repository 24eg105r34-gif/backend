package app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", unique = true, nullable = false, length = 50)
    private String studentId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "academic_year", nullable = false)
    private Integer year;

    @Column(nullable = false)
    private Integer semester;

    @Column(length = 10, nullable = false)
    private String section;

    @Column(name = "profile_image", length = 255)
    private String profileImage;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Student() {}

    public Student(Long id, String studentId, String name, String email, String phone, Department department, Integer year, Integer semester, String section, String profileImage, User user, LocalDateTime createdAt) {
        this.id = id;
        this.studentId = studentId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.year = year;
        this.semester = semester;
        this.section = section;
        this.profileImage = profileImage;
        this.user = user;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static StudentBuilder builder() {
        return new StudentBuilder();
    }

    public static class StudentBuilder {
        private Long id;
        private String studentId;
        private String name;
        private String email;
        private String phone;
        private Department department;
        private Integer year;
        private Integer semester;
        private String section;
        private String profileImage;
        private User user;
        private LocalDateTime createdAt = LocalDateTime.now();

        public StudentBuilder id(Long id) { this.id = id; return this; }
        public StudentBuilder studentId(String studentId) { this.studentId = studentId; return this; }
        public StudentBuilder name(String name) { this.name = name; return this; }
        public StudentBuilder email(String email) { this.email = email; return this; }
        public StudentBuilder phone(String phone) { this.phone = phone; return this; }
        public StudentBuilder department(Department department) { this.department = department; return this; }
        public StudentBuilder year(Integer year) { this.year = year; return this; }
        public StudentBuilder semester(Integer semester) { this.semester = semester; return this; }
        public StudentBuilder section(String section) { this.section = section; return this; }
        public StudentBuilder profileImage(String profileImage) { this.profileImage = profileImage; return this; }
        public StudentBuilder user(User user) { this.user = user; return this; }
        public StudentBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Student build() {
            return new Student(id, studentId, name, email, phone, department, year, semester, section, profileImage, user, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
