package app.service;

import app.entity.Department;
import app.entity.Faculty;
import app.entity.Student;
import app.entity.Timetable;
import app.repository.TimetableRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class TimetableService {

    private final TimetableRepository timetableRepository;

    public TimetableService(TimetableRepository timetableRepository) {
        this.timetableRepository = timetableRepository;
    }

    public List<Timetable> getAllTimetableEntries() {
        return timetableRepository.findAll();
    }

    public List<Timetable> getTimetableForStudent(Student student) {
        return timetableRepository.findByDepartmentAndYearAndSection(
                student.getDepartment(),
                student.getYear(),
                student.getSection()
        );
    }

    public List<Timetable> getTimetableForFaculty(Faculty faculty) {
        return timetableRepository.findByFaculty(faculty);
    }

    public Map<Timetable.DayOfWeek, List<Timetable>> getGroupedTimetableForStudent(Student student) {
        List<Timetable> entries = getTimetableForStudent(student);
        Map<Timetable.DayOfWeek, List<Timetable>> grouped = new TreeMap<>();
        for (Timetable.DayOfWeek day : Timetable.DayOfWeek.values()) {
            grouped.put(day, new ArrayList<>());
        }
        for (Timetable t : entries) {
            grouped.get(t.getDayOfWeek()).add(t);
        }
        for (List<Timetable> dayList : grouped.values()) {
            dayList.sort(Comparator.comparing(Timetable::getStartTime));
        }
        return grouped;
    }

    public Timetable saveTimetable(Timetable timetable) {
        if (timetable.getStartTime() == null || timetable.getEndTime() == null) {
            throw new IllegalArgumentException("Start time and end time are required.");
        }
        if (timetable.getStartTime().isAfter(timetable.getEndTime()) || timetable.getStartTime().equals(timetable.getEndTime())) {
            throw new IllegalArgumentException("Start time must be strictly before end time.");
        }

        // 1. Conflict Check: Same Faculty scheduled during overlapping timeslot
        List<Timetable> facultyConflicts = timetableRepository.findFacultyConflicts(
                timetable.getDayOfWeek(),
                timetable.getFaculty(),
                timetable.getStartTime(),
                timetable.getEndTime(),
                timetable.getId()
        );
        if (!facultyConflicts.isEmpty()) {
            Timetable c = facultyConflicts.get(0);
            throw new IllegalStateException(String.format(
                    "Faculty Conflict: %s is already assigned to %s (%s - %s) on %s in Room %s.",
                    timetable.getFaculty().getName(),
                    c.getSubject().getSubjectName(),
                    c.getStartTime(),
                    c.getEndTime(),
                    timetable.getDayOfWeek(),
                    c.getRoom()
            ));
        }

        // 2. Conflict Check: Same Room booked during overlapping timeslot
        List<Timetable> roomConflicts = timetableRepository.findRoomConflicts(
                timetable.getDayOfWeek(),
                timetable.getRoom(),
                timetable.getStartTime(),
                timetable.getEndTime(),
                timetable.getId()
        );
        if (!roomConflicts.isEmpty()) {
            Timetable c = roomConflicts.get(0);
            throw new IllegalStateException(String.format(
                    "Room Conflict: Room %s is already reserved for %s (%s - %s) on %s.",
                    timetable.getRoom(),
                    c.getSubject().getSubjectName(),
                    c.getStartTime(),
                    c.getEndTime(),
                    timetable.getDayOfWeek()
            ));
        }

        // 3. Conflict Check: Same Department, Year & Section having overlapping class
        List<Timetable> sectionConflicts = timetableRepository.findSectionConflicts(
                timetable.getDayOfWeek(),
                timetable.getDepartment(),
                timetable.getYear(),
                timetable.getSection(),
                timetable.getStartTime(),
                timetable.getEndTime(),
                timetable.getId()
        );
        if (!sectionConflicts.isEmpty()) {
            Timetable c = sectionConflicts.get(0);
            throw new IllegalStateException(String.format(
                    "Class Conflict: %s Year %d Section %s already has %s (%s - %s) on %s.",
                    timetable.getDepartment().getDepartmentCode(),
                    timetable.getYear(),
                    timetable.getSection(),
                    c.getSubject().getSubjectName(),
                    c.getStartTime(),
                    c.getEndTime(),
                    timetable.getDayOfWeek()
            ));
        }

        return timetableRepository.save(timetable);
    }


    public void deleteTimetable(Long id) {
        timetableRepository.deleteById(id);
    }
}
