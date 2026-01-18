package nibm.project.campus_office.views.Dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.entity.Enrollment;
import nibm.project.campus_office.entity.Interaction;
import nibm.project.campus_office.entity.Payment;
import nibm.project.campus_office.enums.EnrollmentStatus;
import nibm.project.campus_office.enums.PaymentStatus;
import nibm.project.campus_office.enums.StudentStatus;
import nibm.project.campus_office.repository.*;
import nibm.project.campus_office.views.MainLayout;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | Diploma CRM")
@PermitAll
public class DashboardView extends VerticalLayout {

    private final StudentRepository studentRepo;
    private final CourseRepository courseRepo;
    private final EnrollmentRepository enrollmentRepo;
    private final PaymentRepository paymentRepo;
    private final InteractionRepository interactionRepo;

    public DashboardView(StudentRepository studentRepo, CourseRepository courseRepo,
                         EnrollmentRepository enrollmentRepo, PaymentRepository paymentRepo,
                         InteractionRepository interactionRepo) {
        this.studentRepo = studentRepo;
        this.courseRepo = courseRepo;
        this.enrollmentRepo = enrollmentRepo;
        this.paymentRepo = paymentRepo;
        this.interactionRepo = interactionRepo;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        H2 title = new H2("Welcome, " + username);

        add(
                title,
                createStatsLayout(),
                createSummaryCardsLayout(),
                createRecentActivitiesLayout()
        );
    }

    private Component createStatsLayout() {
        long activeStudents = studentRepo.findByStatus(StudentStatus.ACTIVE).size();
        long totalCourses = courseRepo.count();
        long activeEnrollments = enrollmentRepo.findAll().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.ENROLLED).count();

        BigDecimal pendingAmount = paymentRepo.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PENDING)
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        HorizontalLayout stats = new HorizontalLayout(
                createStatCard("Active Students", String.valueOf(activeStudents), VaadinIcon.USERS, "#2196F3"),
                createStatCard("Total Courses", String.valueOf(totalCourses), VaadinIcon.BOOK, "#4CAF50"),
                createStatCard("Active Enrollments", String.valueOf(activeEnrollments), VaadinIcon.DIPLOMA, "#FF9800"),
                createStatCard("Pending Amount", "Rs. " + pendingAmount, VaadinIcon.DOLLAR, "#F44336")
        );
        stats.setWidthFull();
        stats.setSpacing(true);
        return stats;
    }

    private Component createStatCard(String label, String value, VaadinIcon icon, String color) {
        VerticalLayout card = new VerticalLayout();
        card.setPadding(true);
        card.setSpacing(false);
        card.getStyle()
                .set("border", "1px solid #e0e0e0")
                .set("border-radius", "8px")
                .set("background", "#ffffff")
                .set("box-shadow", "0 2px 4px rgba(0,0,0,0.1)");

        Icon cardIcon = new Icon(icon);
        cardIcon.setSize("32px");
        cardIcon.getStyle().set("color", color);

        Span valueLabel = new Span(value);
        valueLabel.getStyle()
                .set("font-size", "24px")
                .set("font-weight", "bold")
                .set("color", color)
                .set("margin", "8px 0");

        Span labelSpan = new Span(label);
        labelSpan.getStyle()
                .set("color", "#666")
                .set("font-size", "14px");

        card.add(cardIcon, valueLabel, labelSpan);
        card.setAlignItems(FlexComponent.Alignment.START);
        return card;
    }

    private Component createSummaryCardsLayout() {
        HorizontalLayout summaryLayout = new HorizontalLayout();
        summaryLayout.setWidthFull();
        summaryLayout.setSpacing(true);

        summaryLayout.add(
                createEnrollmentSummary(),
                createPaymentSummary()
        );

        return summaryLayout;
    }

    private Component createEnrollmentSummary() {
        VerticalLayout container = new VerticalLayout();
        container.setPadding(true);
        container.getStyle()
                .set("border", "1px solid #e0e0e0")
                .set("border-radius", "8px")
                .set("background", "#ffffff")
                .set("flex", "1");

        H3 title = new H3("Enrollment Breakdown");
        title.getStyle().set("margin", "0 0 16px 0");

        Map<EnrollmentStatus, Long> enrollmentStats = enrollmentRepo.findAll().stream()
                .collect(Collectors.groupingBy(Enrollment::getStatus, Collectors.counting()));

        VerticalLayout statsList = new VerticalLayout();
        statsList.setPadding(false);
        statsList.setSpacing(true);

        enrollmentStats.forEach((status, count) -> {
            HorizontalLayout row = new HorizontalLayout();
            row.setWidthFull();
            row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            row.setAlignItems(FlexComponent.Alignment.CENTER);

            Span statusLabel = new Span(status.toString());
            statusLabel.getStyle().set("font-weight", "500");

            Span countBadge = new Span(count.toString());
            countBadge.getStyle()
                    .set("background", getStatusColor(status))
                    .set("color", "white")
                    .set("padding", "4px 12px")
                    .set("border-radius", "12px")
                    .set("font-weight", "bold");

            row.add(statusLabel, countBadge);
            statsList.add(row);
        });

        container.add(title, statsList);
        return container;
    }

    private Component createPaymentSummary() {
        VerticalLayout container = new VerticalLayout();
        container.setPadding(true);
        container.getStyle()
                .set("border", "1px solid #e0e0e0")
                .set("border-radius", "8px")
                .set("background", "#ffffff")
                .set("flex", "1");

        H3 title = new H3("Payment Breakdown");
        title.getStyle().set("margin", "0 0 16px 0");

        Map<PaymentStatus, Long> paymentStats = paymentRepo.findAll().stream()
                .collect(Collectors.groupingBy(Payment::getStatus, Collectors.counting()));

        Map<PaymentStatus, BigDecimal> paymentAmounts = paymentRepo.findAll().stream()
                .collect(Collectors.groupingBy(
                        Payment::getStatus,
                        Collectors.reducing(BigDecimal.ZERO, Payment::getAmount, BigDecimal::add)
                ));

        VerticalLayout statsList = new VerticalLayout();
        statsList.setPadding(false);
        statsList.setSpacing(true);

        paymentStats.forEach((status, count) -> {
            VerticalLayout row = new VerticalLayout();
            row.setPadding(false);
            row.setSpacing(false);

            HorizontalLayout header = new HorizontalLayout();
            header.setWidthFull();
            header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            header.setAlignItems(FlexComponent.Alignment.CENTER);

            Span statusLabel = new Span(status.toString());
            statusLabel.getStyle().set("font-weight", "500");

            Span countBadge = new Span(count.toString());
            countBadge.getStyle()
                    .set("background", getPaymentStatusColor(status))
                    .set("color", "white")
                    .set("padding", "4px 12px")
                    .set("border-radius", "12px")
                    .set("font-weight", "bold");

            header.add(statusLabel, countBadge);

            Span amount = new Span("Rs. " + paymentAmounts.get(status));
            amount.getStyle()
                    .set("color", "#666")
                    .set("font-size", "14px")
                    .set("margin-left", "4px");

            row.add(header, amount);
            statsList.add(row);
        });

        container.add(title, statsList);
        return container;
    }

    private Component createRecentActivitiesLayout() {
        HorizontalLayout activitiesLayout = new HorizontalLayout();
        activitiesLayout.setWidthFull();
        activitiesLayout.setSpacing(true);

        activitiesLayout.add(
                createRecentEnrollments(),
                createRecentPayments(),
                createRecentInteractions()
        );

        return activitiesLayout;
    }

    private Component createRecentEnrollments() {
        VerticalLayout container = new VerticalLayout();
        container.setPadding(true);
        container.getStyle()
                .set("border", "1px solid #e0e0e0")
                .set("border-radius", "8px")
                .set("background", "#ffffff")
                .set("flex", "1");

        H3 title = new H3("Recent Enrollments");
        title.getStyle().set("margin", "0 0 16px 0");

        Grid<Enrollment> grid = new Grid<>(Enrollment.class, false);
        grid.addColumn(e -> e.getStudent().getFirstName() + " " + e.getStudent().getLastName())
                .setHeader("Student").setAutoWidth(true);
        grid.addColumn(e -> e.getCourse().getTitle())
                .setHeader("Course").setAutoWidth(true);
        grid.addColumn(e -> e.getEnrollmentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .setHeader("Date").setAutoWidth(true);
        grid.addComponentColumn(e -> {
            Span badge = new Span(e.getStatus().toString());
            badge.getElement().getThemeList().add("badge");
            badge.getStyle()
                    .set("background", getStatusColor(e.getStatus()))
                    .set("color", "white")
                    .set("padding", "2px 8px")
                    .set("border-radius", "4px")
                    .set("font-size", "12px");
            return badge;
        }).setHeader("Status").setAutoWidth(true);

        List<Enrollment> recent = enrollmentRepo.findAll().stream()
                .sorted(Comparator.comparing(Enrollment::getEnrollmentDate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        grid.setItems(recent);
        grid.setHeight("300px");

        container.add(title, grid);
        return container;
    }

    private Component createRecentPayments() {
        VerticalLayout container = new VerticalLayout();
        container.setPadding(true);
        container.getStyle()
                .set("border", "1px solid #e0e0e0")
                .set("border-radius", "8px")
                .set("background", "#ffffff")
                .set("flex", "1");

        H3 title = new H3("Recent Payments");
        title.getStyle().set("margin", "0 0 16px 0");

        Grid<Payment> grid = new Grid<>(Payment.class, false);
        grid.addColumn(p -> p.getStudent().getFirstName() + " " + p.getStudent().getLastName())
                .setHeader("Student").setAutoWidth(true);
        grid.addColumn(p -> "Rs. " + p.getAmount())
                .setHeader("Amount").setAutoWidth(true);
        grid.addColumn(p -> p.getPaymentDate() != null ?
                        p.getPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "-")
                .setHeader("Date").setAutoWidth(true);
        grid.addComponentColumn(p -> {
            Span badge = new Span(p.getStatus().toString());
            badge.getStyle()
                    .set("background", getPaymentStatusColor(p.getStatus()))
                    .set("color", "white")
                    .set("padding", "2px 8px")
                    .set("border-radius", "4px")
                    .set("font-size", "12px");
            return badge;
        }).setHeader("Status").setAutoWidth(true);

        List<Payment> recent = paymentRepo.findAll().stream()
                .filter(p -> p.getPaymentDate() != null)
                .sorted(Comparator.comparing(Payment::getPaymentDate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        grid.setItems(recent);
        grid.setHeight("300px");

        container.add(title, grid);
        return container;
    }

    private Component createRecentInteractions() {
        VerticalLayout container = new VerticalLayout();
        container.setPadding(true);
        container.getStyle()
                .set("border", "1px solid #e0e0e0")
                .set("border-radius", "8px")
                .set("background", "#ffffff")
                .set("flex", "1");

        H3 title = new H3("Recent Interactions");
        title.getStyle().set("margin", "0 0 16px 0");

        Grid<Interaction> grid = new Grid<>(Interaction.class, false);
        grid.addColumn(i -> i.getStudent().getFirstName() + " " + i.getStudent().getLastName())
                .setHeader("Student").setAutoWidth(true);
        grid.addColumn(Interaction::getType)
                .setHeader("Type").setAutoWidth(true);
        grid.addColumn(Interaction::getSubject)
                .setHeader("Subject").setAutoWidth(true);
        grid.addColumn(i -> i.getInteractionDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
                .setHeader("Date").setAutoWidth(true);

        List<Interaction> recent = interactionRepo.findAll().stream()
                .sorted(Comparator.comparing(Interaction::getInteractionDate).reversed())
                .limit(5)
                .collect(Collectors.toList());

        grid.setItems(recent);
        grid.setHeight("300px");

        container.add(title, grid);
        return container;
    }

    private String getStatusColor(EnrollmentStatus status) {
        return switch (status) {
            case ENROLLED -> "#4CAF50";
            case COMPLETED -> "#2196F3";
            case WITHDRAWN -> "#F44336";
            case FAILED -> "#FF9800";
        };
    }

    private String getPaymentStatusColor(PaymentStatus status) {
        return switch (status) {
            case PAID -> "#4CAF50";
            case PENDING -> "#FF9800";
            case OVERDUE -> "#F44336";
            case REFUNDED -> "#9E9E9E";
        };
    }
}
