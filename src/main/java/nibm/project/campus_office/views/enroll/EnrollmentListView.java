package nibm.project.campus_office.views.enroll;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.views.MainLayout;

@Route(value = "enrollments", layout = MainLayout.class)
@PageTitle("Enrollments | Diploma CRM")
@PermitAll
public class EnrollmentListView extends VerticalLayout {
    public EnrollmentListView() {
        add(new H2("Enrollments"), new Span("Enrollment management coming soon"));
    }
}