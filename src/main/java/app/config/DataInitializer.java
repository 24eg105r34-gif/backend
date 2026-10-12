package app.config;

import app.entity.*;
import app.repository.*;
import app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${campus.seed.demodata:false}")
    private boolean seedDemoData;

    private final UserRepository userRepository;
    private final UserService userService;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;
    private final TimetableRepository timetableRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final MaterialRepository materialRepository;
    private final NoticeRepository noticeRepository;
    private final CampusEventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final ComplaintRepository complaintRepository;
    private final NotificationRepository notificationRepository;

    public DataInitializer(UserRepository userRepository,
                           UserService userService,
                           DepartmentRepository departmentRepository,
                           FacultyRepository facultyRepository,
                           StudentRepository studentRepository,
                           SubjectRepository subjectRepository,
                           AttendanceRepository attendanceRepository,
                           MarkRepository markRepository,
                           TimetableRepository timetableRepository,
                           AssignmentRepository assignmentRepository,
                           AssignmentSubmissionRepository submissionRepository,
                           MaterialRepository materialRepository,
                           NoticeRepository noticeRepository,
                           CampusEventRepository eventRepository,
                           EventRegistrationRepository registrationRepository,
                           ComplaintRepository complaintRepository,
                           NotificationRepository notificationRepository) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
        this.timetableRepository = timetableRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.materialRepository = materialRepository;
        this.noticeRepository = noticeRepository;
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.complaintRepository = complaintRepository;
        this.notificationRepository = notificationRepository;
    }

    private void createAdminIfNotExists(String username, String password, String fullName) {
        if (!userRepository.existsByUsername(username)) {
            userService.createUser(username, password, Role.ROLE_ADMIN, fullName);
            log.info("Created Administrator account: {}", username);
        }
    }

    @Override
    public void run(String... args) {
        // 1. Ensure Administrator accounts exist (including Lead Developer Peddi Kalyan - 24EG105R34)
        createAdminIfNotExists("24eg105r34@gmail.com", "admin123", "Peddi Kalyan (24EG105R34)");
        createAdminIfNotExists("24eg105r34@anurag.edu.in", "admin123", "Peddi Kalyan (24EG105R34)");
        createAdminIfNotExists("23eg105r34@anurag.edu.in", "admin123", "Anurag Administrator (23EG105R34)");
        createAdminIfNotExists("23eg105r342@anurag.edu.in", "admin123", "Anurag Administrator (23EG105R342)");
        createAdminIfNotExists("23eg105r342anurag.edu.in", "admin123", "Anurag Administrator");
        createAdminIfNotExists("admin@campus.edu", "admin123", "Campus Administrator");

        if (!seedDemoData) {
            log.info("Demo dataset seeding disabled (campus.seed.demodata=false). Clean campus mode active with no fake data.");
            return;
        }

        if (departmentRepository.count() > 0) {
            log.info("Database already seeded with demo campus records.");
            return;
        }

        log.info("Initializing Smart Campus Management System demo dataset...");

        User adminUser = userRepository.findByUsername("admin@campus.edu").orElse(null);

        // 2. Create Departments
        Department cse = departmentRepository.save(Department.builder()
                .departmentCode("CSE")
                .departmentName("Computer Science & Engineering")
                .hodName("Dr. Arvind Raman")
                .build());

        Department ece = departmentRepository.save(Department.builder()
                .departmentCode("ECE")
                .departmentName("Electronics & Communication Engineering")
                .hodName("Dr. Sunita Varma")
                .build());

        Department mech = departmentRepository.save(Department.builder()
                .departmentCode("MECH")
                .departmentName("Mechanical Engineering")
                .hodName("Dr. Rajesh Kulkarni")
                .build());

        // 3. Create 3 Faculty
        User fUser1 = userService.createUser("prof.sharma@campus.edu", "faculty123", Role.ROLE_FACULTY, "Prof. Alok Sharma");
        Faculty faculty1 = facultyRepository.save(Faculty.builder()
                .facultyId("FAC-CSE-01")
                .name("Prof. Alok Sharma")
                .email("prof.sharma@campus.edu")
                .phone("+91 98765 43210")
                .department(cse)
                .designation("Associate Professor")
                .specialization("Distributed Systems & Cloud Computing")
                .user(fUser1)
                .build());

        User fUser2 = userService.createUser("dr.patel@campus.edu", "faculty123", Role.ROLE_FACULTY, "Dr. Meera Patel");
        Faculty faculty2 = facultyRepository.save(Faculty.builder()
                .facultyId("FAC-ECE-02")
                .name("Dr. Meera Patel")
                .email("dr.patel@campus.edu")
                .phone("+91 98765 43211")
                .department(ece)
                .designation("Professor")
                .specialization("VLSI & Embedded Systems")
                .user(fUser2)
                .build());

        User fUser3 = userService.createUser("prof.rao@campus.edu", "faculty123", Role.ROLE_FACULTY, "Prof. Suresh Rao");
        Faculty faculty3 = facultyRepository.save(Faculty.builder()
                .facultyId("FAC-MECH-03")
                .name("Prof. Suresh Rao")
                .email("prof.rao@campus.edu")
                .phone("+91 98765 43212")
                .department(mech)
                .designation("Assistant Professor")
                .specialization("Thermodynamics & Fluid Mechanics")
                .user(fUser3)
                .build());

        // 4. Create 6 Subjects
        Subject os = subjectRepository.save(Subject.builder()
                .subjectCode("CS501")
                .subjectName("Operating Systems")
                .credits(4)
                .department(cse)
                .semester(5)
                .faculty(faculty1)
                .build());

        Subject dbms = subjectRepository.save(Subject.builder()
                .subjectCode("CS502")
                .subjectName("Database Management Systems")
                .credits(4)
                .department(cse)
                .semester(5)
                .faculty(faculty1)
                .build());

        Subject javaProg = subjectRepository.save(Subject.builder()
                .subjectCode("CS503")
                .subjectName("Java & Enterprise Technologies")
                .credits(3)
                .department(cse)
                .semester(5)
                .faculty(faculty1)
                .build());

        Subject dsp = subjectRepository.save(Subject.builder()
                .subjectCode("EC501")
                .subjectName("Digital Signal Processing")
                .credits(4)
                .department(ece)
                .semester(5)
                .faculty(faculty2)
                .build());

        Subject vlsi = subjectRepository.save(Subject.builder()
                .subjectCode("EC502")
                .subjectName("VLSI Design Fundamentals")
                .credits(3)
                .department(ece)
                .semester(5)
                .faculty(faculty2)
                .build());

        Subject thermo = subjectRepository.save(Subject.builder()
                .subjectCode("ME501")
                .subjectName("Applied Thermodynamics")
                .credits(4)
                .department(mech)
                .semester(5)
                .faculty(faculty3)
                .build());

        // 5. Create 11 Students
        String[][] studentData = {
                {"rahul.cse@campus.edu", "Rahul Verma", "STU-2026-001", "CSE", "3", "5", "A"},
                {"sneha.cse@campus.edu", "Sneha Iyer", "STU-2026-002", "CSE", "3", "5", "A"},
                {"rohit.cse@campus.edu", "Rohit Kumar", "STU-2026-003", "CSE", "3", "5", "A"},
                {"ananya.cse@campus.edu", "Ananya Sen", "STU-2026-004", "CSE", "3", "5", "B"},
                {"vikram.cse@campus.edu", "Vikram Joshi", "STU-2026-005", "CSE", "3", "5", "B"},
                {"amit.ece@campus.edu", "Amit Roy", "STU-2026-006", "ECE", "3", "5", "A"},
                {"pooja.ece@campus.edu", "Pooja Nair", "STU-2026-007", "ECE", "3", "5", "A"},
                {"karan.ece@campus.edu", "Karan Das", "STU-2026-008", "ECE", "3", "5", "B"},
                {"priya.mech@campus.edu", "Priya Kulkarni", "STU-2026-009", "MECH", "3", "5", "A"},
                {"manish.mech@campus.edu", "Manish Tiwari", "STU-2026-010", "MECH", "3", "5", "A"},
                {"deepak.mech@campus.edu", "Deepak Solanki", "STU-2026-011", "MECH", "3", "5", "B"}
        };

        Student firstStudent = null;
        Student shortageStudent = null;

        for (int i = 0; i < studentData.length; i++) {
            String[] s = studentData[i];
            User stuUser = userService.createUser(s[0], "student123", Role.ROLE_STUDENT, s[1]);
            Department dept = s[3].equals("CSE") ? cse : s[3].equals("ECE") ? ece : mech;

            Student student = studentRepository.save(Student.builder()
                    .studentId(s[2])
                    .name(s[1])
                    .email(s[0])
                    .phone("+91 91234 567" + (10 + i))
                    .department(dept)
                    .year(Integer.parseInt(s[4]))
                    .semester(Integer.parseInt(s[5]))
                    .section(s[6])
                    .user(stuUser)
                    .build());

            if (i == 0) firstStudent = student;
            if (i == 1) shortageStudent = student;
        }

        // 6. Seed Attendance Records
        LocalDate today = LocalDate.now();
        if (firstStudent != null) {
            for (int dayOffset = 10; dayOffset >= 1; dayOffset--) {
                attendanceRepository.save(Attendance.builder()
                        .student(firstStudent)
                        .subject(os)
                        .faculty(faculty1)
                        .attendanceDate(today.minusDays(dayOffset))
                        .status(dayOffset == 4 || dayOffset == 7 ? Attendance.AttendanceStatus.ABSENT : Attendance.AttendanceStatus.PRESENT)
                        .remarks(dayOffset == 4 ? "Medical Leave" : "Normal Class")
                        .build());

                attendanceRepository.save(Attendance.builder()
                        .student(firstStudent)
                        .subject(dbms)
                        .faculty(faculty1)
                        .attendanceDate(today.minusDays(dayOffset))
                        .status(dayOffset == 3 ? Attendance.AttendanceStatus.ABSENT : Attendance.AttendanceStatus.PRESENT)
                        .remarks("Regular lecture")
                        .build());
            }
        }

        if (shortageStudent != null) {
            // Shortage student (Sneha) has attendance shortage: 4/10 = 40% (< 75%)
            for (int dayOffset = 10; dayOffset >= 1; dayOffset--) {
                attendanceRepository.save(Attendance.builder()
                        .student(shortageStudent)
                        .subject(os)
                        .faculty(faculty1)
                        .attendanceDate(today.minusDays(dayOffset))
                        .status(dayOffset <= 4 ? Attendance.AttendanceStatus.PRESENT : Attendance.AttendanceStatus.ABSENT)
                        .remarks(dayOffset > 4 ? "Unexcused Absence" : "Present")
                        .build());
            }
        }

        // 7. Seed Marks
        if (firstStudent != null) {
            markRepository.save(Mark.builder()
                    .student(firstStudent)
                    .subject(os)
                    .faculty(faculty1)
                    .assessmentType(Mark.AssessmentType.MIDTERM)
                    .maxMarks(50.0)
                    .obtainedMarks(44.0)
                    .percentage(88.0)
                    .remarks("Strong conceptual understanding")
                    .build());

            markRepository.save(Mark.builder()
                    .student(firstStudent)
                    .subject(dbms)
                    .faculty(faculty1)
                    .assessmentType(Mark.AssessmentType.MIDTERM)
                    .maxMarks(50.0)
                    .obtainedMarks(42.5)
                    .percentage(85.0)
                    .remarks("Good query optimization knowledge")
                    .build());

            markRepository.save(Mark.builder()
                    .student(firstStudent)
                    .subject(javaProg)
                    .faculty(faculty1)
                    .assessmentType(Mark.AssessmentType.ASSIGNMENT)
                    .maxMarks(25.0)
                    .obtainedMarks(23.0)
                    .percentage(92.0)
                    .remarks("Clean code style")
                    .build());
        }

        // 8. Seed Timetable
        Timetable.DayOfWeek[] days = {
                Timetable.DayOfWeek.MONDAY,
                Timetable.DayOfWeek.TUESDAY,
                Timetable.DayOfWeek.WEDNESDAY,
                Timetable.DayOfWeek.THURSDAY,
                Timetable.DayOfWeek.FRIDAY
        };

        for (Timetable.DayOfWeek d : days) {
            timetableRepository.save(Timetable.builder()
                    .dayOfWeek(d)
                    .startTime(LocalTime.of(9, 0))
                    .endTime(LocalTime.of(10, 0))
                    .subject(os)
                    .faculty(faculty1)
                    .room("LH-201")
                    .department(cse)
                    .year(3)
                    .section("A")
                    .build());

            timetableRepository.save(Timetable.builder()
                    .dayOfWeek(d)
                    .startTime(LocalTime.of(10, 15))
                    .endTime(LocalTime.of(11, 15))
                    .subject(dbms)
                    .faculty(faculty1)
                    .room("LH-201")
                    .department(cse)
                    .year(3)
                    .section("A")
                    .build());

            timetableRepository.save(Timetable.builder()
                    .dayOfWeek(d)
                    .startTime(LocalTime.of(11, 30))
                    .endTime(LocalTime.of(12, 30))
                    .subject(javaProg)
                    .faculty(faculty1)
                    .room("CS-Lab 3")
                    .department(cse)
                    .year(3)
                    .section("A")
                    .build());
        }

        // 9. Seed Notices
        noticeRepository.save(Notice.builder()
                .title("Midterm Examination Schedule — Autumn 2026")
                .description("The Midterm Examination for Semester 5 will commence from 24th October 2026. Hall tickets will be issued digitally through the campus portal.")
                .category(Notice.NoticeCategory.Examination)
                .publishedDate(LocalDate.now().minusDays(2))
                .expiryDate(LocalDate.now().plusDays(30))
                .createdBy("Office of the Dean Academics")
                .active(true)
                .build());

        noticeRepository.save(Notice.builder()
                .title("Smart Campus Innovation Hackathon 2026")
                .description("Registrations are now open for the annual 36-hour inter-collegiate hackathon. Cash prizes up to Rs. 2,00,000 to be won.")
                .category(Notice.NoticeCategory.Event)
                .publishedDate(LocalDate.now().minusDays(1))
                .expiryDate(LocalDate.now().plusDays(15))
                .createdBy("Innovation & Incubation Cell")
                .active(true)
                .build());

        noticeRepository.save(Notice.builder()
                .title("Library Timing Extension for Exam Preparation")
                .description("The Central Library will remain open until 11:00 PM on all working days throughout the upcoming examination period.")
                .category(Notice.NoticeCategory.Academic)
                .publishedDate(LocalDate.now())
                .expiryDate(LocalDate.now().plusDays(20))
                .createdBy("Chief Librarian")
                .active(true)
                .build());

        // 10. Seed Campus Events
        CampusEvent event1 = eventRepository.save(CampusEvent.builder()
                .title("TECH FEST 2026 — Annual Technology Conclave")
                .description("National collegiate technical symposium featuring coding marathons, robotics showcases, and keynote speeches by industry pioneers.")
                .eventDate(LocalDate.now().plusDays(12))
                .startTime(LocalTime.of(9, 30))
                .endTime(LocalTime.of(17, 30))
                .location("Main Auditorium")
                .organizer("Department of Computer Science & Engineering")
                .capacity(100)
                .registeredCount(14)
                .registrationDeadline(LocalDate.now().plusDays(10))
                .build());

        CampusEvent event2 = eventRepository.save(CampusEvent.builder()
                .title("Industry Expert Talk: Cloud Architecture at Scale")
                .description("Interactive masterclass on enterprise microservices, Kubernetes orchestration, and reactive systems by Lead Architect from Google.")
                .eventDate(LocalDate.now().plusDays(5))
                .startTime(LocalTime.of(14, 0))
                .endTime(LocalTime.of(16, 0))
                .location("Seminar Hall B")
                .organizer("Faculty of Engineering")
                .capacity(60)
                .registeredCount(42)
                .registrationDeadline(LocalDate.now().plusDays(4))
                .build());

        // Register firstStudent for event1
        if (firstStudent != null) {
            registrationRepository.save(EventRegistration.builder()
                    .campusEvent(event1)
                    .student(firstStudent)
                    .registrationDate(LocalDateTime.now().minusDays(1))
                    .build());
        }

        // 11. Seed Assignments & Submissions
        Assignment assignment1 = assignmentRepository.save(Assignment.builder()
                .title("Process Scheduling Algorithms Simulation")
                .description("Implement Round Robin, Shortest Job First, and Priority Scheduling in Java. Submit code and analysis report.")
                .subject(os)
                .faculty(faculty1)
                .deadline(LocalDateTime.now().plusDays(4))
                .maxMarks(25.0)
                .build());

        if (firstStudent != null) {
            submissionRepository.save(AssignmentSubmission.builder()
                    .assignment(assignment1)
                    .student(firstStudent)
                    .submissionText("Completed implementation with Gantt chart calculation and turn-around time graphs.")
                    .status(AssignmentSubmission.SubmissionStatus.SUBMITTED)
                    .submissionDate(LocalDateTime.now().minusHours(6))
                    .build());
        }

        // 12. Seed Study Materials
        materialRepository.save(Material.builder()
                .title("Unit 3: CPU Scheduling & Synchronization Primitives")
                .description("Comprehensive lecture slide deck covering mutexes, semaphores, monitors, and deadlock prevention.")
                .subject(os)
                .faculty(faculty1)
                .fileName("Unit_3_CPU_Scheduling.pdf")
                .filePath("/materials/Unit_3_CPU_Scheduling.pdf")
                .fileType("PDF")
                .fileSize(4521000L)
                .build());

        materialRepository.save(Material.builder()
                .title("Relational Algebra & SQL Normalization Handout")
                .description("Study notes on 1NF, 2NF, 3NF, BCNF with solved examination problems.")
                .subject(dbms)
                .faculty(faculty1)
                .fileName("DBMS_Normalization_Guide.pdf")
                .filePath("/materials/DBMS_Normalization_Guide.pdf")
                .fileType("PDF")
                .fileSize(2840000L)
                .build());

        // 13. Seed Complaints
        if (firstStudent != null) {
            complaintRepository.save(Complaint.builder()
                    .student(firstStudent)
                    .category(Complaint.ComplaintCategory.Laboratory)
                    .subject("Projector malfunctioning in CS Lab 3")
                    .description("The overhead HDMI projector flickers continuously during the afternoon lab sessions.")
                    .status(Complaint.ComplaintStatus.RESOLVED)
                    .adminResponse("IT maintenance replaced the HDMI cable and refreshed projector firmware on Monday.")
                    .createdAt(LocalDateTime.now().minusDays(5))
                    .resolvedAt(LocalDateTime.now().minusDays(2))
                    .build());

            complaintRepository.save(Complaint.builder()
                    .student(firstStudent)
                    .category(Complaint.ComplaintCategory.Internet)
                    .subject("Low Wi-Fi signal in Library 2nd floor reading hall")
                    .description("Wi-Fi keeps disconnecting frequently near the journals section.")
                    .status(Complaint.ComplaintStatus.IN_PROGRESS)
                    .adminResponse("Network engineer dispatched to install an additional wireless access point.")
                    .createdAt(LocalDateTime.now().minusDays(1))
                    .build());
        }

        // 14. Seed Notifications
        // Student Notifications
        if (firstStudent != null) {
            notificationRepository.save(Notification.builder()
                    .user(firstStudent.getUser())
                    .title("Event Pass Confirmed: TECH FEST 2026")
                    .message("Your registration is confirmed for TECH FEST 2026 on " + event1.getEventDate() + " at Main Auditorium.")
                    .type(Notification.NotificationType.EVENT)
                    .linkUrl("/student/events")
                    .read(false)
                    .build());

            notificationRepository.save(Notification.builder()
                    .user(firstStudent.getUser())
                    .title("New Assignment Posted: Process Scheduling")
                    .message("Prof. Alok Sharma posted a new assignment for CS501 Operating Systems.")
                    .type(Notification.NotificationType.ASSIGNMENT)
                    .linkUrl("/student/assignments")
                    .read(false)
                    .build());

            notificationRepository.save(Notification.builder()
                    .user(firstStudent.getUser())
                    .title("Grievance In Progress (#TKT-2)")
                    .message("Admin updated ticket status to IN_PROGRESS: Network engineer dispatched to install an additional wireless access point.")
                    .type(Notification.NotificationType.COMPLAINT)
                    .linkUrl("/student/complaints")
                    .read(false)
                    .build());
        }

        if (shortageStudent != null) {
            notificationRepository.save(Notification.builder()
                    .user(shortageStudent.getUser())
                    .title("CRITICAL: Attendance Shortage Alert (40.0%)")
                    .message("Your attendance in CS501 is currently 40.0%, which is below the mandatory 75% threshold.")
                    .type(Notification.NotificationType.ATTENDANCE_WARNING)
                    .linkUrl("/student/attendance")
                    .read(false)
                    .build());
        }

        // Admin Notifications
        notificationRepository.save(Notification.builder()
                .user(adminUser)
                .title("New Grievance Ticket (#TKT-2)")
                .message("Rahul Verma (STU-2026-001) reported Low Wi-Fi signal in Library 2nd floor reading hall.")
                .type(Notification.NotificationType.COMPLAINT)
                .linkUrl("/admin/complaints")
                .read(false)
                .build());

        // Faculty Notifications
        if (faculty1.getUser() != null && firstStudent != null) {
            notificationRepository.save(Notification.builder()
                    .user(faculty1.getUser())
                    .title("New Assignment Submission")
                    .message(firstStudent.getName() + " (" + firstStudent.getStudentId() + ") submitted coursework for Process Scheduling.")
                    .type(Notification.NotificationType.ASSIGNMENT)
                    .linkUrl("/faculty/assignments/" + assignment1.getId() + "/submissions")
                    .read(false)
                    .build());
        }

        log.info("Smart Campus demo dataset initialized successfully with 1 Admin, 3 Faculty, 11 Students, 6 Subjects, Attendance, Marks, Timetable, Notices, and Events.");
    }
}
