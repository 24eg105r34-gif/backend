package app.service;

import app.entity.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final DepartmentService departmentService;
    private final SubjectService subjectService;
    private final ComplaintService complaintService;
    private final CampusEventService eventService;
    private final AttendanceService attendanceService;
    private final TimetableService timetableService;
    private final AssignmentService assignmentService;
    private final NoticeService noticeService;
    private final MarkService markService;

    public DashboardService(StudentService studentService,
                            FacultyService facultyService,
                            DepartmentService departmentService,
                            SubjectService subjectService,
                            ComplaintService complaintService,
                            CampusEventService eventService,
                            AttendanceService attendanceService,
                            TimetableService timetableService,
                            AssignmentService assignmentService,
                            NoticeService noticeService,
                            MarkService markService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.departmentService = departmentService;
        this.subjectService = subjectService;
        this.complaintService = complaintService;
        this.eventService = eventService;
        this.attendanceService = attendanceService;
        this.timetableService = timetableService;
        this.assignmentService = assignmentService;
        this.noticeService = noticeService;
        this.markService = markService;
    }

    public Map<String, Object> getAdminDashboardData() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalStudents", studentService.countStudents());
        data.put("totalFaculty", facultyService.countFaculty());
        data.put("totalDepartments", departmentService.countDepartments());
        data.put("totalSubjects", subjectService.countSubjects());
        data.put("pendingComplaints", complaintService.countPendingComplaints());
        data.put("upcomingEventsCount", eventService.countUpcomingEvents());

        Map<String, Long> studentsByDept = new LinkedHashMap<>();
        for (Department dept : departmentService.getAllDepartments()) {
            studentsByDept.put(dept.getDepartmentCode(), studentService.countStudentsByDepartment(dept));
        }
        data.put("studentsByDept", studentsByDept);

        data.put("recentNotices", noticeService.getRecentNotices());
        data.put("recentComplaints", complaintService.getAllComplaints().stream().limit(5).toList());
        data.put("upcomingEvents", eventService.getTopUpcomingEvents());

        return data;
    }

    public Map<String, Object> getFacultyDashboardData(Faculty faculty) {
        Map<String, Object> data = new HashMap<>();
        List<Subject> assignedSubjects = subjectService.getSubjectsByFaculty(faculty);
        data.put("assignedSubjects", assignedSubjects);
        data.put("totalAssignedSubjects", assignedSubjects.size());

        Timetable.DayOfWeek today = Timetable.DayOfWeek.valueOf(LocalDate.now().getDayOfWeek().name().toUpperCase());
        List<Timetable> facultyTimetable = timetableService.getTimetableForFaculty(faculty);
        List<Timetable> todayClasses = facultyTimetable.stream()
                .filter(t -> t.getDayOfWeek() == today)
                .sorted(Comparator.comparing(Timetable::getStartTime))
                .toList();
        data.put("todayClasses", todayClasses);

        Set<Student> uniqueStudents = new HashSet<>();
        for (Subject s : assignedSubjects) {
            uniqueStudents.addAll(studentService.getStudentsByDepartment(s.getDepartment()));
        }
        data.put("totalStudents", uniqueStudents.size());
        data.put("pendingSubmissions", assignmentService.countPendingSubmissions(faculty));
        data.put("recentNotices", noticeService.getRecentNotices());

        return data;
    }

    public Map<String, Object> getStudentDashboardData(Student student) {
        Map<String, Object> data = new HashMap<>();
        data.put("student", student);

        double attendancePct = attendanceService.getOverallAttendancePercentage(student);
        data.put("attendancePercentage", attendancePct);
        data.put("isAttendanceShortage", attendancePct < 75.0);

        double avgMarksPct = markService.getAveragePercentageForStudent(student);
        data.put("academicAverage", avgMarksPct);

        Timetable.DayOfWeek today = Timetable.DayOfWeek.valueOf(LocalDate.now().getDayOfWeek().name().toUpperCase());
        List<Timetable> studentTimetable = timetableService.getTimetableForStudent(student);
        List<Timetable> todayClasses = studentTimetable.stream()
                .filter(t -> t.getDayOfWeek() == today)
                .sorted(Comparator.comparing(Timetable::getStartTime))
                .toList();
        data.put("todayClasses", todayClasses);

        data.put("pendingAssignments", assignmentService.getAssignmentsForStudent(student).stream().limit(5).toList());
        data.put("upcomingEvents", eventService.getTopUpcomingEvents());
        data.put("recentNotices", noticeService.getRecentNotices());

        return data;
    }
}
