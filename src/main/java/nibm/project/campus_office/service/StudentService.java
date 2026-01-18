package nibm.project.campus_office.service;

import lombok.RequiredArgsConstructor;
import nibm.project.campus_office.entity.Student;
import nibm.project.campus_office.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    @Transactional(readOnly = true)
    public List<Student> findAll() {
        return studentRepository.findAll();
    }

    @Transactional
    public Student save(Student enrollment) {
        return studentRepository.save(enrollment);
    }

    @Transactional
    public void delete(Student enrollment) {
        studentRepository.delete(enrollment);
    }
}
