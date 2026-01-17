package nibm.project.campus_office.repository;

import nibm.project.campus_office.entity.Enrollment;
import nibm.project.campus_office.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByStudent(Student student);
}