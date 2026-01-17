package nibm.project.campus_office.repository;

import nibm.project.campus_office.entity.Student;
import nibm.project.campus_office.enums.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByStatus(StudentStatus status);
}