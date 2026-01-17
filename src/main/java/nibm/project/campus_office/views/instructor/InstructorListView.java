package nibm.project.campus_office.views.instructor;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.views.MainLayout;

@Route(value = "instructors", layout = MainLayout.class)
@PageTitle("Instructors | Diploma CRM")
@PermitAll
public class InstructorListView extends VerticalLayout {
    public InstructorListView() {
        add(new H2("Instructors"), new Span("Instructor management coming soon"));
    }
}
