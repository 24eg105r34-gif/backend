package app.service;

import app.entity.*;
import app.repository.NotificationRepository;
import app.repository.StudentRepository;
import app.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final StudentRepository studentRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               StudentRepository studentRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
    }

    public Notification sendNotification(User user, String title, String message, Notification.NotificationType type, String linkUrl) {
        if (user == null) return null;
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .linkUrl(linkUrl)
                .read(false)
                .createdAt(LocalDateTime.now())
                .build();
        return notificationRepository.save(notification);
    }

    public Notification notifyUser(User user, String title, String message, Notification.NotificationType type, String linkUrl) {
        return sendNotification(user, title, message, type, linkUrl);
    }

    public void notifyAdmins(String title, String message, Notification.NotificationType type, String linkUrl) {
        List<User> admins = userRepository.findByRole(Role.ROLE_ADMIN);
        List<Notification> notifications = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (User admin : admins) {
            notifications.add(Notification.builder()
                    .user(admin)
                    .title(title)
                    .message(message)
                    .type(type)
                    .linkUrl(linkUrl)
                    .read(false)
                    .createdAt(now)
                    .build());
        }
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }
    }

    public void notifyAllStudents(String title, String message, Notification.NotificationType type, String linkUrl) {
        List<User> students = userRepository.findByRole(Role.ROLE_STUDENT);
        List<Notification> notifications = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (User studentUser : students) {
            notifications.add(Notification.builder()
                    .user(studentUser)
                    .title(title)
                    .message(message)
                    .type(type)
                    .linkUrl(linkUrl)
                    .read(false)
                    .createdAt(now)
                    .build());
        }
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }
    }

    public void notifyStudents(List<Student> students, String title, String message, Notification.NotificationType type, String linkUrl) {
        if (students == null || students.isEmpty()) return;
        List<Notification> notifications = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Student s : students) {
            if (s.getUser() != null) {
                notifications.add(Notification.builder()
                        .user(s.getUser())
                        .title(title)
                        .message(message)
                        .type(type)
                        .linkUrl(linkUrl)
                        .read(false)
                        .createdAt(now)
                        .build());
            }
        }
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }
    }

    public void notifySubjectStudents(Subject subject, String title, String message, Notification.NotificationType type, String linkUrl) {
        if (subject == null) return;
        List<Student> students = studentRepository.findByDepartmentAndSemester(subject.getDepartment(), subject.getSemester());
        notifyStudents(students, title, message, type, linkUrl);
    }

    public void notifyDepartmentStudents(Department department, String title, String message, Notification.NotificationType type, String linkUrl) {
        if (department == null) return;
        List<Student> students = studentRepository.findByDepartment(department);
        notifyStudents(students, title, message, type, linkUrl);
    }

    public List<Notification> getNotificationsForUser(User user) {
        if (user == null) return List.of();
        return notificationRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public List<Notification> getRecentNotifications(User user) {
        if (user == null) return List.of();
        return notificationRepository.findTop5ByUserOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(User user) {
        if (user == null) return 0;
        return notificationRepository.countByUserAndReadFalse(user);
    }

    public void markAllAsRead(User user) {
        if (user == null) return;
        List<Notification> notifications = notificationRepository.findByUserOrderByCreatedAtDesc(user);
        for (Notification n : notifications) {
            n.setRead(true);
        }
        notificationRepository.saveAll(notifications);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(n -> {
            n.setRead(true);
            notificationRepository.save(n);
        });
    }
}
