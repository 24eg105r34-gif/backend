package app.service;

import app.entity.Assignment;
import app.entity.AssignmentSubmission;
import app.entity.Faculty;
import app.entity.Notification;
import app.entity.Student;
import app.entity.Subject;
import app.repository.AssignmentRepository;
import app.repository.AssignmentSubmissionRepository;
import app.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final SubjectRepository subjectRepository;
    private final NotificationService notificationService;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             AssignmentSubmissionRepository submissionRepository,
                             SubjectRepository subjectRepository,
                             NotificationService notificationService) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.subjectRepository = subjectRepository;
        this.notificationService = notificationService;
    }

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAllByOrderByDeadlineAsc();
    }

    public List<Assignment> getAssignmentsForFaculty(Faculty faculty) {
        return assignmentRepository.findByFaculty(faculty);
    }

    public List<Assignment> getAssignmentsForStudent(Student student) {
        List<Subject> subjects = subjectRepository.findByDepartmentAndSemester(student.getDepartment(), student.getSemester());
        if (subjects.isEmpty()) {
            return assignmentRepository.findAllByOrderByDeadlineAsc();
        }
        return assignmentRepository.findBySubjectIn(subjects);
    }

    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    public Assignment createAssignment(Assignment assignment) {
        if (assignment.getDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Assignment deadline cannot be in the past.");
        }
        Assignment saved = assignmentRepository.save(assignment);

        // Notify enrolled students
        notificationService.notifySubjectStudents(
                saved.getSubject(),
                "New Assignment: " + saved.getTitle(),
                "Posted for " + saved.getSubject().getSubjectName() + " by " + saved.getFaculty().getName() + ". Deadline: " + saved.getDeadline().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")),
                Notification.NotificationType.ASSIGNMENT,
                "/student/assignments"
        );

        return saved;
    }

    public Optional<AssignmentSubmission> getSubmission(Assignment assignment, Student student) {
        return submissionRepository.findByAssignmentAndStudent(assignment, student);
    }

    public List<AssignmentSubmission> getSubmissionsForAssignment(Assignment assignment) {
        return submissionRepository.findByAssignment(assignment);
    }

    public AssignmentSubmission submitAssignment(Long assignmentId, Student student, String submissionText, String filePath) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        LocalDateTime now = LocalDateTime.now();
        AssignmentSubmission.SubmissionStatus status = now.isAfter(assignment.getDeadline())
                ? AssignmentSubmission.SubmissionStatus.LATE
                : AssignmentSubmission.SubmissionStatus.SUBMITTED;

        Optional<AssignmentSubmission> existingOpt = submissionRepository.findByAssignmentAndStudent(assignment, student);
        AssignmentSubmission submission;
        if (existingOpt.isPresent()) {
            submission = existingOpt.get();
            submission.setSubmissionText(submissionText);
            if (filePath != null && !filePath.isEmpty()) {
                submission.setFilePath(filePath);
            }
            submission.setSubmissionDate(now);
            submission.setStatus(status);
        } else {
            submission = AssignmentSubmission.builder()
                    .assignment(assignment)
                    .student(student)
                    .submissionText(submissionText)
                    .filePath(filePath)
                    .submissionDate(now)
                    .status(status)
                    .build();
        }

        AssignmentSubmission saved = submissionRepository.save(submission);

        // Notify faculty of submission
        if (assignment.getFaculty() != null && assignment.getFaculty().getUser() != null) {
            notificationService.sendNotification(
                    assignment.getFaculty().getUser(),
                    "New Submission: " + assignment.getTitle(),
                    student.getName() + " (" + student.getStudentId() + ") has submitted coursework (" + status + ").",
                    Notification.NotificationType.ASSIGNMENT,
                    "/faculty/assignments/" + assignment.getId() + "/submissions"
            );
        }

        // Notify student of confirmation
        if (student.getUser() != null) {
            notificationService.sendNotification(
                    student.getUser(),
                    "Submission Received: " + assignment.getTitle(),
                    "Your work for " + assignment.getSubject().getSubjectName() + " has been received successfully (" + status + ").",
                    Notification.NotificationType.ASSIGNMENT,
                    "/student/assignments"
            );
        }

        return saved;
    }

    public AssignmentSubmission gradeSubmission(Long submissionId, Double marksObtained, String feedback) {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new IllegalArgumentException("Submission not found: " + submissionId));

        Double maxMarks = submission.getAssignment().getMaxMarks();
        if (marksObtained != null && (marksObtained < 0 || marksObtained > maxMarks)) {
            throw new IllegalArgumentException("Marks must be between 0 and " + maxMarks);
        }

        submission.setMarksObtained(marksObtained);
        submission.setFeedback(feedback);
        submission.setStatus(AssignmentSubmission.SubmissionStatus.REVIEWED);
        submission.setReviewedAt(LocalDateTime.now());
        AssignmentSubmission saved = submissionRepository.save(submission);

        // Notify student of grade
        Student student = saved.getStudent();
        if (student != null && student.getUser() != null) {
            String fb = (feedback != null && !feedback.trim().isEmpty()) ? " Feedback: " + feedback.trim() : "";
            notificationService.sendNotification(
                    student.getUser(),
                    "Assignment Graded: " + saved.getAssignment().getTitle(),
                    "Score: " + marksObtained + " / " + maxMarks + "." + fb,
                    Notification.NotificationType.ASSIGNMENT,
                    "/student/assignments"
            );
        }

        return saved;
    }

    public long countPendingSubmissions(Faculty faculty) {
        List<Assignment> assignments = assignmentRepository.findByFaculty(faculty);
        long pending = 0;
        for (Assignment a : assignments) {
            pending += submissionRepository.countByAssignmentAndStatus(a, AssignmentSubmission.SubmissionStatus.SUBMITTED);
        }
        return pending;
    }
}
