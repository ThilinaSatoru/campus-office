package nibm.project.campus_office.views;

import com.vaadin.flow.component.Component;
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
import jakarta.annotation.security.RolesAllowed;
import nibm.project.campus_office.security.SecurityService;
import nibm.project.campus_office.views.Dashboard.DashboardView;
import nibm.project.campus_office.views.course.CourseListView;
import nibm.project.campus_office.views.enroll.EnrollmentListView;
import nibm.project.campus_office.views.instructor.InstructorListView;
import nibm.project.campus_office.views.interactions.InteractionListView;
import nibm.project.campus_office.views.payments.PaymentListView;
import nibm.project.campus_office.views.sudent.StudentListView;
import nibm.project.campus_office.views.user.UserListView;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;
import java.util.stream.Collectors;

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

        // Add menu items only if user has access
        addItemIfAuthorized(nav, "Dashboard", DashboardView.class, VaadinIcon.DASHBOARD.create());
        addItemIfAuthorized(nav, "Students", StudentListView.class, VaadinIcon.ACADEMY_CAP.create());
        addItemIfAuthorized(nav, "Courses", CourseListView.class, VaadinIcon.BOOK.create());
        addItemIfAuthorized(nav, "Instructors", InstructorListView.class, VaadinIcon.USER_STAR.create());
        addItemIfAuthorized(nav, "Enrollments", EnrollmentListView.class, VaadinIcon.CLIPBOARD_TEXT.create());
        addItemIfAuthorized(nav, "Interactions", InteractionListView.class, VaadinIcon.COMMENTS.create());
        addItemIfAuthorized(nav, "Payments", PaymentListView.class, VaadinIcon.DOLLAR.create());
        addItemIfAuthorized(nav, "Users", UserListView.class, VaadinIcon.USER.create());

        addToDrawer(nav);
    }

    private void addItemIfAuthorized(SideNav nav, String label, Class<? extends Component> viewClass, Component icon) {
        if (hasAccess(viewClass)) {
            nav.addItem(new SideNavItem(label, viewClass, icon));
        }
    }

    private boolean hasAccess(Class<? extends Component> viewClass) {
        // Check if class has @PermitAll - everyone can access
        if (viewClass.isAnnotationPresent(PermitAll.class)) {
            return true;
        }

        // Check if class has @RolesAllowed
        RolesAllowed rolesAllowed = viewClass.getAnnotation(RolesAllowed.class);
        if (rolesAllowed == null) {
            return false; // No security annotation, deny access
        }

        // Get current user roles
        Set<String> userRoles = getUserRoles();

        // Check if user has any of the required roles
        for (String requiredRole : rolesAllowed.value()) {
            if (userRoles.contains("ROLE_" + requiredRole) || userRoles.contains(requiredRole)) {
                return true;
            }
        }

        return false;
    }

    private Set<String> getUserRoles() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toSet());
        }
        return Set.of();
    }
}