package app.repository;

import app.entity.CampusEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CampusEventRepository extends JpaRepository<CampusEvent, Long> {
    List<CampusEvent> findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate date);
    List<CampusEvent> findAllByOrderByEventDateDesc();
    List<CampusEvent> findTop5ByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate date);
}
