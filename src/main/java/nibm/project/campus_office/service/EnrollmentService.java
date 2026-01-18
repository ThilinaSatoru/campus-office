package nibm.project.campus_office.service;

import lombok.RequiredArgsConstructor;
import nibm.project.campus_office.entity.Enrollment;
import nibm.project.campus_office.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepo;

    @Transactional(readOnly = true)
    public List<Enrollment> findAll() {
        return enrollmentRepo.findAll();
    }

    @Transactional
    public Enrollment save(Enrollment enrollment) {
        return enrollmentRepo.save(enrollment);
    }

    @Transactional
    public void delete(Enrollment enrollment) {
        enrollmentRepo.delete(enrollment);
    }
}
