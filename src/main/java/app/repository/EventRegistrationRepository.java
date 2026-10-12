package app.repository;

import app.entity.CampusEvent;
import app.entity.EventRegistration;
import app.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByCampusEvent(CampusEvent campusEvent);
    List<EventRegistration> findByCampusEventOrderByRegistrationDateDesc(CampusEvent campusEvent);
    List<EventRegistration> findByStudent(Student student);
    Optional<EventRegistration> findByCampusEventAndStudent(CampusEvent campusEvent, Student student);
    boolean existsByCampusEventAndStudent(CampusEvent campusEvent, Student student);
    long countByCampusEvent(CampusEvent campusEvent);
}
