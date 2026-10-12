package app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "department_code", unique = true, nullable = false, length = 20)
    private String departmentCode;

    @Column(name = "department_name", nullable = false, length = 150)
    private String departmentName;

    @Column(name = "hod_name", length = 100)
    private String hodName;

    public Department() {}

    public Department(Long id, String departmentCode, String departmentName, String hodName) {
        this.id = id;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.hodName = hodName;
    }

    public static DepartmentBuilder builder() {
        return new DepartmentBuilder();
    }

    public static class DepartmentBuilder {
        private Long id;
        private String departmentCode;
        private String departmentName;
        private String hodName;

        public DepartmentBuilder id(Long id) { this.id = id; return this; }
        public DepartmentBuilder departmentCode(String code) { this.departmentCode = code; return this; }
        public DepartmentBuilder departmentName(String name) { this.departmentName = name; return this; }
        public DepartmentBuilder hodName(String hod) { this.hodName = hod; return this; }

        public Department build() {
            return new Department(id, departmentCode, departmentName, hodName);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDepartmentCode() { return departmentCode; }
    public void setDepartmentCode(String departmentCode) { this.departmentCode = departmentCode; }

    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }

    public String getHodName() { return hodName; }
    public void setHodName(String hodName) { this.hodName = hodName; }
}
