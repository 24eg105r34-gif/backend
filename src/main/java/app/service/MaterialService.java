package app.service;

import app.entity.Faculty;
import app.entity.Material;
import app.entity.Student;
import app.entity.Subject;
import app.repository.MaterialRepository;
import app.repository.SubjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class MaterialService {

    private final MaterialRepository materialRepository;
    private final SubjectRepository subjectRepository;

    public MaterialService(MaterialRepository materialRepository, SubjectRepository subjectRepository) {
        this.materialRepository = materialRepository;
        this.subjectRepository = subjectRepository;
    }

    public List<Material> getAllMaterials() {
        return materialRepository.findAllByOrderByUploadDateDesc();
    }

    public List<Material> getMaterialsByFaculty(Faculty faculty) {
        return materialRepository.findByFaculty(faculty);
    }

    public List<Material> getMaterialsBySubject(Subject subject) {
        return materialRepository.findBySubject(subject);
    }

    public List<Material> getMaterialsForStudent(Student student) {
        List<Subject> subjects = subjectRepository.findByDepartmentAndSemester(student.getDepartment(), student.getSemester());
        if (subjects.isEmpty()) {
            return materialRepository.findAllByOrderByUploadDateDesc();
        }
        return materialRepository.findBySubjectIn(subjects);
    }

    public Optional<Material> getMaterialById(Long id) {
        return materialRepository.findById(id);
    }

    public Material uploadMaterial(String title, String description, Subject subject, Faculty faculty, String fileName, String filePath, String fileType, Long fileSize) {
        Material material = Material.builder()
                .title(title)
                .description(description)
                .subject(subject)
                .faculty(faculty)
                .fileName(fileName)
                .filePath(filePath)
                .fileType(fileType)
                .fileSize(fileSize)
                .uploadDate(LocalDateTime.now())
                .build();
        return materialRepository.save(material);
    }

    public void deleteMaterial(Long id) {
        materialRepository.deleteById(id);
    }
}
