package nibm.project.campus_office.views.payments;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.views.MainLayout;

@Route(value = "payments", layout = MainLayout.class)
@PageTitle("Payments | Diploma CRM")
@PermitAll
public class PaymentListView extends VerticalLayout {
    public PaymentListView() {
        add(new H2("Payments"), new Span("Payment management coming soon"));
    }
}