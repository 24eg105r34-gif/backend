package app.repository;

import app.entity.Complaint;
import app.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudentOrderByCreatedAtDesc(Student student);
    List<Complaint> findByStatusOrderByCreatedAtDesc(Complaint.ComplaintStatus status);
    List<Complaint> findByCategoryOrderByCreatedAtDesc(Complaint.ComplaintCategory category);
    List<Complaint> findByStatusAndCategoryOrderByCreatedAtDesc(Complaint.ComplaintStatus status, Complaint.ComplaintCategory category);
    List<Complaint> findAllByOrderByCreatedAtDesc();
    long countByStatus(Complaint.ComplaintStatus status);
    long countByStudent(Student student);
}
