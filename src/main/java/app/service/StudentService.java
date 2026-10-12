package app.service;

import app.entity.Department;
import app.entity.Role;
import app.entity.Student;
import app.entity.User;
import app.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserService userService;

    public StudentService(StudentRepository studentRepository, UserService userService) {
        this.studentRepository = studentRepository;
        this.userService = userService;
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Optional<Student> getStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    public Optional<Student> getStudentByStudentId(String studentId) {
        return studentRepository.findByStudentId(studentId);
    }

    public Optional<Student> getStudentByUser(User user) {
        return studentRepository.findByUser(user);
    }

    public List<Student> getStudentsByDepartment(Department department) {
        return studentRepository.findByDepartment(department);
    }

    public List<Student> getStudentsByDepartmentAndSemester(Department department, Integer semester) {
        if (department == null || semester == null) {
            return studentRepository.findAll();
        }
        return studentRepository.findByDepartmentAndSemester(department, semester);
    }

    public List<Student> filterStudents(Long deptId, Integer year, Integer semester, String section) {
        return studentRepository.filterStudents(deptId, year, semester, section);
    }

    public List<Student> searchStudents(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return studentRepository.findAll();
        }
        return studentRepository.searchStudents(keyword.trim());
    }

    public Student createStudent(Student student, String rawPassword) {
        if (student.getUser() == null && !userService.existsByUsername(student.getEmail())) {
            User user = userService.createUser(student.getEmail(), rawPassword != null ? rawPassword : "student123", Role.ROLE_STUDENT, student.getName());
            student.setUser(user);
        }
        return studentRepository.save(student);
    }

    public Student updateStudent(Student student) {
        return studentRepository.save(student);
    }

    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }

    public long countStudents() {
        return studentRepository.count();
    }

    public long countStudentsByDepartment(Department department) {
        return studentRepository.countByDepartment(department);
    }
}
