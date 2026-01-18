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

    private Instructor instructor;

    public InstructorForm() {
        addClassName("instructor-form");
        add(firstName, lastName, email, phone, specialization, bio, createButtonsLayout());
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
        if (instructor != null) {
            firstName.setValue(instructor.getFirstName() != null ? instructor.getFirstName() : "");
            lastName.setValue(instructor.getLastName() != null ? instructor.getLastName() : "");
            email.setValue(instructor.getEmail() != null ? instructor.getEmail() : "");
            phone.setValue(instructor.getPhone() != null ? instructor.getPhone() : "");
            specialization.setValue(instructor.getSpecialization() != null ? instructor.getSpecialization() : "");
            bio.setValue(instructor.getBio() != null ? instructor.getBio() : "");
        }
    }

    private void validateAndSave() {
        if (instructor == null) instructor = new Instructor();

        instructor.setFirstName(firstName.getValue());
        instructor.setLastName(lastName.getValue());
        instructor.setEmail(email.getValue());
        instructor.setPhone(phone.getValue());
        instructor.setSpecialization(specialization.getValue());
        instructor.setBio(bio.getValue());

        fireEvent(new SaveEvent(this, instructor));
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
