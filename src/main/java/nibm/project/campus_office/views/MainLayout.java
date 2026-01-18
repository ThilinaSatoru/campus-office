package nibm.project.campus_office.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.security.SecurityService;
import nibm.project.campus_office.views.Dashboard.DashboardView;
import nibm.project.campus_office.views.course.CourseListView;
import nibm.project.campus_office.views.enroll.EnrollmentListView;
import nibm.project.campus_office.views.instructor.InstructorListView;
import nibm.project.campus_office.views.interactions.InteractionListView;
import nibm.project.campus_office.views.payments.PaymentListView;
import nibm.project.campus_office.views.sudent.StudentListView;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@PermitAll
public class MainLayout extends AppLayout {

    private final SecurityService securityService;

    public MainLayout(SecurityService securityService) {
        this.securityService = securityService;
        createHeader();
        createDrawer();
    }

    private void createHeader() {
        H1 logo = new H1("Diploma CRM");
        logo.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.Margin.MEDIUM);

        // Get current user details
        String username = "Guest";
        String role = "USER";

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
            // Extract role (remove "ROLE_" prefix if present)
            role = ((UserDetails) principal).getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority().replace("ROLE_", ""))
                    .orElse("USER");
        }

        // User info section
        Avatar avatar = new Avatar();
        avatar.setImage(null); // No image, will show initials
        avatar.setName(username);
        avatar.setColorIndex(username.hashCode() % 7); // Generate color based on username

        Span usernameSpan = new Span(username);
        usernameSpan.addClassNames(LumoUtility.FontWeight.SEMIBOLD);

        Span roleSpan = new Span(role);
        roleSpan.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.TextColor.SECONDARY);

        VerticalLayout userInfo = new VerticalLayout(usernameSpan, roleSpan);
        userInfo.setSpacing(false);
        userInfo.setPadding(false);
        userInfo.addClassNames(LumoUtility.Gap.XSMALL);

        Button logout = new Button("Logout", VaadinIcon.SIGN_OUT.create());
        logout.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logout.addClickListener(e -> securityService.logout());

        HorizontalLayout userSection = new HorizontalLayout(avatar, userInfo, logout);
        userSection.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        userSection.setSpacing(true);
        userSection.addClassNames(LumoUtility.Gap.MEDIUM);

        DrawerToggle toggle = new DrawerToggle();

        HorizontalLayout header = new HorizontalLayout(toggle, logo, userSection);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.expand(logo);
        header.setWidthFull();
        header.addClassNames(
                LumoUtility.Padding.Vertical.NONE,
                LumoUtility.Padding.Horizontal.MEDIUM
        );

        addToNavbar(header);
    }

    private void createDrawer() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create()));
        nav.addItem(new SideNavItem("Students", StudentListView.class, VaadinIcon.ACADEMY_CAP.create()));
        nav.addItem(new SideNavItem("Courses", CourseListView.class, VaadinIcon.BOOK.create()));
        nav.addItem(new SideNavItem("Instructors", InstructorListView.class, VaadinIcon.USER_STAR.create()));
        nav.addItem(new SideNavItem("Enrollments", EnrollmentListView.class, VaadinIcon.CLIPBOARD_TEXT.create()));
        nav.addItem(new SideNavItem("Interactions", InteractionListView.class, VaadinIcon.COMMENTS.create()));
        nav.addItem(new SideNavItem("Payments", PaymentListView.class, VaadinIcon.DOLLAR.create()));

        addToDrawer(nav);
    }
}
