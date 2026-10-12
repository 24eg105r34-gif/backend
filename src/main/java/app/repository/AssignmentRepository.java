package app.repository;

import app.entity.Assignment;
import app.entity.Faculty;
import app.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByFaculty(Faculty faculty);
    List<Assignment> findBySubject(Subject subject);
    List<Assignment> findBySubjectIn(List<Subject> subjects);
    List<Assignment> findAllByOrderByDeadlineAsc();
}
