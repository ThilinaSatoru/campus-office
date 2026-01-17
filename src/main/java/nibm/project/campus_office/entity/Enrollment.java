package nibm.project.campus_office.entity;

import jakarta.persistence.*;
import lombok.*;
import nibm.project.campus_office.enums.EnrollmentStatus;

import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Enrollment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    private LocalDate enrollmentDate;
    private LocalDate completionDate;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status;

    private Double grade;

    @Column(length = 1000)
    private String feedback;
}