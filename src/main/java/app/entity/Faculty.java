package app.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "faculty")
public class Faculty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "faculty_id", unique = true, nullable = false, length = 50)
    private String facultyId;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(length = 100)
    private String designation;

    @Column(length = 150)
    private String specialization;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Faculty() {}

    public Faculty(Long id, String facultyId, String name, String email, String phone, Department department, String designation, String specialization, User user, LocalDateTime createdAt) {
        this.id = id;
        this.facultyId = facultyId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.designation = designation;
        this.specialization = specialization;
        this.user = user;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static FacultyBuilder builder() {
        return new FacultyBuilder();
    }

    public static class FacultyBuilder {
        private Long id;
        private String facultyId;
        private String name;
        private String email;
        private String phone;
        private Department department;
        private String designation;
        private String specialization;
        private User user;
        private LocalDateTime createdAt = LocalDateTime.now();

        public FacultyBuilder id(Long id) { this.id = id; return this; }
        public FacultyBuilder facultyId(String facultyId) { this.facultyId = facultyId; return this; }
        public FacultyBuilder name(String name) { this.name = name; return this; }
        public FacultyBuilder email(String email) { this.email = email; return this; }
        public FacultyBuilder phone(String phone) { this.phone = phone; return this; }
        public FacultyBuilder department(Department department) { this.department = department; return this; }
        public FacultyBuilder designation(String designation) { this.designation = designation; return this; }
        public FacultyBuilder specialization(String specialization) { this.specialization = specialization; return this; }
        public FacultyBuilder user(User user) { this.user = user; return this; }
        public FacultyBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Faculty build() {
            return new Faculty(id, facultyId, name, email, phone, department, designation, specialization, user, createdAt);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFacultyId() { return facultyId; }
    public void setFacultyId(String facultyId) { this.facultyId = facultyId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

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
