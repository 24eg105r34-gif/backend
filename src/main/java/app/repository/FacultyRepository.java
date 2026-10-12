package app.repository;

import app.entity.Department;
import app.entity.Faculty;
import app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
    Optional<Faculty> findByFacultyId(String facultyId);
    Optional<Faculty> findByEmail(String email);
    Optional<Faculty> findByUser(User user);
    List<Faculty> findByDepartment(Department department);

    @Query("SELECT f FROM Faculty f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(f.facultyId) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Faculty> searchFaculty(@Param("keyword") String keyword);
}
