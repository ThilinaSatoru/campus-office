package nibm.project.campus_office.service;

import lombok.RequiredArgsConstructor;
import nibm.project.campus_office.entity.Course;
import nibm.project.campus_office.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Transactional
    public Course save(Course enrollment) {
        return courseRepository.save(enrollment);
    }

    @Transactional
    public void delete(Course enrollment) {
        courseRepository.delete(enrollment);
    }
}
