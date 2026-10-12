package app.repository;

import app.entity.Mark;
import app.entity.Student;
import app.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarkRepository extends JpaRepository<Mark, Long> {
    List<Mark> findByStudent(Student student);
    List<Mark> findBySubject(Subject subject);
    List<Mark> findByStudentAndSubject(Student student, Subject subject);
    Optional<Mark> findByStudentIdAndSubjectIdAndAssessmentType(Long studentId, Long subjectId, Mark.AssessmentType assessmentType);
}
