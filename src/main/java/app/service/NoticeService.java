package app.service;

import app.entity.Notice;
import app.entity.Notification;
import app.repository.NoticeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NotificationService notificationService;

    public NoticeService(NoticeRepository noticeRepository,
                         NotificationService notificationService) {
        this.noticeRepository = noticeRepository;
        this.notificationService = notificationService;
    }

    public List<Notice> getActiveNotices() {
        return noticeRepository.findByActiveTrueOrderByPublishedDateDesc();
    }

    public List<Notice> getRecentNotices() {
        return noticeRepository.findTop5ByActiveTrueOrderByPublishedDateDesc();
    }

    public List<Notice> getNoticesByCategory(Notice.NoticeCategory category) {
        return noticeRepository.findByCategoryAndActiveTrueOrderByPublishedDateDesc(category);
    }

    public Optional<Notice> getNoticeById(Long id) {
        return noticeRepository.findById(id);
    }

    public Notice publishNotice(String title, String description, Notice.NoticeCategory category, LocalDate expiryDate, String createdBy) {
        Notice notice = Notice.builder()
                .title(title)
                .description(description)
                .category(category)
                .publishedDate(LocalDate.now())
                .expiryDate(expiryDate)
                .createdBy(createdBy)
                .active(true)
                .build();
        Notice saved = noticeRepository.save(notice);

        // Broadcast to all campus students
        notificationService.notifyAllStudents(
                "Campus Circular: " + saved.getTitle(),
                "New [" + category + "] announcement published: " + saved.getDescription(),
                Notification.NotificationType.NOTICE,
                "/student/notices"
        );

        return saved;
    }

    public void deleteNotice(Long id) {
        noticeRepository.deleteById(id);
    }

    public long countActiveNotices() {
        return noticeRepository.findByActiveTrueOrderByPublishedDateDesc().size();
    }
}
