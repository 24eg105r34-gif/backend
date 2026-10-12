package app.service;

import app.dto.StudentAttendanceSummaryDto;
import app.entity.Attendance;
import app.entity.Faculty;
import app.entity.Notification;
import app.entity.Student;
import app.entity.Subject;
import app.repository.AttendanceRepository;
import app.repository.FacultyRepository;
import app.repository.StudentRepository;
import app.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class AttendanceService {

    @Value("${campus.attendance.warning-threshold:75.0}")
    private double warningThreshold = 75.0;

    private final AttendanceRepository attendanceRepository;
    private final SubjectRepository subjectRepository;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             SubjectRepository subjectRepository,
                             FacultyRepository facultyRepository,
                             StudentRepository studentRepository,
                             NotificationService notificationService) {
        this.attendanceRepository = attendanceRepository;
        this.subjectRepository = subjectRepository;
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.notificationService = notificationService;
    }

    public double getWarningThreshold() {
        return warningThreshold;
    }

    public void setWarningThreshold(double warningThreshold) {
        this.warningThreshold = warningThreshold;
    }


    public void markAttendance(Long subjectId, Long facultyId, LocalDate date, Map<Long, Attendance.AttendanceStatus> studentStatuses) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found: " + subjectId));
        Faculty faculty = facultyRepository.findById(facultyId)
                .orElseThrow(() -> new IllegalArgumentException("Faculty not found: " + facultyId));

        for (Map.Entry<Long, Attendance.AttendanceStatus> entry : studentStatuses.entrySet()) {
            Long studentId = entry.getKey();
            Attendance.AttendanceStatus status = entry.getValue();

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new IllegalArgumentException("Student not found: " + studentId));

            Optional<Attendance> existingOpt = attendanceRepository.findByStudentIdAndSubjectIdAndAttendanceDate(studentId, subjectId, date);
            Attendance attendance;
            if (existingOpt.isPresent()) {
                attendance = existingOpt.get();
                attendance.setStatus(status);
                attendance.setFaculty(faculty);
            } else {
                attendance = Attendance.builder()
                        .student(student)
                        .subject(subject)
                        .faculty(faculty)
                        .attendanceDate(date)
                        .status(status)
                        .build();
            }
            attendanceRepository.save(attendance);

            // Notify absent students and alert on attendance shortage (< threshold)
            if (status == Attendance.AttendanceStatus.ABSENT && student.getUser() != null) {
                double overall = getOverallAttendancePercentage(student);
                if (overall < warningThreshold) {
                    notificationService.sendNotification(
                            student.getUser(),
                            "CRITICAL: Attendance Shortage Alert (" + overall + "%)",
                            "Marked ABSENT in " + subject.getSubjectName() + " on " + date + ". Your overall attendance has dropped to " + overall + "% (Required: " + warningThreshold + "%).",
                            Notification.NotificationType.ATTENDANCE_WARNING,
                            "/student/attendance"
                    );
                } else {
                    notificationService.sendNotification(
                            student.getUser(),
                            "Attendance Alert: Marked Absent",
                            "You were marked ABSENT for " + subject.getSubjectName() + " on " + date + ".",
                            Notification.NotificationType.ATTENDANCE_WARNING,
                            "/student/attendance"
                    );
                }
            }
        }
    }

    public List<Attendance> getAttendanceByStudent(Student student) {
        return attendanceRepository.findByStudent(student);
    }

    public List<Attendance> getAttendanceForSubjectAndDate(Subject subject, LocalDate date) {
        return attendanceRepository.findBySubjectAndAttendanceDate(subject, date);
    }

    public List<StudentAttendanceSummaryDto> getSummaryForStudent(Student student) {
        List<Attendance> allStudentAttendance = attendanceRepository.findByStudent(student);
        Map<Subject, List<Attendance>> subjectGroup = new HashMap<>();

        for (Attendance a : allStudentAttendance) {
            subjectGroup.computeIfAbsent(a.getSubject(), k -> new ArrayList<>()).add(a);
        }

        List<StudentAttendanceSummaryDto> summaries = new ArrayList<>();
        for (Map.Entry<Subject, List<Attendance>> entry : subjectGroup.entrySet()) {
            Subject subject = entry.getKey();
            List<Attendance> records = entry.getValue();

            long total = records.size();
            long present = records.stream().filter(r -> r.getStatus() == Attendance.AttendanceStatus.PRESENT).count();
            long absent = total - present;
            double percentage = total > 0 ? (double) Math.round(((double) present / total * 100.0) * 100.0) / 100.0 : 0.0;
            boolean shortageWarning = total > 0 && percentage < warningThreshold;

            summaries.add(StudentAttendanceSummaryDto.builder()
                    .subject(subject)
                    .totalClasses(total)
                    .presentClasses(present)
                    .absentClasses(absent)
                    .percentage(percentage)
                    .shortageWarning(shortageWarning)
                    .build());
        }

        summaries.sort(Comparator.comparing(s -> s.getSubject().getSubjectCode()));
        return summaries;
    }

    public double getOverallAttendancePercentage(Student student) {
        long total = attendanceRepository.countTotalClassesForStudent(student);
        if (total == 0) return 100.0;
        long present = attendanceRepository.countPresentClassesForStudent(student);
        return Math.round(((double) present / total * 100.0) * 100.0) / 100.0;
    }

    public boolean isOverallShortage(Student student) {
        long total = attendanceRepository.countTotalClassesForStudent(student);
        if (total == 0) return false;
        return getOverallAttendancePercentage(student) < warningThreshold;
    }
}
