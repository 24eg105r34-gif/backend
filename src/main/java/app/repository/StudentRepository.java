package app.repository;

import app.entity.Department;
import app.entity.Student;
import app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);
    Optional<Student> findByEmail(String email);
    Optional<Student> findByUser(User user);
    List<Student> findByDepartment(Department department);
    long countByDepartment(Department department);
    List<Student> findByDepartmentAndSemester(Department department, Integer semester);

    List<Student> findByDepartmentAndYearAndSemesterAndSection(Department department, Integer year, Integer semester, String section);

    @Query("SELECT s FROM Student s WHERE " +
           "(:deptId IS NULL OR s.department.id = :deptId) AND " +
           "(:year IS NULL OR s.year = :year) AND " +
           "(:semester IS NULL OR s.semester = :semester) AND " +
           "(:section IS NULL OR :section = '' OR s.section = :section)")
    List<Student> filterStudents(@Param("deptId") Long deptId,
                                 @Param("year") Integer year,
                                 @Param("semester") Integer semester,
                                 @Param("section") String section);

    @Query("SELECT s FROM Student s WHERE " +
           "LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.studentId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Student> searchStudents(@Param("keyword") String keyword);
}
