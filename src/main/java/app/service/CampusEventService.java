package app.service;

import app.entity.CampusEvent;
import app.entity.EventRegistration;
import app.entity.Notification;
import app.entity.Student;
import app.repository.CampusEventRepository;
import app.repository.EventRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CampusEventService {

    private final CampusEventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final NotificationService notificationService;

    public CampusEventService(CampusEventRepository eventRepository,
                              EventRegistrationRepository registrationRepository,
                              NotificationService notificationService) {
        this.eventRepository = eventRepository;
        this.registrationRepository = registrationRepository;
        this.notificationService = notificationService;
    }

    public List<CampusEvent> getAllEvents() {
        return eventRepository.findAllByOrderByEventDateDesc();
    }

    public List<CampusEvent> getUpcomingEvents() {
        return eventRepository.findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate.now());
    }

    public List<CampusEvent> getTopUpcomingEvents() {
        return eventRepository.findTop5ByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate.now());
    }

    public Optional<CampusEvent> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public CampusEvent createEvent(CampusEvent event) {
        if (event.getRegistrationDeadline().isAfter(event.getEventDate())) {
            throw new IllegalArgumentException("Registration deadline cannot be after the event date.");
        }
        CampusEvent saved = eventRepository.save(event);

        // Notify all students about the newly organized event
        notificationService.notifyAllStudents(
                "New Event: " + saved.getTitle(),
                "Organized by " + saved.getOrganizer() + " on " + saved.getEventDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + " at " + saved.getLocation() + ". Register before " + saved.getRegistrationDeadline() + "!",
                Notification.NotificationType.EVENT,
                "/student/events"
        );

        return saved;
    }

    public EventRegistration registerStudentForEvent(Long eventId, Student student) {
        CampusEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        if (LocalDate.now().isAfter(event.getRegistrationDeadline())) {
            throw new IllegalStateException("Registration deadline has passed for this event.");
        }

        if (event.isFull()) {
            throw new IllegalStateException("Event has reached its maximum seat capacity (" + event.getCapacity() + ").");
        }

        if (registrationRepository.existsByCampusEventAndStudent(event, student)) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        EventRegistration registration = EventRegistration.builder()
                .campusEvent(event)
                .student(student)
                .build();

        event.setRegisteredCount(event.getRegisteredCount() + 1);
        eventRepository.save(event);

        EventRegistration saved = registrationRepository.save(registration);

        // Notify student about confirmed registration
        if (student.getUser() != null) {
            notificationService.sendNotification(
                    student.getUser(),
                    "Event Pass Confirmed: " + event.getTitle(),
                    "Your seat is confirmed for " + event.getTitle() + " on " + event.getEventDate() + " at " + event.getLocation() + ".",
                    Notification.NotificationType.EVENT,
                    "/student/events"
            );
        }

        return saved;
    }

    public void unregisterStudentFromEvent(Long eventId, Student student) {
        CampusEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));

        EventRegistration registration = registrationRepository.findByCampusEventAndStudent(event, student)
                .orElseThrow(() -> new IllegalStateException("You are not registered for this event."));

        registrationRepository.delete(registration);

        if (event.getRegisteredCount() > 0) {
            event.setRegisteredCount(event.getRegisteredCount() - 1);
            eventRepository.save(event);
        }

        if (student.getUser() != null) {
            notificationService.sendNotification(
                    student.getUser(),
                    "Event Registration Cancelled",
                    "Your registration for '" + event.getTitle() + "' has been successfully cancelled.",
                    Notification.NotificationType.EVENT,
                    "/student/events"
            );
        }
    }

    public boolean isStudentRegistered(CampusEvent event, Student student) {
        return registrationRepository.existsByCampusEventAndStudent(event, student);
    }

    public List<EventRegistration> getRegistrationsForEvent(CampusEvent event) {
        return registrationRepository.findByCampusEventOrderByRegistrationDateDesc(event);
    }

    public List<EventRegistration> getRegistrationsForEvent(Long eventId) {
        CampusEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + eventId));
        return registrationRepository.findByCampusEventOrderByRegistrationDateDesc(event);
    }

    public List<EventRegistration> getRegistrationsForStudent(Student student) {
        return registrationRepository.findByStudent(student);
    }

    public void deleteEvent(Long id) {
        eventRepository.deleteById(id);
    }

    public long countUpcomingEvents() {
        return eventRepository.findByEventDateGreaterThanEqualOrderByEventDateAsc(LocalDate.now()).size();
    }
}
