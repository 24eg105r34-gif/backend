package app.controller;

import app.entity.*;
import app.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    private final DashboardService dashboardService;
    private final FacultyService facultyService;
    private final SubjectService subjectService;
    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final MarkService markService;
    private final AssignmentService assignmentService;
    private final MaterialService materialService;
    private final NoticeService noticeService;
    private final UserService userService;
    private final NotificationService notificationService;

    public FacultyController(DashboardService dashboardService,
                             FacultyService facultyService,
                             SubjectService subjectService,
                             StudentService studentService,
                             AttendanceService attendanceService,
                             MarkService markService,
                             AssignmentService assignmentService,
                             MaterialService materialService,
                             NoticeService noticeService,
                             UserService userService,
                             NotificationService notificationService) {
        this.dashboardService = dashboardService;
        this.facultyService = facultyService;
        this.subjectService = subjectService;
        this.studentService = studentService;
        this.attendanceService = attendanceService;
        this.markService = markService;
        this.assignmentService = assignmentService;
        this.materialService = materialService;
        this.noticeService = noticeService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    private Faculty getAuthenticatedFaculty(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));
        return facultyService.getFacultyByUser(user)
                .orElseThrow(() -> new IllegalStateException("Faculty profile not found for user: " + user.getUsername()));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Faculty faculty = getAuthenticatedFaculty(authentication);
        model.addAllAttributes(dashboardService.getFacultyDashboardData(faculty));
        model.addAttribute("faculty", faculty);
        model.addAttribute("activeNav", "dashboard");
        return "faculty/dashboard";
    }

    @GetMapping("/subjects")
    public String subjects(Authentication authentication, Model model) {
        Faculty faculty = getAuthenticatedFaculty(authentication);
        List<Subject> subjects = subjectService.getSubjectsByFaculty(faculty);
        model.addAttribute("subjects", subjects);
        model.addAttribute("activeNav", "subjects");
        return "faculty/subjects";
    }

    @GetMapping("/attendance")
    public String attendance(@RequestParam(required = false) Long subjectId,
                             @RequestParam(required = false) String date,
                             Authentication authentication,
                             Model model) {
        Faculty faculty = getAuthenticatedFaculty(authentication);
        List<Subject> subjects = subjectService.getSubjectsByFaculty(faculty);
        model.addAttribute("subjects", subjects);

        LocalDate selectedDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        model.addAttribute("selectedDate", selectedDate);

        Subject selectedSubject = null;
        if (subjectId != null) {
            selectedSubject = subjectService.getSubjectById(subjectId).orElse(null);
        } else if (!subjects.isEmpty()) {
            selectedSubject = subjects.get(0);
        }

        if (selectedSubject != null) {
            model.addAttribute("selectedSubject", selectedSubject);
            List<Student> students = studentService.getStudentsByDepartment(selectedSubject.getDepartment());
            model.addAttribute("students", students);

            List<Attendance> existingRecords = attendanceService.getAttendanceForSubjectAndDate(selectedSubject, selectedDate);
            Map<Long, Attendance.AttendanceStatus> statusMap = new HashMap<>();
            for (Attendance a : existingRecords) {
                statusMap.put(a.getStudent().getId(), a.getStatus());
            }
            model.addAttribute("statusMap", statusMap);
        }

        model.addAttribute("activeNav", "attendance");
        return "faculty/attendance";
    }

    @PostMapping("/attendance/save")
    public String saveAttendance(@RequestParam Long subjectId,
                                 @RequestParam String date,
                                 @RequestParam Map<String, String> allParams,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            Faculty faculty = getAuthenticatedFaculty(authentication);
            LocalDate attendanceDate = LocalDate.parse(date);

            Map<Long, Attendance.AttendanceStatus> studentStatuses = new HashMap<>();
            for (Map.Entry<String, String> entry : allParams.entrySet()) {
                if (entry.getKey().startsWith("student_status_")) {
                    Long studentId = Long.parseLong(entry.getKey().replace("student_status_", ""));
                    Attendance.AttendanceStatus status = Attendance.AttendanceStatus.valueOf(entry.getValue());
                    studentStatuses.put(studentId, status);
                }
            }

            attendanceService.markAttendance(subjectId, faculty.getId(), attendanceDate, studentStatuses);
            redirectAttributes.addFlashAttribute("successMessage", "Attendance recorded for " + studentStatuses.size() + " students on " + date + ".");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error recording attendance: " + ex.getMessage());
        }
        return "redirect:/faculty/attendance?subjectId=" + subjectId + "&date=" + date;
    }

    @GetMapping("/marks")
    public String marks(@RequestParam(required = false) Long subjectId,
                        Authentication authentication,
                        Model model) {
        Faculty faculty = getAuthenticatedFaculty(authentication);
        List<Subject> subjects = subjectService.getSubjectsByFaculty(faculty);
        model.addAttribute("subjects", subjects);

        Subject selectedSubject = null;
        if (subjectId != null) {
            selectedSubject = subjectService.getSubjectById(subjectId).orElse(null);
        } else if (!subjects.isEmpty()) {
            selectedSubject = subjects.get(0);
        }

        if (selectedSubject != null) {
            model.addAttribute("selectedSubject", selectedSubject);
            model.addAttribute("students", studentService.getStudentsByDepartment(selectedSubject.getDepartment()));
            model.addAttribute("marksList", markService.getMarksBySubject(selectedSubject));
        }

        model.addAttribute("assessmentTypes", Mark.AssessmentType.values());
        model.addAttribute("activeNav", "marks");
        return "faculty/marks";
    }

    @PostMapping("/marks/save")
    public String saveMark(@RequestParam Long studentId,
                           @RequestParam Long subjectId,
                           @RequestParam Mark.AssessmentType assessmentType,
                           @RequestParam Double maxMarks,
                           @RequestParam Double obtainedMarks,
                           @RequestParam(required = false) String remarks,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {
        try {
            Faculty faculty = getAuthenticatedFaculty(authentication);
            markService.saveMark(studentId, subjectId, faculty, assessmentType, maxMarks, obtainedMarks, remarks);
            redirectAttributes.addFlashAttribute("successMessage", "Marks saved successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/faculty/marks?subjectId=" + subjectId;
    }

    @GetMapping("/assignments")
    public String assignments(Authentication authentication, Model model) {
        Faculty faculty = getAuthenticatedFaculty(authentication);
        List<Assignment> assignments = assignmentService.getAssignmentsForFaculty(faculty);
        model.addAttribute("assignments", assignments);
        model.addAttribute("subjects", subjectService.getSubjectsByFaculty(faculty));
        model.addAttribute("activeNav", "assignments");
        return "faculty/assignments";
    }

    @PostMapping("/assignments/create")
    public String createAssignment(@RequestParam String title,
                                   @RequestParam String description,
                                   @RequestParam Long subjectId,
                                   @RequestParam String deadline,
                                   @RequestParam Double maxMarks,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            Faculty faculty = getAuthenticatedFaculty(authentication);
            Subject subject = subjectService.getSubjectById(subjectId).orElseThrow();

            Assignment assignment = Assignment.builder()
                    .title(title)
                    .description(description)
                    .subject(subject)
                    .faculty(faculty)
                    .deadline(LocalDateTime.parse(deadline))
                    .maxMarks(maxMarks)
                    .build();

            assignmentService.createAssignment(assignment);
            redirectAttributes.addFlashAttribute("successMessage", "Assignment published successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating assignment: " + ex.getMessage());
        }
        return "redirect:/faculty/assignments";
    }

    @GetMapping("/assignments/{id}/submissions")
    public String viewSubmissions(@PathVariable Long id, Model model) {
        Assignment assignment = assignmentService.getAssignmentById(id).orElseThrow();
        model.addAttribute("assignment", assignment);
        model.addAttribute("submissions", assignmentService.getSubmissionsForAssignment(assignment));
        model.addAttribute("activeNav", "assignments");
        return "faculty/submissions";
    }

    @PostMapping("/assignments/grade")
    public String gradeSubmission(@RequestParam Long submissionId,
                                  @RequestParam Double marksObtained,
                                  @RequestParam(required = false) String feedback,
                                  @RequestParam Long assignmentId,
                                  RedirectAttributes redirectAttributes) {
        try {
            assignmentService.gradeSubmission(submissionId, marksObtained, feedback);
            redirectAttributes.addFlashAttribute("successMessage", "Submission graded successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error grading: " + ex.getMessage());
        }
        return "redirect:/faculty/assignments/" + assignmentId + "/submissions";
    }

    @GetMapping("/materials")
    public String materials(Authentication authentication, Model model) {
        Faculty faculty = getAuthenticatedFaculty(authentication);
        model.addAttribute("materials", materialService.getMaterialsByFaculty(faculty));
        model.addAttribute("subjects", subjectService.getSubjectsByFaculty(faculty));
        model.addAttribute("activeNav", "materials");
        return "faculty/materials";
    }

    @PostMapping("/materials/upload")
    public String uploadMaterial(@RequestParam String title,
                                 @RequestParam String description,
                                 @RequestParam Long subjectId,
                                 @RequestParam String fileName,
                                 @RequestParam String fileType,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            Faculty faculty = getAuthenticatedFaculty(authentication);
            Subject subject = subjectService.getSubjectById(subjectId).orElseThrow();
            String simulatedPath = "/materials/" + fileName;

            materialService.uploadMaterial(title, description, subject, faculty, fileName, simulatedPath, fileType, 3500000L);
            redirectAttributes.addFlashAttribute("successMessage", "Study material uploaded successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error uploading material: " + ex.getMessage());
        }
        return "redirect:/faculty/materials";
    }

    @GetMapping("/notices")
    public String notices(Model model) {
        model.addAttribute("notices", noticeService.getActiveNotices());
        model.addAttribute("categories", Notice.NoticeCategory.values());
        model.addAttribute("activeNav", "notices");
        return "faculty/notices";
    }

    @PostMapping("/notices/publish")
    public String publishNotice(@RequestParam String title,
                                @RequestParam String description,
                                @RequestParam Notice.NoticeCategory category,
                                @RequestParam(required = false) String expiryDate,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            Faculty faculty = getAuthenticatedFaculty(authentication);
            LocalDate expiry = (expiryDate != null && !expiryDate.isEmpty()) ? LocalDate.parse(expiryDate) : null;
            noticeService.publishNotice(title, description, category, expiry, faculty.getName() + " (" + faculty.getDepartment().getDepartmentCode() + ")");
            redirectAttributes.addFlashAttribute("successMessage", "Academic notice posted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error posting notice: " + ex.getMessage());
        }
        return "redirect:/faculty/notices";
    }

    // --- NOTIFICATIONS ---
    @GetMapping("/notifications")
    public String notifications(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        model.addAttribute("notifications", notificationService.getNotificationsForUser(user));
        model.addAttribute("activeNav", "notifications");
        return "faculty/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsRead(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        notificationService.markAllAsRead(user);
        return "redirect:/faculty/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/faculty/notifications";
    }
}
