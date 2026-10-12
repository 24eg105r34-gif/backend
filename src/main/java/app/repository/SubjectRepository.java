package app.repository;

import app.entity.Department;
import app.entity.Faculty;
import app.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findBySubjectCode(String subjectCode);
    List<Subject> findByFaculty(Faculty faculty);
    List<Subject> findByDepartment(Department department);
    List<Subject> findByDepartmentAndSemester(Department department, Integer semester);
}
