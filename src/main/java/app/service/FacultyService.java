package app.service;

import app.entity.Department;
import app.entity.Faculty;
import app.entity.Role;
import app.entity.User;
import app.repository.FacultyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final UserService userService;

    public FacultyService(FacultyRepository facultyRepository, UserService userService) {
        this.facultyRepository = facultyRepository;
        this.userService = userService;
    }

    public List<Faculty> getAllFaculty() {
        return facultyRepository.findAll();
    }

    public Optional<Faculty> getFacultyById(Long id) {
        return facultyRepository.findById(id);
    }

    public Optional<Faculty> getFacultyByEmail(String email) {
        return facultyRepository.findByEmail(email);
    }

    public Optional<Faculty> getFacultyByUser(User user) {
        return facultyRepository.findByUser(user);
    }

    public List<Faculty> getFacultyByDepartment(Department department) {
        return facultyRepository.findByDepartment(department);
    }

    public List<Faculty> searchFaculty(String query) {
        if (query == null || query.trim().isEmpty()) {
            return facultyRepository.findAll();
        }
        return facultyRepository.searchFaculty(query.trim());
    }

    public Faculty createFaculty(Faculty faculty, String rawPassword) {
        if (faculty.getUser() == null && !userService.existsByUsername(faculty.getEmail())) {
            User user = userService.createUser(faculty.getEmail(), rawPassword != null ? rawPassword : "faculty123", Role.ROLE_FACULTY, faculty.getName());
            faculty.setUser(user);
        }
        return facultyRepository.save(faculty);
    }

    public Faculty updateFaculty(Faculty faculty) {
        return facultyRepository.save(faculty);
    }

    public void deleteFaculty(Long id) {
        facultyRepository.deleteById(id);
    }

    public long countFaculty() {
        return facultyRepository.count();
    }
}
