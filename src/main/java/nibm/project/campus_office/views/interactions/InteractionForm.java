package nibm.project.campus_office.views.interactions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import nibm.project.campus_office.entity.Interaction;
import nibm.project.campus_office.entity.Student;
import nibm.project.campus_office.enums.InteractionType;

import java.util.List;

public class InteractionForm extends FormLayout {

    ComboBox<Student> student = new ComboBox<>("Student");
    ComboBox<InteractionType> type = new ComboBox<>("Type");
    TextField subject = new TextField("Subject");
    TextArea notes = new TextArea("Notes");
    DateTimePicker interactionDate = new DateTimePicker("Interaction Date");
    TextField contactedBy = new TextField("Contacted By");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private final Binder<Interaction> binder = new BeanValidationBinder<>(Interaction.class);
    private Interaction interaction;

    public InteractionForm(List<Student> students) {
        addClassName("interaction-form");

        student.setItems(students);
        student.setItemLabelGenerator(s -> s.getFirstName() + " " + s.getLastName());

        type.setItems(InteractionType.values());

        configureValidation();

        add(student, type, subject, interactionDate, contactedBy, notes, createButtonsLayout());
    }

    private void configureValidation() {
        // Student - Required
        binder.forField(student)
                .asRequired("Student is required")
                .bind(Interaction::getStudent, Interaction::setStudent);
        student.setHelperText("Select student for this interaction");

        // Type - Required
        binder.forField(type)
                .asRequired("Interaction type is required")
                .bind(Interaction::getType, Interaction::setType);
        type.setHelperText("Select type of interaction");

        // Subject - Required
        binder.forField(subject)
                .asRequired("Subject is required")
                .bind(Interaction::getSubject, Interaction::setSubject);
        subject.setHelperText("Enter interaction subject");

        // Interaction Date - Required
        binder.forField(interactionDate)
                .asRequired("Interaction date is required")
                .bind(Interaction::getInteractionDate, Interaction::setInteractionDate);
        interactionDate.setHelperText("Select date and time of interaction");

        // Contacted By
        binder.forField(contactedBy)
                .bind(Interaction::getContactedBy, Interaction::setContactedBy);
        contactedBy.setHelperText("Enter name of person who made contact");

        // Notes
        binder.forField(notes)
                .bind(Interaction::getNotes, Interaction::setNotes);
        notes.setHelperText("Enter additional notes or details");
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new DeleteEvent(this, interaction)));
        close.addClickListener(e -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, close);
    }

    public void setInteraction(Interaction interaction) {
        this.interaction = interaction;
        binder.readBean(interaction);
    }

    private void validateAndSave() {
        try {
            if (interaction == null) {
                interaction = new Interaction();
            }
            binder.writeBean(interaction);
            fireEvent(new SaveEvent(this, interaction));
        } catch (ValidationException e) {
            // Validation errors are automatically displayed on fields
        }
    }

    public static abstract class InteractionFormEvent extends ComponentEvent<InteractionForm> {
        private final Interaction interaction;

        protected InteractionFormEvent(InteractionForm source, Interaction interaction) {
            super(source, false);
            this.interaction = interaction;
        }

        public Interaction getInteraction() {
            return interaction;
        }
    }

    public static class SaveEvent extends InteractionFormEvent {
        SaveEvent(InteractionForm source, Interaction interaction) {
            super(source, interaction);
        }
    }

    public static class DeleteEvent extends InteractionFormEvent {
        DeleteEvent(InteractionForm source, Interaction interaction) {
            super(source, interaction);
        }
    }

    public static class CloseEvent extends InteractionFormEvent {
        CloseEvent(InteractionForm source) {
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
