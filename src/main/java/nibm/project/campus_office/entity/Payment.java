package nibm.project.campus_office.entity;

import jakarta.persistence.*;
import lombok.*;
import nibm.project.campus_office.enums.PaymentMethod;
import nibm.project.campus_office.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Column(nullable = false)
    private BigDecimal amount;

    private LocalDate paymentDate;
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentMethod method;

    private String transactionId;

    @Column(length = 500)
    private String notes;
}
