package app.controller;

import app.entity.*;
import app.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final DashboardService dashboardService;
    private final StudentService studentService;
    private final FacultyService facultyService;
    private final DepartmentService departmentService;
    private final SubjectService subjectService;
    private final TimetableService timetableService;
    private final NoticeService noticeService;
    private final CampusEventService eventService;
    private final ComplaintService complaintService;
    private final NotificationService notificationService;
    private final UserService userService;

    public AdminController(DashboardService dashboardService,
                           StudentService studentService,
                           FacultyService facultyService,
                           DepartmentService departmentService,
                           SubjectService subjectService,
                           TimetableService timetableService,
                           NoticeService noticeService,
                           CampusEventService eventService,
                           ComplaintService complaintService,
                           NotificationService notificationService,
                           UserService userService) {
        this.dashboardService = dashboardService;
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.departmentService = departmentService;
        this.subjectService = subjectService;
        this.timetableService = timetableService;
        this.noticeService = noticeService;
        this.eventService = eventService;
        this.complaintService = complaintService;
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAllAttributes(dashboardService.getAdminDashboardData());
        model.addAttribute("activeNav", "dashboard");
        return "admin/dashboard";
    }

    // --- STUDENTS ---
    @GetMapping("/students")
    public String students(@RequestParam(required = false) Long deptId,
                           @RequestParam(required = false) Integer year,
                           @RequestParam(required = false) Integer semester,
                           @RequestParam(required = false) String section,
                           @RequestParam(required = false) String search,
                           Model model) {
        List<Student> studentList;
        if (search != null && !search.trim().isEmpty()) {
            studentList = studentService.searchStudents(search.trim());
        } else if (deptId != null || year != null || semester != null || (section != null && !section.isEmpty())) {
            studentList = studentService.filterStudents(deptId, year, semester, section);
        } else {
            studentList = studentService.getAllStudents();
        }

        model.addAttribute("students", studentList);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("selectedDeptId", deptId);
        model.addAttribute("selectedYear", year);
        model.addAttribute("selectedSemester", semester);
        model.addAttribute("selectedSection", section);
        model.addAttribute("searchQuery", search);
        model.addAttribute("activeNav", "students");
        return "admin/students";
    }

    @PostMapping("/students/add")
    public String addStudent(@RequestParam String name,
                             @RequestParam String studentId,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam Long departmentId,
                             @RequestParam Integer year,
                             @RequestParam Integer semester,
                             @RequestParam String section,
                             RedirectAttributes redirectAttributes) {
        try {
            Department dept = departmentService.getDepartmentById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID"));

            Student student = Student.builder()
                    .name(name)
                    .studentId(studentId)
                    .email(email)
                    .phone(phone)
                    .department(dept)
                    .year(year)
                    .semester(semester)
                    .section(section)
                    .build();

            studentService.createStudent(student, "student123");
            redirectAttributes.addFlashAttribute("successMessage", "Student " + name + " enrolled successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error enrolling student: " + ex.getMessage());
        }
        return "redirect:/admin/students";
    }

    @PostMapping("/students/delete/{id}")
    public String deleteStudent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            studentService.deleteStudent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Student removed successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting student: " + ex.getMessage());
        }
        return "redirect:/admin/students";
    }

    // --- FACULTY ---
    @GetMapping("/faculty")
    public String faculty(@RequestParam(required = false) String search, Model model) {
        List<Faculty> list = facultyService.searchFaculty(search);
        model.addAttribute("facultyList", list);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("searchQuery", search);
        model.addAttribute("activeNav", "faculty");
        return "admin/faculty";
    }

    @PostMapping("/faculty/add")
    public String addFaculty(@RequestParam String name,
                             @RequestParam String facultyId,
                             @RequestParam String email,
                             @RequestParam String phone,
                             @RequestParam Long departmentId,
                             @RequestParam String designation,
                             @RequestParam String specialization,
                             RedirectAttributes redirectAttributes) {
        try {
            Department dept = departmentService.getDepartmentById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID"));

            Faculty faculty = Faculty.builder()
                    .name(name)
                    .facultyId(facultyId)
                    .email(email)
                    .phone(phone)
                    .department(dept)
                    .designation(designation)
                    .specialization(specialization)
                    .build();

            facultyService.createFaculty(faculty, "faculty123");
            redirectAttributes.addFlashAttribute("successMessage", "Faculty member added successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding faculty: " + ex.getMessage());
        }
        return "redirect:/admin/faculty";
    }

    @PostMapping("/faculty/delete/{id}")
    public String deleteFaculty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            facultyService.deleteFaculty(id);
            redirectAttributes.addFlashAttribute("successMessage", "Faculty member removed successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting faculty: " + ex.getMessage());
        }
        return "redirect:/admin/faculty";
    }

    // --- DEPARTMENTS ---
    @GetMapping("/departments")
    public String departments(Model model) {
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("activeNav", "departments");
        return "admin/departments";
    }

    @PostMapping("/departments/add")
    public String addDepartment(@RequestParam String departmentCode,
                                @RequestParam String departmentName,
                                @RequestParam String hodName,
                                RedirectAttributes redirectAttributes) {
        try {
            Department dept = Department.builder()
                    .departmentCode(departmentCode.toUpperCase())
                    .departmentName(departmentName)
                    .hodName(hodName)
                    .build();
            departmentService.saveDepartment(dept);
            redirectAttributes.addFlashAttribute("successMessage", "Department created successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating department: " + ex.getMessage());
        }
        return "redirect:/admin/departments";
    }

    @PostMapping("/departments/delete/{id}")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            departmentService.deleteDepartment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Department deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting department: " + ex.getMessage());
        }
        return "redirect:/admin/departments";
    }

    // --- SUBJECTS ---
    @GetMapping("/subjects")
    public String subjects(Model model) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("facultyList", facultyService.getAllFaculty());
        model.addAttribute("activeNav", "subjects");
        return "admin/subjects";
    }

    @PostMapping("/subjects/add")
    public String addSubject(@RequestParam String subjectCode,
                             @RequestParam String subjectName,
                             @RequestParam Integer credits,
                             @RequestParam Long departmentId,
                             @RequestParam Integer semester,
                             @RequestParam(required = false) Long facultyId,
                             RedirectAttributes redirectAttributes) {
        try {
            Department dept = departmentService.getDepartmentById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID"));

            Faculty faculty = facultyId != null ? facultyService.getFacultyById(facultyId).orElse(null) : null;

            Subject subject = Subject.builder()
                    .subjectCode(subjectCode.toUpperCase())
                    .subjectName(subjectName)
                    .credits(credits)
                    .department(dept)
                    .semester(semester)
                    .faculty(faculty)
                    .build();

            subjectService.saveSubject(subject);
            redirectAttributes.addFlashAttribute("successMessage", "Subject registered successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding subject: " + ex.getMessage());
        }
        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/assign")
    public String assignFacultyToSubject(@RequestParam Long subjectId,
                                         @RequestParam Long facultyId,
                                         RedirectAttributes redirectAttributes) {
        try {
            Faculty faculty = facultyService.getFacultyById(facultyId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid faculty ID"));
            subjectService.assignFaculty(subjectId, faculty);
            redirectAttributes.addFlashAttribute("successMessage", "Subject assigned to faculty successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error assigning faculty: " + ex.getMessage());
        }
        return "redirect:/admin/subjects";
    }

    @PostMapping("/subjects/delete/{id}")
    public String deleteSubject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            subjectService.deleteSubject(id);
            redirectAttributes.addFlashAttribute("successMessage", "Subject removed successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting subject: " + ex.getMessage());
        }
        return "redirect:/admin/subjects";
    }

    // --- TIMETABLE ---
    @GetMapping("/timetable")
    public String timetable(Model model) {
        model.addAttribute("timetableEntries", timetableService.getAllTimetableEntries());
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("subjects", subjectService.getAllSubjects());
        model.addAttribute("facultyList", facultyService.getAllFaculty());
        model.addAttribute("days", Timetable.DayOfWeek.values());
        model.addAttribute("activeNav", "timetable");
        return "admin/timetable";
    }

    @PostMapping("/timetable/add")
    public String addTimetableEntry(@RequestParam Timetable.DayOfWeek dayOfWeek,
                                    @RequestParam String startTime,
                                    @RequestParam String endTime,
                                    @RequestParam Long subjectId,
                                    @RequestParam Long facultyId,
                                    @RequestParam String room,
                                    @RequestParam Long departmentId,
                                    @RequestParam Integer year,
                                    @RequestParam String section,
                                    RedirectAttributes redirectAttributes) {
        try {
            Subject subject = subjectService.getSubjectById(subjectId).orElseThrow();
            Faculty faculty = facultyService.getFacultyById(facultyId).orElseThrow();
            Department department = departmentService.getDepartmentById(departmentId).orElseThrow();

            Timetable entry = Timetable.builder()
                    .dayOfWeek(dayOfWeek)
                    .startTime(LocalTime.parse(startTime))
                    .endTime(LocalTime.parse(endTime))
                    .subject(subject)
                    .faculty(faculty)
                    .room(room)
                    .department(department)
                    .year(year)
                    .section(section)
                    .build();

            timetableService.saveTimetable(entry);
            redirectAttributes.addFlashAttribute("successMessage", "Timetable slot added successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding timetable entry: " + ex.getMessage());
        }
        return "redirect:/admin/timetable";
    }

    @PostMapping("/timetable/delete/{id}")
    public String deleteTimetable(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            timetableService.deleteTimetable(id);
            redirectAttributes.addFlashAttribute("successMessage", "Timetable slot deleted.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting entry: " + ex.getMessage());
        }
        return "redirect:/admin/timetable";
    }

    // --- NOTICES ---
    @GetMapping("/notices")
    public String notices(Model model) {
        model.addAttribute("notices", noticeService.getActiveNotices());
        model.addAttribute("categories", Notice.NoticeCategory.values());
        model.addAttribute("activeNav", "notices");
        return "admin/notices";
    }

    @PostMapping("/notices/add")
    public String addNotice(@RequestParam String title,
                            @RequestParam String description,
                            @RequestParam Notice.NoticeCategory category,
                            @RequestParam(required = false) String expiryDate,
                            RedirectAttributes redirectAttributes) {
        try {
            LocalDate expiry = (expiryDate != null && !expiryDate.isEmpty()) ? LocalDate.parse(expiryDate) : null;
            noticeService.publishNotice(title, description, category, expiry, "Campus Administration");
            redirectAttributes.addFlashAttribute("successMessage", "Notice published successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error publishing notice: " + ex.getMessage());
        }
        return "redirect:/admin/notices";
    }

    @PostMapping("/notices/delete/{id}")
    public String deleteNotice(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            noticeService.deleteNotice(id);
            redirectAttributes.addFlashAttribute("successMessage", "Notice removed successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting notice: " + ex.getMessage());
        }
        return "redirect:/admin/notices";
    }

    // --- EVENTS ---
    @GetMapping("/events")
    public String events(Model model) {
        model.addAttribute("events", eventService.getAllEvents());
        model.addAttribute("activeNav", "events");
        return "admin/events";
    }

    @PostMapping("/events/add")
    public String addEvent(@RequestParam String title,
                           @RequestParam String description,
                           @RequestParam String eventDate,
                           @RequestParam String startTime,
                           @RequestParam String endTime,
                           @RequestParam String location,
                           @RequestParam String organizer,
                           @RequestParam Integer capacity,
                           @RequestParam String registrationDeadline,
                           RedirectAttributes redirectAttributes) {
        try {
            CampusEvent event = CampusEvent.builder()
                    .title(title)
                    .description(description)
                    .eventDate(LocalDate.parse(eventDate))
                    .startTime(LocalTime.parse(startTime))
                    .endTime(LocalTime.parse(endTime))
                    .location(location)
                    .organizer(organizer)
                    .capacity(capacity)
                    .registrationDeadline(LocalDate.parse(registrationDeadline))
                    .build();

            eventService.createEvent(event);
            redirectAttributes.addFlashAttribute("successMessage", "Campus event created successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating event: " + ex.getMessage());
        }
        return "redirect:/admin/events";
    }

    @PostMapping("/events/delete/{id}")
    public String deleteEvent(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            eventService.deleteEvent(id);
            redirectAttributes.addFlashAttribute("successMessage", "Event deleted successfully.");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error deleting event: " + ex.getMessage());
        }
        return "redirect:/admin/events";
    }

    @GetMapping("/events/{id}/attendees")
    public String eventAttendees(@PathVariable Long id, Model model) {
        CampusEvent event = eventService.getEventById(id).orElseThrow();
        model.addAttribute("event", event);
        model.addAttribute("registrations", eventService.getRegistrationsForEvent(id));
        model.addAttribute("activeNav", "events");
        return "admin/event_attendees";
    }

    // --- COMPLAINTS ---
    @GetMapping("/complaints")
    public String complaints(@RequestParam(required = false) Complaint.ComplaintStatus status,
                             @RequestParam(required = false) Complaint.ComplaintCategory category,
                             Model model) {
        model.addAttribute("complaints", complaintService.filterComplaints(status, category));
        model.addAttribute("statuses", Complaint.ComplaintStatus.values());
        model.addAttribute("categories", Complaint.ComplaintCategory.values());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("activeNav", "complaints");
        return "admin/complaints";
    }

    @PostMapping("/complaints/update")
    public String updateComplaint(@RequestParam Long complaintId,
                                  @RequestParam Complaint.ComplaintStatus status,
                                  @RequestParam(required = false) String adminResponse,
                                  RedirectAttributes redirectAttributes) {
        try {
            complaintService.updateComplaintStatus(complaintId, status, adminResponse);
            redirectAttributes.addFlashAttribute("successMessage", "Complaint updated successfully!");
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating complaint: " + ex.getMessage());
        }
        return "redirect:/admin/complaints";
    }

    // --- NOTIFICATIONS ---
    @GetMapping("/notifications")
    public String notifications(Authentication authentication, Model model) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        model.addAttribute("notifications", notificationService.getNotificationsForUser(user));
        model.addAttribute("activeNav", "notifications");
        return "admin/notifications";
    }

    @PostMapping("/notifications/read-all")
    public String markAllNotificationsRead(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElseThrow();
        notificationService.markAllAsRead(user);
        return "redirect:/admin/notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markNotificationRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "redirect:/admin/notifications";
    }

    // --- REPORTS ---
    @GetMapping("/reports")
    public String reports(Model model) {
        model.addAllAttributes(dashboardService.getAdminDashboardData());
        model.addAttribute("activeNav", "reports");
        return "admin/reports";
    }
}
