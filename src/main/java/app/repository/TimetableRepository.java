package app.repository;

import app.entity.Department;
import app.entity.Faculty;
import app.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByDepartmentAndYearAndSection(Department department, Integer year, String section);
    List<Timetable> findByFaculty(Faculty faculty);
    List<Timetable> findByDepartment(Department department);
    List<Timetable> findByDayOfWeek(Timetable.DayOfWeek dayOfWeek);

    @Query("SELECT t FROM Timetable t WHERE t.dayOfWeek = :dayOfWeek AND t.faculty = :faculty " +
           "AND t.startTime < :endTime AND t.endTime > :startTime " +
           "AND (:excludeId IS NULL OR t.id <> :excludeId)")
    List<Timetable> findFacultyConflicts(@Param("dayOfWeek") Timetable.DayOfWeek dayOfWeek,
                                         @Param("faculty") Faculty faculty,
                                         @Param("startTime") LocalTime startTime,
                                         @Param("endTime") LocalTime endTime,
                                         @Param("excludeId") Long excludeId);

    @Query("SELECT t FROM Timetable t WHERE t.dayOfWeek = :dayOfWeek AND LOWER(t.room) = LOWER(:room) " +
           "AND t.startTime < :endTime AND t.endTime > :startTime " +
           "AND (:excludeId IS NULL OR t.id <> :excludeId)")
    List<Timetable> findRoomConflicts(@Param("dayOfWeek") Timetable.DayOfWeek dayOfWeek,
                                      @Param("room") String room,
                                      @Param("startTime") LocalTime startTime,
                                      @Param("endTime") LocalTime endTime,
                                      @Param("excludeId") Long excludeId);

    @Query("SELECT t FROM Timetable t WHERE t.dayOfWeek = :dayOfWeek AND t.department = :department " +
           "AND t.year = :year AND UPPER(t.section) = UPPER(:section) " +
           "AND t.startTime < :endTime AND t.endTime > :startTime " +
           "AND (:excludeId IS NULL OR t.id <> :excludeId)")
    List<Timetable> findSectionConflicts(@Param("dayOfWeek") Timetable.DayOfWeek dayOfWeek,
                                         @Param("department") Department department,
                                         @Param("year") Integer year,
                                         @Param("section") String section,
                                         @Param("startTime") LocalTime startTime,
                                         @Param("endTime") LocalTime endTime,
                                         @Param("excludeId") Long excludeId);
}

