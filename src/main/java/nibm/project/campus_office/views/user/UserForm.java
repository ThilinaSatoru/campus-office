package nibm.project.campus_office.views.user;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import nibm.project.campus_office.entity.User;
import nibm.project.campus_office.enums.UserRole;
import org.springframework.security.crypto.password.PasswordEncoder;

public class UserForm extends FormLayout {

    TextField username = new TextField("Username");
    TextField firstName = new TextField("First Name");
    TextField lastName = new TextField("Last Name");
    EmailField email = new EmailField("Email");
    ComboBox<UserRole> role = new ComboBox<>("Role");
    Checkbox enabled = new Checkbox("Enabled");
    Checkbox accountNonLocked = new Checkbox("Account Non Locked");

    PasswordField password = new PasswordField("Password");
    PasswordField confirmPassword = new PasswordField("Confirm Password");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button resetPassword = new Button("Reset Password");
    Button close = new Button("Cancel");

    private User user;
    private boolean isNewUser = false;
    private final PasswordEncoder passwordEncoder;

    public UserForm(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        addClassName("user-form");

        role.setItems(UserRole.values());

        enabled.setValue(true);
        accountNonLocked.setValue(true);

        add(username, firstName, lastName, email, role, enabled, accountNonLocked,
                createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        resetPassword.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new DeleteEvent(this, user)));
        resetPassword.addClickListener(e -> openResetPasswordDialog());
        close.addClickListener(e -> fireEvent(new CloseEvent(this)));

        HorizontalLayout buttonLayout = new HorizontalLayout(save, delete, resetPassword, close);
        return buttonLayout;
    }

    public void setUser(User user) {
        this.user = user;
        this.isNewUser = (user == null || user.getId() == null);

        if (user != null) {
            username.setValue(user.getUsername() != null ? user.getUsername() : "");
            firstName.setValue(user.getFirstName() != null ? user.getFirstName() : "");
            lastName.setValue(user.getLastName() != null ? user.getLastName() : "");
            email.setValue(user.getEmail() != null ? user.getEmail() : "");
            role.setValue(user.getRole());
            enabled.setValue(user.getEnabled() != null ? user.getEnabled() : true);
            accountNonLocked.setValue(user.getAccountNonLocked() != null ? user.getAccountNonLocked() : true);

            // Show/hide password fields and reset button based on new/existing user
            if (isNewUser) {
                showPasswordFields();
                resetPassword.setVisible(false);
                delete.setVisible(true);
            } else {
                hidePasswordFields();
                resetPassword.setVisible(true);
                // Hide delete button for ADMIN role
                delete.setVisible(user.getRole() != UserRole.ADMIN);
            }
        }
    }

    private void showPasswordFields() {
        if (!this.getChildren().anyMatch(c -> c == password)) {
            addComponentAtIndex(7, password);
            addComponentAtIndex(8, confirmPassword);
        }
        password.setRequiredIndicatorVisible(true);
        confirmPassword.setRequiredIndicatorVisible(true);
    }

    private void hidePasswordFields() {
        remove(password);
        remove(confirmPassword);
        password.clear();
        confirmPassword.clear();
    }

    private void validateAndSave() {
        if (user == null) user = new User();

        // Validate required fields
        if (username.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
                email.isEmpty() || role.isEmpty()) {
            fireEvent(new ValidationErrorEvent(this, "Please fill all required fields"));
            return;
        }

        // Validate password for new users
        if (isNewUser) {
            if (password.isEmpty() || confirmPassword.isEmpty()) {
                fireEvent(new ValidationErrorEvent(this, "Password is required for new users"));
                return;
            }
            if (!password.getValue().equals(confirmPassword.getValue())) {
                fireEvent(new ValidationErrorEvent(this, "Passwords do not match"));
                return;
            }
            user.setPassword(passwordEncoder.encode(password.getValue()));
        }

        user.setUsername(username.getValue());
        user.setFirstName(firstName.getValue());
        user.setLastName(lastName.getValue());
        user.setEmail(email.getValue());
        user.setRole(role.getValue());
        user.setEnabled(enabled.getValue());
        user.setAccountNonLocked(accountNonLocked.getValue());

        fireEvent(new SaveEvent(this, user));
    }

    private void openResetPasswordDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Reset Password");

        PasswordField newPassword = new PasswordField("New Password");
        PasswordField confirmNewPassword = new PasswordField("Confirm New Password");

        Button resetButton = new Button("Reset", e -> {
            if (newPassword.isEmpty() || confirmNewPassword.isEmpty()) {
                fireEvent(new ValidationErrorEvent(this, "Please fill both password fields"));
                return;
            }
            if (!newPassword.getValue().equals(confirmNewPassword.getValue())) {
                fireEvent(new ValidationErrorEvent(this, "Passwords do not match"));
                return;
            }

            String hashedPassword = passwordEncoder.encode(newPassword.getValue());
            fireEvent(new PasswordResetEvent(this, user, hashedPassword));
            dialog.close();
        });
        resetButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Cancel", e -> dialog.close());

        VerticalLayout dialogLayout = new VerticalLayout(newPassword, confirmNewPassword);
        dialog.add(dialogLayout);
        dialog.getFooter().add(cancelButton, resetButton);

        dialog.open();
    }

    // Event classes
    public static abstract class UserFormEvent extends ComponentEvent<UserForm> {
        private final User user;

        protected UserFormEvent(UserForm source, User user) {
            super(source, false);
            this.user = user;
        }

        public User getUser() {
            return user;
        }
    }

    public static class SaveEvent extends UserFormEvent {
        SaveEvent(UserForm source, User user) {
            super(source, user);
        }
    }

    public static class DeleteEvent extends UserFormEvent {
        DeleteEvent(UserForm source, User user) {
            super(source, user);
        }
    }

    public static class CloseEvent extends UserFormEvent {
        CloseEvent(UserForm source) {
            super(source, null);
        }
    }

    public static class ValidationErrorEvent extends ComponentEvent<UserForm> {
        private final String message;

        ValidationErrorEvent(UserForm source, String message) {
            super(source, false);
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class PasswordResetEvent extends UserFormEvent {
        private final String hashedPassword;

        PasswordResetEvent(UserForm source, User user, String hashedPassword) {
            super(source, user);
            this.hashedPassword = hashedPassword;
        }

        public String getHashedPassword() {
            return hashedPassword;
        }
    }

    // Event listeners
    public void addSaveListener(ComponentEventListener<SaveEvent> listener) {
        addListener(SaveEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        addListener(DeleteEvent.class, listener);
    }

    public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
        addListener(CloseEvent.class, listener);
    }

    public void addValidationErrorListener(ComponentEventListener<ValidationErrorEvent> listener) {
        addListener(ValidationErrorEvent.class, listener);
    }

    public void addPasswordResetListener(ComponentEventListener<PasswordResetEvent> listener) {
        addListener(PasswordResetEvent.class, listener);
    }
}
