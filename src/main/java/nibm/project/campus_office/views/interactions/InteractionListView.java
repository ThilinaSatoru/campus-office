package nibm.project.campus_office.views.interactions;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.views.MainLayout;

@Route(value = "interactions", layout = MainLayout.class)
@PageTitle("Interactions | Diploma CRM")
@PermitAll
public class InteractionListView extends VerticalLayout {
    public InteractionListView() {
        add(new H2("Interactions"), new Span("Interaction tracking coming soon"));
    }
}
