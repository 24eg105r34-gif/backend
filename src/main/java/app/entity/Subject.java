package app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_code", unique = true, nullable = false, length = 30)
    private String subjectCode;

    @Column(name = "subject_name", nullable = false, length = 150)
    private String subjectName;

    @Column(nullable = false)
    private Integer credits;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(nullable = false)
    private Integer semester;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    public Subject() {}

    public Subject(Long id, String subjectCode, String subjectName, Integer credits, Department department, Integer semester, Faculty faculty) {
        this.id = id;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.credits = credits;
        this.department = department;
        this.semester = semester;
        this.faculty = faculty;
    }

    public static SubjectBuilder builder() {
        return new SubjectBuilder();
    }

    public static class SubjectBuilder {
        private Long id;
        private String subjectCode;
        private String subjectName;
        private Integer credits;
        private Department department;
        private Integer semester;
        private Faculty faculty;

        public SubjectBuilder id(Long id) { this.id = id; return this; }
        public SubjectBuilder subjectCode(String subjectCode) { this.subjectCode = subjectCode; return this; }
        public SubjectBuilder subjectName(String subjectName) { this.subjectName = subjectName; return this; }
        public SubjectBuilder credits(Integer credits) { this.credits = credits; return this; }
        public SubjectBuilder department(Department department) { this.department = department; return this; }
        public SubjectBuilder semester(Integer semester) { this.semester = semester; return this; }
        public SubjectBuilder faculty(Faculty faculty) { this.faculty = faculty; return this; }

        public Subject build() {
            return new Subject(id, subjectCode, subjectName, credits, department, semester, faculty);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }
}
