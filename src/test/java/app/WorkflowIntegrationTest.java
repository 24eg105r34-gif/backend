package app;

import app.entity.*;
import app.repository.*;
import app.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class WorkflowIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ComplaintService complaintService;

    @Autowired
    private CampusEventService eventService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private MarkService markService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private TimetableService timetableService;

    @Test
    @DisplayName("Workflow 1: Notification Broadcast & Individual Mark as Read")
    void testNotificationServiceWorkflow() {
        User admin = userRepository.findByUsername("admin@campus.edu").orElseThrow();
        long initialUnread = notificationService.getUnreadCount(admin);

        // 1. Send direct notification
        Notification notif = notificationService.sendNotification(
                admin,
                "Test Alert",
                "Direct notification message",
                Notification.NotificationType.ACADEMIC,
                "/admin/dashboard"
        );
        assertNotNull(notif);
        assertNotNull(notif.getId());
        assertFalse(notif.isRead());

        assertEquals(initialUnread + 1, notificationService.getUnreadCount(admin));

        // 2. Mark single as read
        notificationService.markAsRead(notif.getId());
        Notification updated = notificationRepository.findById(notif.getId()).orElseThrow();
        assertTrue(updated.isRead());
        assertEquals(initialUnread, notificationService.getUnreadCount(admin));

        // 3. Test notifyAdmins broadcast
        notificationService.notifyAdmins("Broadcast Admin Alert", "Emergency maintenance announcement", Notification.NotificationType.NOTICE, "/admin/dashboard");
        List<Notification> adminNotifs = notificationService.getNotificationsForUser(admin);
        assertTrue(adminNotifs.stream().anyMatch(n -> n.getTitle().equals("Broadcast Admin Alert")));

        // 4. Test markAllAsRead
        notificationService.markAllAsRead(admin);
        assertEquals(0, notificationService.getUnreadCount(admin));
    }

    @Test
    @DisplayName("Workflow 2: Student Grievance Ticket Raising, Admin Alert & Resolution Notification")
    void testGrievanceRedressalWorkflow() {
        Student student = studentService.getAllStudents().get(0);
        User studentUser = student.getUser();
        assertNotNull(studentUser);

        User admin = userRepository.findByUsername("admin@campus.edu").orElseThrow();
        long adminUnreadBefore = notificationService.getUnreadCount(admin);

        // 1. Student raises complaint
        Complaint ticket = complaintService.raiseComplaint(
                student,
                Complaint.ComplaintCategory.Laboratory,
                "Broken Monitor in Lab 4",
                "Monitor on terminal 12 does not turn on."
        );
        assertNotNull(ticket.getId());
        assertEquals(Complaint.ComplaintStatus.PENDING, ticket.getStatus());

        // 2. Student received confirmation notification
        List<Notification> studentNotifs = notificationService.getNotificationsForUser(studentUser);
        assertTrue(studentNotifs.stream().anyMatch(n -> n.getTitle().contains("Grievance Ticket Created") && n.getTitle().contains("#TKT-" + ticket.getId())));

        // 3. Admin received ticket alert
        List<Notification> adminNotifs = notificationService.getNotificationsForUser(admin);
        assertTrue(adminNotifs.stream().anyMatch(n -> n.getTitle().contains("New Grievance Ticket") && n.getTitle().contains("#TKT-" + ticket.getId())));

        // 4. Admin updates ticket status to RESOLVED
        complaintService.updateComplaintStatus(ticket.getId(), Complaint.ComplaintStatus.RESOLVED, "Hardware team replaced power cable.");
        Complaint resolvedTicket = complaintService.getComplaintById(ticket.getId()).orElseThrow();
        assertEquals(Complaint.ComplaintStatus.RESOLVED, resolvedTicket.getStatus());
        assertNotNull(resolvedTicket.getResolvedAt());
        assertEquals("Hardware team replaced power cable.", resolvedTicket.getAdminResponse());

        // 5. Student received resolution notification
        List<Notification> studentNotifsAfter = notificationService.getNotificationsForUser(studentUser);
        assertTrue(studentNotifsAfter.stream().anyMatch(n -> n.getTitle().contains("Grievance Update") && n.getTitle().contains("RESOLVED")));
    }

    @Test
    @DisplayName("Workflow 3: Campus Event Publishing, Registration Pass & Cancellation")
    void testCampusEventWorkflow() {
        Student student = studentService.getAllStudents().get(0);
        User studentUser = student.getUser();

        // 1. Create Event
        CampusEvent event = CampusEvent.builder()
                .title("Robotics Hackathon 2026")
                .description("48-hour hardware and embedded systems coding marathon")
                .eventDate(LocalDate.now().plusDays(20))
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(18, 0))
                .location("Robotics Lab A")
                .organizer("Robotics Club")
                .capacity(50)
                .registeredCount(0)
                .registrationDeadline(LocalDate.now().plusDays(15))
                .build();
        CampusEvent savedEvent = eventService.createEvent(event);
        assertNotNull(savedEvent.getId());

        // Verify broadcast notification sent to student
        List<Notification> notifs = notificationService.getNotificationsForUser(studentUser);
        assertTrue(notifs.stream().anyMatch(n -> n.getTitle().contains("Robotics Hackathon 2026")));

        // 2. Student registers for event
        EventRegistration reg = eventService.registerStudentForEvent(savedEvent.getId(), student);
        assertNotNull(reg.getId());
        assertEquals(1, savedEvent.getRegisteredCount());
        assertTrue(eventService.isStudentRegistered(savedEvent, student));

        // Verify pass confirmation notification
        List<Notification> notifs2 = notificationService.getNotificationsForUser(studentUser);
        assertTrue(notifs2.stream().anyMatch(n -> n.getTitle().contains("Event Pass Confirmed") && n.getTitle().contains("Robotics Hackathon 2026")));

        // 3. Student cancels registration
        eventService.unregisterStudentFromEvent(savedEvent.getId(), student);
        assertFalse(eventService.isStudentRegistered(savedEvent, student));
        assertEquals(0, savedEvent.getRegisteredCount());

        // Verify cancellation notification
        List<Notification> notifs3 = notificationService.getNotificationsForUser(studentUser);
        assertTrue(notifs3.stream().anyMatch(n -> n.getTitle().contains("Event Registration Cancelled")));
    }

    @Test
    @DisplayName("Workflow 4: Academic Coursework Assignment, Submission & Grading")
    void testAssignmentWorkflow() {
        Faculty faculty = facultyService.getAllFaculty().get(0);
        Subject subject = subjectService.getSubjectsByFaculty(faculty).get(0);
        Student student = studentService.getStudentsByDepartment(subject.getDepartment()).get(0);
        User studentUser = student.getUser();

        // 1. Faculty posts assignment
        Assignment assignment = Assignment.builder()
                .title("Mini Project: Microservices Architecture")
                .description("Design and implement Dockerized REST APIs")
                .subject(subject)
                .faculty(faculty)
                .deadline(LocalDateTime.now().plusDays(7))
                .maxMarks(50.0)
                .build();
        Assignment savedAssignment = assignmentService.createAssignment(assignment);
        assertNotNull(savedAssignment.getId());

        // 2. Student submits assignment
        AssignmentSubmission submission = assignmentService.submitAssignment(
                savedAssignment.getId(),
                student,
                "GitHub Repository URL: https://github.com/campus/microservices-project",
                "/submissions/project.zip"
        );
        assertNotNull(submission.getId());
        assertEquals(AssignmentSubmission.SubmissionStatus.SUBMITTED, submission.getStatus());

        // 3. Faculty grades submission
        AssignmentSubmission graded = assignmentService.gradeSubmission(submission.getId(), 48.0, "Excellent modular architecture and comprehensive README.");
        assertEquals(AssignmentSubmission.SubmissionStatus.REVIEWED, graded.getStatus());
        assertEquals(48.0, graded.getMarksObtained());

        // Verify grade alert received by student
        List<Notification> studentNotifs = notificationService.getNotificationsForUser(studentUser);
        assertTrue(studentNotifs.stream().anyMatch(n -> n.getTitle().contains("Assignment Graded") && n.getTitle().contains("Mini Project")));
    }

    @Test
    @DisplayName("Workflow 5: Attendance Recording & Shortage Alert Safeguard")
    void testAttendanceShortageWorkflow() {
        Faculty faculty = facultyService.getAllFaculty().get(0);
        Subject subject = subjectService.getSubjectsByFaculty(faculty).get(0);
        Student student = studentService.getStudentsByDepartment(subject.getDepartment()).get(0);
        User studentUser = student.getUser();

        // Mark attendance as ABSENT for 5 consecutive sessions to trigger shortage
        for (int i = 1; i <= 5; i++) {
            attendanceService.markAttendance(
                    subject.getId(),
                    faculty.getId(),
                    LocalDate.now().minusDays(i + 10),
                    Map.of(student.getId(), Attendance.AttendanceStatus.ABSENT)
            );
        }

        // Verify attendance warning notification was dispatched
        List<Notification> studentNotifs = notificationService.getNotificationsForUser(studentUser);
        assertTrue(studentNotifs.stream().anyMatch(n -> n.getType() == Notification.NotificationType.ATTENDANCE_WARNING));
    }

    @Test
    @DisplayName("Workflow 6: Timetable Conflict Prevention Engine (Faculty, Room, and Section Overlaps)")
    void testTimetableConflictPrevention() {
        Faculty faculty1 = facultyService.getAllFaculty().get(0);
        Faculty faculty2 = facultyService.getAllFaculty().size() > 1 ? facultyService.getAllFaculty().get(1) : faculty1;
        Subject subject = subjectService.getAllSubjects().get(0);
        Department dept = subject.getDepartment();

        // 1. Create a base timetable entry on SATURDAY: 09:00 to 10:00
        Timetable base = Timetable.builder()
                .dayOfWeek(Timetable.DayOfWeek.SATURDAY)
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .subject(subject)
                .faculty(faculty1)
                .room("LH-101")
                .department(dept)
                .year(2)
                .section("A")
                .build();
        Timetable savedBase = timetableService.saveTimetable(base);
        assertNotNull(savedBase.getId());

        // 2. Test Faculty Conflict: Same faculty overlapping 09:30 - 10:30 on SATURDAY
        Timetable facultyConflict = Timetable.builder()
                .dayOfWeek(Timetable.DayOfWeek.SATURDAY)
                .startTime(LocalTime.of(9, 30))
                .endTime(LocalTime.of(10, 30))
                .subject(subject)
                .faculty(faculty1)
                .room("LH-202")
                .department(dept)
                .year(3)
                .section("B")
                .build();
        IllegalStateException ex1 = assertThrows(IllegalStateException.class, () -> timetableService.saveTimetable(facultyConflict));
        assertTrue(ex1.getMessage().contains("Faculty Conflict"));

        // 3. Test Room Conflict: Different faculty, same room LH-101 overlapping 09:30 - 10:30 on SATURDAY
        Timetable roomConflict = Timetable.builder()
                .dayOfWeek(Timetable.DayOfWeek.SATURDAY)
                .startTime(LocalTime.of(9, 30))
                .endTime(LocalTime.of(10, 30))
                .subject(subject)
                .faculty(faculty2)
                .room("LH-101")
                .department(dept)
                .year(3)
                .section("B")
                .build();
        IllegalStateException ex2 = assertThrows(IllegalStateException.class, () -> timetableService.saveTimetable(roomConflict));
        assertTrue(ex2.getMessage().contains("Room Conflict"));

        // 4. Test Class/Section Conflict: Same Department, Year 2, Section A overlapping 09:30 - 10:30 on SATURDAY
        Timetable classConflict = Timetable.builder()
                .dayOfWeek(Timetable.DayOfWeek.SATURDAY)
                .startTime(LocalTime.of(9, 30))
                .endTime(LocalTime.of(10, 30))
                .subject(subject)
                .faculty(faculty2)
                .room("LH-303")
                .department(dept)
                .year(2)
                .section("A")
                .build();
        IllegalStateException ex3 = assertThrows(IllegalStateException.class, () -> timetableService.saveTimetable(classConflict));
        assertTrue(ex3.getMessage().contains("Class Conflict"));

        // 5. Test Non-Conflicting Slot: SATURDAY 10:00 to 11:00 (back-to-back, no overlap)
        Timetable validNext = Timetable.builder()
                .dayOfWeek(Timetable.DayOfWeek.SATURDAY)
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .subject(subject)
                .faculty(faculty1)
                .room("LH-101")
                .department(dept)
                .year(2)
                .section("A")
                .build();
        Timetable savedValid = timetableService.saveTimetable(validNext);
        assertNotNull(savedValid.getId());
    }

}
