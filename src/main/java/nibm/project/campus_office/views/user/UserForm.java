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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
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

    private final Binder<User> binder = new BeanValidationBinder<>(User.class);
    private User user;
    private boolean isNewUser = false;
    private final PasswordEncoder passwordEncoder;

    public UserForm(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        addClassName("user-form");

        role.setItems(UserRole.values());

        enabled.setValue(true);
        accountNonLocked.setValue(true);

        configureValidation();

        add(username, firstName, lastName, email, role, enabled, accountNonLocked,
                createButtonsLayout());
    }

    private void configureValidation() {
        // Username - Required
        binder.forField(username)
                .asRequired("Username is required")
                .bind(User::getUsername, User::setUsername);
        username.setHelperText("Enter unique username");

        // First Name - Required
        binder.forField(firstName)
                .asRequired("First name is required")
                .bind(User::getFirstName, User::setFirstName);
        firstName.setHelperText("Enter user's first name");

        // Last Name - Required
        binder.forField(lastName)
                .asRequired("Last name is required")
                .bind(User::getLastName, User::setLastName);
        lastName.setHelperText("Enter user's last name");

        // Email - Required with format validation
        binder.forField(email)
                .asRequired("Email is required")
                .withValidator(new EmailValidator("Invalid email format"))
                .bind(User::getEmail, User::setEmail);
        email.setHelperText("Enter valid email address");

        // Role - Required
        binder.forField(role)
                .asRequired("Role is required")
                .bind(User::getRole, User::setRole);
        role.setHelperText("Select user role");

        // Enabled
        binder.forField(enabled)
                .bind(User::getEnabled, User::setEnabled);

        // Account Non Locked
        binder.forField(accountNonLocked)
                .bind(User::getAccountNonLocked, User::setAccountNonLocked);

        // Password fields are validated separately for new users
        password.setHelperText("Minimum 6 characters recommended");
        confirmPassword.setHelperText("Re-enter password to confirm");
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
            binder.readBean(user);

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
        try {
            if (user == null) {
                user = new User();
            }

            // Validate password for new users
            if (isNewUser) {
                if (password.isEmpty()) {
                    password.setErrorMessage("Password is required for new users");
                    password.setInvalid(true);
                    return;
                }
                if (confirmPassword.isEmpty()) {
                    confirmPassword.setErrorMessage("Please confirm password");
                    confirmPassword.setInvalid(true);
                    return;
                }
                if (!password.getValue().equals(confirmPassword.getValue())) {
                    confirmPassword.setErrorMessage("Passwords do not match");
                    confirmPassword.setInvalid(true);
                    return;
                }

                // Clear any previous errors
                password.setInvalid(false);
                confirmPassword.setInvalid(false);

                user.setPassword(passwordEncoder.encode(password.getValue()));
            }

            binder.writeBean(user);
            fireEvent(new SaveEvent(this, user));

        } catch (ValidationException e) {
            // Validation errors are automatically displayed on fields
        }
    }

    private void openResetPasswordDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Reset Password");

        PasswordField newPassword = new PasswordField("New Password");
        PasswordField confirmNewPassword = new PasswordField("Confirm New Password");

        newPassword.setHelperText("Minimum 6 characters recommended");
        confirmNewPassword.setHelperText("Re-enter password to confirm");

        Button resetButton = new Button("Reset", e -> {
            boolean isValid = true;

            if (newPassword.isEmpty()) {
                newPassword.setErrorMessage("Password is required");
                newPassword.setInvalid(true);
                isValid = false;
            } else {
                newPassword.setInvalid(false);
            }

            if (confirmNewPassword.isEmpty()) {
                confirmNewPassword.setErrorMessage("Please confirm password");
                confirmNewPassword.setInvalid(true);
                isValid = false;
            } else if (!newPassword.getValue().equals(confirmNewPassword.getValue())) {
                confirmNewPassword.setErrorMessage("Passwords do not match");
                confirmNewPassword.setInvalid(true);
                isValid = false;
            } else {
                confirmNewPassword.setInvalid(false);
            }

            if (!isValid) return;

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

    public void addPasswordResetListener(ComponentEventListener<PasswordResetEvent> listener) {
        addListener(PasswordResetEvent.class, listener);
    }
}
