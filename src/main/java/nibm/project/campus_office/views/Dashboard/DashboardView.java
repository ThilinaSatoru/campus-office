package nibm.project.campus_office.views.Dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.enums.EnrollmentStatus;
import nibm.project.campus_office.enums.PaymentStatus;
import nibm.project.campus_office.enums.StudentStatus;
import nibm.project.campus_office.repository.CourseRepository;
import nibm.project.campus_office.repository.EnrollmentRepository;
import nibm.project.campus_office.repository.PaymentRepository;
import nibm.project.campus_office.repository.StudentRepository;
import nibm.project.campus_office.views.MainLayout;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Diploma CRM")
@PermitAll
public class DashboardView extends VerticalLayout {

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final PaymentRepository paymentRepo;

    public DashboardView(StudentRepository studentRepo, CourseRepository courseRepo,
                         EnrollmentRepository enrollmentRepo, PaymentRepository paymentRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.paymentRepo = paymentRepo;

        setSizeFull();
        setPadding(true);

        H2 title = new H2("Dashboard");
        add(title, createStatsLayout(), createRecentActivity());
    }

    private Component createStatsLayout() {
        long activeStudents = studentRepo.findByStatus(StudentStatus.ACTIVE).size();
        long totalCourses = courseRepo.count();
        long activeEnrollments = enrollmentRepo.findAll().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED).count();
        long pendingPayments = paymentRepo.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING).count();

        HorizontalLayout stats = new HorizontalLayout(
                createStatCard("Active Students", String.valueOf(activeStudents), "blue"),
                createStatCard("Total Courses", String.valueOf(totalCourses), "green"),
                createStatCard("Active Enrollments", String.valueOf(activeEnrollments), "orange"),
                createStatCard("Pending Payments", String.valueOf(pendingPayments), "red")
        );
        stats.setWidthFull();
        return stats;
    }

    private Component createStatCard(String label, String value, String color) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.getStyle().set("border", "1px solid #ddd")
                .set("border-radius", "8px")
                .set("background", "#f9f9f9");

        H1 valueLabel = new H1(value);
        valueLabel.getStyle().set("color", color).set("margin", "0");

        Span labelSpan = new Span(label);
        labelSpan.getStyle().set("color", "#666");

        card.add(valueLabel, labelSpan);
        return card;
    }

    private Component createRecentActivity() {
        H3 title = new H3("Recent Activity");
        Span placeholder = new Span("Recent enrollments and interactions will appear here");
        return new VerticalLayout(title, placeholder);
    }
}
