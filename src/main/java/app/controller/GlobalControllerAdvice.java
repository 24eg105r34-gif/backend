package app.controller;

import app.entity.Faculty;
import app.entity.Student;
import app.entity.User;
import app.service.FacultyService;
import app.service.NotificationService;
import app.service.StudentService;
import app.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

import java.util.Optional;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserService userService;
    private final StudentService studentService;
    private final FacultyService facultyService;
    private final NotificationService notificationService;

    public GlobalControllerAdvice(UserService userService,
                                  StudentService studentService,
                                  FacultyService facultyService,
                                  NotificationService notificationService) {
        this.userService = userService;
        this.studentService = studentService;
        this.facultyService = facultyService;
        this.notificationService = notificationService;
    }

    @ModelAttribute
    public void addGlobalAttributes(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String username = authentication.getName();
            Optional<User> userOpt = userService.findByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                model.addAttribute("currentUser", user);
                model.addAttribute("unreadNotificationCount", notificationService.getUnreadCount(user));

                if (user.getRole() == app.entity.Role.ROLE_STUDENT) {
                    studentService.getStudentByUser(user).ifPresent(s -> model.addAttribute("currentStudent", s));
                } else if (user.getRole() == app.entity.Role.ROLE_FACULTY) {
                    facultyService.getFacultyByUser(user).ifPresent(f -> model.addAttribute("currentFaculty", f));
                }
            }
        }
    }
}
