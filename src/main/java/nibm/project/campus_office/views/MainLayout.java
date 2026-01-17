package nibm.project.campus_office.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
@PermitAll
public class MainLayout extends AppLayout {

    @Autowired
    private SecurityService securityService;

    public MainLayout() {
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Diploma CRM");
        logo.addClassNames("text-l", "m-m");

        Button logout = new Button("Logout", e -> securityService.logout());

        HorizontalLayout header = new HorizontalLayout(logo, logout);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames("py-0", "px-m");

        addToNavbar(header);
    }

    private void createDrawer() {
        addToDrawer(new VerticalLayout(
//                new RouterLink("Dashboard", DashboardView.class),
//                new RouterLink("Students", StudentListView.class),
//                new RouterLink("Courses", CourseListView.class),
//                new RouterLink("Instructors", InstructorListView.class),
//                new RouterLink("Payments", PaymentListView.class)
        ));
    }
}
