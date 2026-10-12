package app.service;

import app.entity.Faculty;
import app.entity.Mark;
import app.entity.Notification;
import app.entity.Student;
import app.entity.Subject;
import app.repository.MarkRepository;
import app.repository.StudentRepository;
import app.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MarkService {

    private final MarkRepository markRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final NotificationService notificationService;

    public MarkService(MarkRepository markRepository,
                       StudentRepository studentRepository,
                       SubjectRepository subjectRepository,
                       NotificationService notificationService) {
        this.markRepository = markRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.notificationService = notificationService;
    }

    public Mark saveMark(Long studentId, Long subjectId, Faculty faculty, Mark.AssessmentType assessmentType, Double maxMarks, Double obtainedMarks, String remarks) {
        if (maxMarks == null || maxMarks <= 0) {
            throw new IllegalArgumentException("Maximum marks must be greater than zero.");
        }
        if (obtainedMarks == null || obtainedMarks < 0) {
            throw new IllegalArgumentException("Obtained marks cannot be negative.");
        }
        if (obtainedMarks > maxMarks) {
            throw new IllegalArgumentException("Obtained marks (" + obtainedMarks + ") cannot exceed maximum marks (" + maxMarks + ").");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + subjectId));

        Optional<Mark> existingOpt = markRepository.findByStudentIdAndSubjectIdAndAssessmentType(studentId, subjectId, assessmentType);
        Mark mark;
        if (existingOpt.isPresent()) {
            mark = existingOpt.get();
            mark.setMaxMarks(maxMarks);
            mark.setObtainedMarks(obtainedMarks);
            mark.setFaculty(faculty);
            mark.setRemarks(remarks);
        } else {
            mark = Mark.builder()
                    .student(student)
                    .subject(subject)
                    .faculty(faculty)
                    .assessmentType(assessmentType)
                    .maxMarks(maxMarks)
                    .obtainedMarks(obtainedMarks)
                    .remarks(remarks)
                    .build();
        }
        mark.calculatePercentage();
        Mark saved = markRepository.save(mark);

        // Notify student about new marks
        if (student.getUser() != null) {
            String note = (remarks != null && !remarks.trim().isEmpty()) ? " Note: " + remarks.trim() : "";
            notificationService.sendNotification(
                    student.getUser(),
                    "Marks Published: " + subject.getSubjectName() + " (" + assessmentType + ")",
                    "Score: " + obtainedMarks + " / " + maxMarks + " (" + saved.getPercentage() + "%)." + note,
                    Notification.NotificationType.ACADEMIC,
                    "/student/marks"
            );
        }

        return saved;
    }

    public List<Mark> getMarksByStudent(Student student) {
        return markRepository.findByStudent(student);
    }

    public List<Mark> getMarksBySubject(Subject subject) {
        return markRepository.findBySubject(subject);
    }

    public double getAveragePercentageForStudent(Student student) {
        List<Mark> marks = markRepository.findByStudent(student);
        if (marks.isEmpty()) return 0.0;
        double sum = marks.stream().mapToDouble(Mark::getPercentage).sum();
        return Math.round((sum / marks.size()) * 100.0) / 100.0;
    }
}
