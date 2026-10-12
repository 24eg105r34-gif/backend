package app.service;

import app.entity.Complaint;
import app.entity.Notification;
import app.entity.Student;
import app.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final NotificationService notificationService;

    public ComplaintService(ComplaintRepository complaintRepository,
                            NotificationService notificationService) {
        this.complaintRepository = complaintRepository;
        this.notificationService = notificationService;
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Complaint> getComplaintsByStudent(Student student) {
        return complaintRepository.findByStudentOrderByCreatedAtDesc(student);
    }

    public List<Complaint> getComplaintsByStatus(Complaint.ComplaintStatus status) {
        return complaintRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    public List<Complaint> filterComplaints(Complaint.ComplaintStatus status, Complaint.ComplaintCategory category) {
        if (status != null && category != null) {
            return complaintRepository.findByStatusAndCategoryOrderByCreatedAtDesc(status, category);
        } else if (status != null) {
            return complaintRepository.findByStatusOrderByCreatedAtDesc(status);
        } else if (category != null) {
            return complaintRepository.findByCategoryOrderByCreatedAtDesc(category);
        }
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }

    public Complaint raiseComplaint(Student student, Complaint.ComplaintCategory category, String subject, String description) {
        Complaint complaint = Complaint.builder()
                .student(student)
                .category(category)
                .subject(subject)
                .description(description)
                .status(Complaint.ComplaintStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
        Complaint saved = complaintRepository.save(complaint);

        // 1. Notify Student confirmation
        if (student.getUser() != null) {
            notificationService.sendNotification(
                    student.getUser(),
                    "Grievance Ticket Created (#TKT-" + saved.getId() + ")",
                    "Your grievance regarding '" + saved.getSubject() + "' has been assigned to administration for review.",
                    Notification.NotificationType.COMPLAINT,
                    "/student/complaints"
            );
        }

        // 2. Notify Admins
        notificationService.notifyAdmins(
                "New Grievance Ticket (#TKT-" + saved.getId() + ")",
                student.getName() + " (" + student.getStudentId() + ") reported an issue under " + category + ": " + subject,
                Notification.NotificationType.COMPLAINT,
                "/admin/complaints"
        );

        return saved;
    }

    public Complaint updateComplaintStatus(Long complaintId, Complaint.ComplaintStatus status, String adminResponse) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found: " + complaintId));

        complaint.setStatus(status);
        if (adminResponse != null && !adminResponse.trim().isEmpty()) {
            complaint.setAdminResponse(adminResponse.trim());
        }
        if (status == Complaint.ComplaintStatus.RESOLVED || status == Complaint.ComplaintStatus.REJECTED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }
        Complaint saved = complaintRepository.save(complaint);

        // Notify Student of resolution / update
        if (saved.getStudent() != null && saved.getStudent().getUser() != null) {
            String remarks = (adminResponse != null && !adminResponse.trim().isEmpty())
                    ? " Remarks: " + adminResponse.trim()
                    : "";
            notificationService.sendNotification(
                    saved.getStudent().getUser(),
                    "Grievance Update (#TKT-" + saved.getId() + "): " + status,
                    "Ticket status updated to " + status + "." + remarks,
                    Notification.NotificationType.COMPLAINT,
                    "/student/complaints"
            );
        }

        return saved;
    }

    public void cancelComplaint(Long complaintId, Student student) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found: " + complaintId));

        if (!complaint.getStudent().getId().equals(student.getId())) {
            throw new IllegalStateException("Unauthorized to cancel this ticket.");
        }

        if (complaint.getStatus() != Complaint.ComplaintStatus.PENDING) {
            throw new IllegalStateException("Only pending grievances can be withdrawn.");
        }

        complaintRepository.delete(complaint);

        if (student.getUser() != null) {
            notificationService.sendNotification(
                    student.getUser(),
                    "Grievance Withdrawn (#TKT-" + complaintId + ")",
                    "Your grievance ticket #" + complaintId + " has been withdrawn.",
                    Notification.NotificationType.COMPLAINT,
                    "/student/complaints"
            );
        }
    }

    public long countPendingComplaints() {
        return complaintRepository.countByStatus(Complaint.ComplaintStatus.PENDING);
    }
}
