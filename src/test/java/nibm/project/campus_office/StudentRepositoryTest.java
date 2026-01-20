package nibm.project.campus_office;

import lombok.extern.slf4j.Slf4j;
import nibm.project.campus_office.entity.Student;
import nibm.project.campus_office.enums.StudentStatus;
import nibm.project.campus_office.repository.StudentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class StudentRepositoryTest {

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("CREATE - Should save student successfully")
    void shouldCreateStudent() {
        Student student = Student.builder()
                .firstName("Satoru")
                .lastName("Gojo")
                .email("gojo@nibm.lk")
                .enrollmentDate(LocalDate.now())
                .status(StudentStatus.ACTIVE)
                .build();

        Student saved = studentRepository.save(student);

        log.info(String.valueOf(saved.getId()));
        log.info("Email - {}", saved.getEmail());
        log.info(String.valueOf(saved.getCreatedAt()));
        log.info("✓ CREATE test passed - Student ID: {}", saved.getId());
    }

    @Test
    @DisplayName("READ - Should find student by ID")
    void shouldFindStudentById() {
        Student student = Student.builder()
                .firstName("Yuta")
                .lastName("Okkotsu")
                .email("gojo@nibm.lk")
                .enrollmentDate(LocalDate.now())
                .status(StudentStatus.ACTIVE)
                .build();

        Student saved = studentRepository.save(student);

        Optional<Student> found = studentRepository.findById(saved.getId());

        log.info(String.valueOf(found.isPresent()));
        log.info("Email  - {}", found.get().getEmail());
        log.info("✓ READ test passed - Found: {}", found.get().getFirstName());
    }
}
