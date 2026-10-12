package app.controller;

import app.dto.StudentAttendanceSummaryDto;
import app.entity.*;
import app.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/student")
public class StudentController {

    private final DashboardService dashboardService;
    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final MarkService markService;
    private final TimetableService timetableService;
    private final AssignmentService assignmentService;
    private final MaterialService materialService;
    private final NoticeService noticeService;
    private final CampusEventService eventService;
    private final ComplaintService complaintService;
    private final NotificationService notificationService;
    private final UserService userService;

    public StudentController(DashboardService dashboardService,
                             StudentService studentService,
                             AttendanceService attendanceService,
                             MarkService markService,
                             TimetableService timetableService,
                             AssignmentService assignmentService,
                             MaterialService materialService,
                             NoticeService noticeService,
                             CampusEventService eventService,
                             ComplaintService complaintService,
                             NotificationService notificationService,
                             UserService userService) {
        this.dashboardService = dashboardService;
        this.studentService = studentService;
        this.attendanceService = attendanceService;
        this.markService = markService;
        this.timetableService = timetableService;
        this.assignmentService = assignmentService;
        this.materialService = materialService;
        this.noticeService = noticeService;
        this.eventService = eventService;
        this.complaintService = complaintService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    private Student getAuthenticatedStudent(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        return studentService.getStudentByUser(user)
                .orElseThrow(() -> new IllegalStateException("Student profile not found for user: " + user.getUsername()));
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        Student student = getAuthenticatedStudent(authentication);
        model.addAllAttributes(dashboardService.getStudentDashboardData(student));
        model.addAttribute("activeNav", "dashboard");
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        Student student = getAuthenticatedStudent(authentication);
        model.addAttribute("student", student);
        model.addAttribute("overallAttendance", attendanceService.getOverallAttendancePercentage(student));
        model.addAttribute("academicAverage", markService.getAveragePercentageForStudent(student));
        model.addAttribute("activeNav", "profile");
        return "student/profile";
    }

    @GetMapping("/attendance")
    public String attendance(Authentication authentication, Model model) {
        Student student = getAuthenticatedStudent(authentication);
        List<StudentAttendanceSummaryDto> summaries = attendanceService.getSummaryForStudent(student);
        model.addAttribute("summaries", summaries);
        model.addAttribute("overallPercentage", attendanceService.getOverallAttendancePercentage(student));
        model.addAttribute("isShortage", attendanceService.isOverallShortage(student));
        model.addAttribute("activeNav", "attendance");
        return "student/attendance";
    }

    @GetMapping("/marks")
    public String marks(Authentication authentication, Model model) {
        Student student = getAuthenticatedStudent(authentication);
        List<Mark> marks = markService.getMarksByStudent(student);
        model.addAttribute("marksList", marks);
        model.addAttribute("academicAverage", markService.getAveragePercentageForStudent(student));
        model.addAttribute("activeNav", "marks");
        return "student/marks";
    }

    @GetMapping("/timetable")
    public String timetable(Authentication authentication, Model model) {
        Student student = getAuthenticatedStudent(authentication);
        Map<Timetable.DayOfWeek, List<Timetable>> timetableMap = timetableService.getGroupedTimetableForStudent(student);
        model.addAttribute("timetableMap", timetableMap);
        model.addAttribute("student", student);
        model.addAttribute("activeNav", "timetable");
        return "student/timetable";
    }

    @GetMapping("/assignments")
    public String assignments(Authentication authentication, Model model) {
        Student student = getAuthenticatedStudent(authentication);
        List<Assignment> assignments = assignmentService.getAssignmentsForStudent(student);

        Map<Long, AssignmentSubmission> submissionMap = new HashMap<>();
        for (Assignment a : assignments) {
            assignmentService.getSubmission(a, student).ifPresent(sub -> submissionMap.put(a.getId(), sub));
        }

        model.addAttribute("assignments", assignments);
        model.addAttribute("submissionMap", submissionMap);
        model.addAttribute("activeNav", "assignments");
        return "student/assignments";
    }

    @PostMapping("/assignments/submit")
    public String submitAssignment(@RequestParam Long assignmentId,
                                   @RequestParam String submissionText,
                                   @RequestParam(required = false) String fileName,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            Student student = getAuthenticatedStudent(authentication);
            String filePath = (fileName != null && !fileName.isEmpty()) ? "/submissions/" + fileName : null;
            assignmentService.submitAssignment(assignmentId, student, submissionText, filePath);
            redirectAttributes.addFlashAttribute("successMessage", "Assignment submitted successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting assignment: " + ex.getMessage());
        }
        return "redirect:/student/assignments";
    }

    @GetMapping("/materials")
    public String materials(Authentication authentication, Model model) {
        Student student = getAuthenticatedStudent(authentication);
        model.addAttribute("materials", materialService.getMaterialsForStudent(student));
        model.addAttribute("activeNav", "materials");
        return "student/materials";
    }

    @GetMapping("/notices")
    public String notices(@RequestParam(required = false) Notice.NoticeCategory category, Model model) {
        if (category != null) {
            model.addAttribute("notices", noticeService.getNoticesByCategory(category));
        } else {
            model.addAttribute("notices", noticeService.getActiveNotices());
        }
        model.addAttribute("categories", Notice.NoticeCategory.values());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("activeNav", "notices");
        return "student/notices";
    }

    @GetMapping("/events")
    public String events(Authentication authentication, Model model) {
        Student student = getAuthenticatedStudent(authentication);
        List<CampusEvent> upcoming = eventService.getUpcomingEvents();

        Set<Long> registeredEventIds = new HashSet<>();
        for (EventRegistration reg : eventService.getRegistrationsForStudent(student)) {
            registeredEventIds.add(reg.getCampusEvent().getId());
        }

        model.addAttribute("events", upcoming);
        model.addAttribute("registeredEventIds", registeredEventIds);
        model.addAttribute("activeNav", "events");
        return "student/events";
    }

    @PostMapping("/events/register")
    public String registerForEvent(@RequestParam Long eventId,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        try {
            Student student = getAuthenticatedStudent(authentication);
            eventService.registerStudentForEvent(eventId, student);
            redirectAttributes.addFlashAttribute("successMessage", "Successfully registered for event!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/events";
    }

    @PostMapping("/events/cancel")
    public String cancelEventRegistration(@RequestParam Long eventId,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes) {
        try {
            Student student = getAuthenticatedStudent(authentication);
            eventService.unregisterStudentFromEvent(eventId, student);
            redirectAttributes.addFlashAttribute("successMessage", "Event registration cancelled.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/events";
    }

    @GetMapping("/complaints")
    public String complaints(Authentication authentication, Model model) {
        Student student = getAuthenticatedStudent(authentication);
        model.addAttribute("complaints", complaintService.getComplaintsByStudent(student));
        model.addAttribute("categories", Complaint.ComplaintCategory.values());
        model.addAttribute("activeNav", "complaints");
        return "student/complaints";
    }

    @PostMapping("/complaints/raise")
    public String raiseComplaint(@RequestParam Complaint.ComplaintCategory category,
                                 @RequestParam String subject,
                                 @RequestParam String description,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            Student student = getAuthenticatedStudent(authentication);
            complaintService.raiseComplaint(student, category, subject, description);
            redirectAttributes.addFlashAttribute("successMessage", "Grievance ticket submitted. Tracking is active.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error submitting complaint: " + ex.getMessage());
        }
        return "redirect:/student/complaints";
    }

    @PostMapping("/complaints/cancel")
    public String cancelComplaint(@RequestParam Long complaintId,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        try {
            Student student = getAuthenticatedStudent(authentication);
            complaintService.cancelComplaint(complaintId, student);
            redirectAttributes.addFlashAttribute("successMessage", "Grievance ticket #" + complaintId + " has been withdrawn.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/student/complaints";
    }

    @GetMapping("/notifications")
    public String notifications(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        model.addAttribute("notifications", notificationService.getNotificationsForUser(user));
        model.addAttribute("activeNav", "notifications");
        return "student/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsRead(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        notificationService.markAllAsRead(user);
        return "redirect:/student/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/student/notifications";
    }
}
