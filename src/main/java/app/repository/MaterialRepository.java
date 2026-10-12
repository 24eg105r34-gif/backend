package app.repository;

import app.entity.Faculty;
import app.entity.Material;
import app.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByFaculty(Faculty faculty);
    List<Material> findBySubject(Subject subject);
    List<Material> findBySubjectIn(List<Subject> subjects);
    List<Material> findAllByOrderByUploadDateDesc();
}
