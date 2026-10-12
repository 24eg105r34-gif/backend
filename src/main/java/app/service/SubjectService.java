package app.service;

import app.entity.Department;
import app.entity.Faculty;
import app.entity.Subject;
import app.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Optional<Subject> getSubjectById(Long id) {
        return subjectRepository.findById(id);
    }

    public Optional<Subject> getSubjectByCode(String code) {
        return subjectRepository.findBySubjectCode(code);
    }

    public List<Subject> getSubjectsByFaculty(Faculty faculty) {
        return subjectRepository.findByFaculty(faculty);
    }

    public List<Subject> getSubjectsByDepartment(Department department) {
        return subjectRepository.findByDepartment(department);
    }

    public List<Subject> getSubjectsByDepartmentAndSemester(Department department, Integer semester) {
        return subjectRepository.findByDepartmentAndSemester(department, semester);
    }

    public Subject saveSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    public void assignFaculty(Long subjectId, Faculty faculty) {
        subjectRepository.findById(subjectId).ifPresent(subject -> {
            subject.setFaculty(faculty);
            subjectRepository.save(subject);
        });
    }

    public void deleteSubject(Long id) {
        subjectRepository.deleteById(id);
    }

    public long countSubjects() {
        return subjectRepository.count();
    }
}
