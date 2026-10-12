package app.repository;

import app.entity.Assignment;
import app.entity.AssignmentSubmission;
import app.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {
    List<AssignmentSubmission> findByAssignment(Assignment assignment);
    List<AssignmentSubmission> findByStudent(Student student);
    Optional<AssignmentSubmission> findByAssignmentAndStudent(Assignment assignment, Student student);
    long countByAssignment(Assignment assignment);
    long countByAssignmentAndStatus(Assignment assignment, AssignmentSubmission.SubmissionStatus status);
}
