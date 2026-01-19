package nibm.project.campus_office.views.user;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import nibm.project.campus_office.entity.User;
import nibm.project.campus_office.repository.UserRepository;
import nibm.project.campus_office.views.MainLayout;
import org.springframework.security.crypto.password.PasswordEncoder;

@Route(value = "users", layout = MainLayout.class)
@PageTitle("Users | Diploma CRM")
@RolesAllowed("ADMIN")
public class UserListView extends VerticalLayout {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final Grid<User> grid = new Grid<>(User.class, false);
    private UserForm form;

    public UserListView(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(User::getUsername).setHeader("Username").setSortable(true);
        grid.addColumn(User::getFirstName).setHeader("First Name").setSortable(true);
        grid.addColumn(User::getLastName).setHeader("Last Name").setSortable(true);
        grid.addColumn(User::getEmail).setHeader("Email").setSortable(true);
        grid.addColumn(User::getRole).setHeader("Role").setSortable(true);
        grid.addColumn(user -> user.getEnabled() ? "Yes" : "No").setHeader("Enabled");
        grid.addColumn(user -> user.getAccountNonLocked() ? "Yes" : "No").setHeader("Unlocked");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editUser(e.getValue()));
    }

    private void configureForm() {
        form = new UserForm(passwordEncoder);
        form.setWidth("25em");
        form.addSaveListener(this::saveUser);
        form.addDeleteListener(this::deleteUser);
        form.addCloseListener(e -> closeEditor());
        form.addValidationErrorListener(this::handleValidationError);
        form.addPasswordResetListener(this::resetPassword);
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("Add User");
        addButton.addClickListener(e -> addUser());
        return new HorizontalLayout(addButton);
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }

    private void updateList() {
        grid.setItems(userRepo.findAll());
    }

    private void addUser() {
        grid.asSingleSelect().clear();
        editUser(new User());
    }

    private void editUser(User user) {
        if (user == null) {
            closeEditor();
        } else {
            form.setUser(user);
            form.setVisible(true);
        }
    }

    private void closeEditor() {
        form.setUser(null);
        form.setVisible(false);
    }

    private void saveUser(UserForm.SaveEvent event) {
        try {
            userRepo.save(event.getUser());
            updateList();
            closeEditor();
            Notification.show("User saved successfully")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error saving user: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void deleteUser(UserForm.DeleteEvent event) {
        try {
            userRepo.delete(event.getUser());
            updateList();
            closeEditor();
            Notification.show("User deleted successfully")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error deleting user: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void resetPassword(UserForm.PasswordResetEvent event) {
        try {
            User user = event.getUser();
            user.setPassword(event.getHashedPassword());
            userRepo.save(user);
            Notification.show("Password reset successfully")
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error resetting password: " + e.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void handleValidationError(UserForm.ValidationErrorEvent event) {
        Notification.show(event.getMessage())
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}