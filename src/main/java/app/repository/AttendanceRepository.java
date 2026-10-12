package app.repository;

import app.entity.Attendance;
import app.entity.Student;
import app.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByStudent(Student student);
    List<Attendance> findByStudentAndSubject(Student student, Subject subject);
    List<Attendance> findBySubjectAndAttendanceDate(Subject subject, LocalDate attendanceDate);
    Optional<Attendance> findByStudentIdAndSubjectIdAndAttendanceDate(Long studentId, Long subjectId, LocalDate attendanceDate);

    long countByStudentAndSubject(Student student, Subject subject);
    long countByStudentAndSubjectAndStatus(Student student, Subject subject, Attendance.AttendanceStatus status);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student")
    long countTotalClassesForStudent(@Param("student") Student student);

    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.student = :student AND a.status = 'PRESENT'")
    long countPresentClassesForStudent(@Param("student") Student student);
}
