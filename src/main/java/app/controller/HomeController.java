package app.controller;

import app.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main Controller for Smart Campus System Home and Health verification.
 */
@Controller
public class HomeController {

    private final StudentService studentService;
    private final FacultyService facultyService;
    private final DepartmentService departmentService;
    private final CampusEventService eventService;
    private final NoticeService noticeService;
    private final ComplaintService complaintService;
    private final UserService userService;

    public HomeController(StudentService studentService,
                          FacultyService facultyService,
                          DepartmentService departmentService,
                          CampusEventService eventService,
                          NoticeService noticeService,
                          ComplaintService complaintService,
                          UserService userService) {
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.departmentService = departmentService;
        this.eventService = eventService;
        this.noticeService = noticeService;
        this.complaintService = complaintService;
        this.userService = userService;
    }

    @GetMapping({"/", "/index"})
    public String index(Model model) {
        model.addAttribute("systemName", "SMART CAMPUS MANAGEMENT SYSTEM");
        model.addAttribute("version", "1.0.0-PROD");
        model.addAttribute("currentPhase", "PHASE 5: Complete Enterprise Workflows & Real-Time Redressal Engine");
        model.addAttribute("serverTime", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm:ss a")));
        model.addAttribute("status", "ONLINE — ALL WORKFLOWS ACTIVE");

        // Real-time campus operational statistics
        model.addAttribute("totalStudents", studentService.countStudents());
        model.addAttribute("totalFaculty", facultyService.countFaculty());
        model.addAttribute("totalDepartments", departmentService.getAllDepartments().size());
        model.addAttribute("upcomingEvents", eventService.countUpcomingEvents());
        model.addAttribute("activeNotices", noticeService.countActiveNotices());
        model.addAttribute("pendingComplaints", complaintService.countPendingComplaints());

        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "register";
    }

    @org.springframework.web.bind.annotation.PostMapping("/register")
    public String processRegistration(@org.springframework.web.bind.annotation.RequestParam String name,
                                      @org.springframework.web.bind.annotation.RequestParam String studentId,
                                      @org.springframework.web.bind.annotation.RequestParam String email,
                                      @org.springframework.web.bind.annotation.RequestParam(required = false) String phone,
                                      @org.springframework.web.bind.annotation.RequestParam Long departmentId,
                                      @org.springframework.web.bind.annotation.RequestParam Integer year,
                                      @org.springframework.web.bind.annotation.RequestParam Integer semester,
                                      @org.springframework.web.bind.annotation.RequestParam String section,
                                      @org.springframework.web.bind.annotation.RequestParam String password,
                                      @org.springframework.web.bind.annotation.RequestParam String confirmPassword,
                                      Model model,
                                      org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "register";
        }
        if (userService.existsByUsername(email.trim().toLowerCase())) {
            model.addAttribute("errorMessage", "An account with this email already exists.");
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "register";
        }
        if (studentService.getStudentByStudentId(studentId.trim().toUpperCase()).isPresent()) {
            model.addAttribute("errorMessage", "A student with this Roll No / Student ID already exists.");
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "register";
        }

        app.entity.Department dept = departmentService.getDepartmentById(departmentId).orElse(null);
        if (dept == null) {
            model.addAttribute("errorMessage", "Please select a valid academic department.");
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "register";
        }

        app.entity.Student student = app.entity.Student.builder()
                .name(name.trim())
                .studentId(studentId.trim().toUpperCase())
                .email(email.trim().toLowerCase())
                .phone(phone != null ? phone.trim() : "")
                .department(dept)
                .year(year != null ? year : 1)
                .semester(semester != null ? semester : 1)
                .section(section != null && !section.trim().isEmpty() ? section.trim().toUpperCase() : "A")
                .build();

        studentService.createStudent(student, password);
        redirectAttributes.addFlashAttribute("successMessage", "Registration complete! You can now log in.");
        return "redirect:/login?registered=true";
    }

    @GetMapping("/access-denied")
    public String accessDenied() {
        return "error/403";
    }
}
