package nibm.project.campus_office.views.instructor;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
import nibm.project.campus_office.entity.Instructor;

public class InstructorForm extends FormLayout {

    TextField firstName = new TextField("First Name");
    TextField lastName = new TextField("Last Name");
    EmailField email = new EmailField("Email");
    TextField phone = new TextField("Phone");
    TextField specialization = new TextField("Specialization");
    TextArea bio = new TextArea("Bio");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private final Binder<Instructor> binder = new BeanValidationBinder<>(Instructor.class);
    private Instructor instructor;

    public InstructorForm() {
        addClassName("instructor-form");
        configureValidation();
        add(firstName, lastName, email, phone, specialization, bio, createButtonsLayout());
    }

    private void configureValidation() {
        // First Name - Required
        binder.forField(firstName)
                .asRequired("First name is required")
                .bind(Instructor::getFirstName, Instructor::setFirstName);
        firstName.setHelperText("Enter instructor's first name");

        // Last Name - Required
        binder.forField(lastName)
                .asRequired("Last name is required")
                .bind(Instructor::getLastName, Instructor::setLastName);
        lastName.setHelperText("Enter instructor's last name");

        // Email - Required with format validation
        binder.forField(email)
                .asRequired("Email is required")
                .withValidator(new EmailValidator("Invalid email format"))
                .bind(Instructor::getEmail, Instructor::setEmail);
        email.setHelperText("Enter valid email address");

        // Phone
        binder.forField(phone)
                .bind(Instructor::getPhone, Instructor::setPhone);
        phone.setHelperText("Enter contact number");

        // Specialization
        binder.forField(specialization)
                .bind(Instructor::getSpecialization, Instructor::setSpecialization);
        specialization.setHelperText("Enter area of specialization");

        // Bio
        binder.forField(bio)
                .bind(Instructor::getBio, Instructor::setBio);
        bio.setHelperText("Enter instructor biography");
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new DeleteEvent(this, instructor)));
        close.addClickListener(e -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, close);
    }

    public void setInstructor(Instructor instructor) {
        this.instructor = instructor;
        binder.readBean(instructor);
    }

    private void validateAndSave() {
        try {
            if (instructor == null) {
                instructor = new Instructor();
            }
            binder.writeBean(instructor);
            fireEvent(new SaveEvent(this, instructor));
        } catch (ValidationException e) {
            // Validation errors are automatically displayed on fields
        }
    }

    public static abstract class InstructorFormEvent extends ComponentEvent<InstructorForm> {
        private final Instructor instructor;

        protected InstructorFormEvent(InstructorForm source, Instructor instructor) {
            super(source, false);
            this.instructor = instructor;
        }

        public Instructor getInstructor() {
            return instructor;
        }
    }

    public static class SaveEvent extends InstructorFormEvent {
        SaveEvent(InstructorForm source, Instructor instructor) {
            super(source, instructor);
        }
    }

    public static class DeleteEvent extends InstructorFormEvent {
        DeleteEvent(InstructorForm source, Instructor instructor) {
            super(source, instructor);
        }
    }

    public static class CloseEvent extends InstructorFormEvent {
        CloseEvent(InstructorForm source) {
            super(source, null);
        }
    }

    public void addSaveListener(ComponentEventListener<SaveEvent> listener) {
        addListener(SaveEvent.class, listener);
    }

    public void addDeleteListener(ComponentEventListener<DeleteEvent> listener) {
        addListener(DeleteEvent.class, listener);
    }

    public void addCloseListener(ComponentEventListener<CloseEvent> listener) {
        addListener(CloseEvent.class, listener);
    }
}
