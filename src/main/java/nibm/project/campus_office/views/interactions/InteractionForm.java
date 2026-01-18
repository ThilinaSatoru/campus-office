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

    private Interaction interaction;

    public InteractionForm(List<Student> students) {
        addClassName("interaction-form");

        student.setItems(students);
        student.setItemLabelGenerator(s -> s.getFirstName() + " " + s.getLastName());

        type.setItems(InteractionType.values());

        add(student, type, subject, interactionDate, contactedBy, notes, createButtonsLayout());
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
        if (interaction != null) {
            student.setValue(interaction.getStudent());
            type.setValue(interaction.getType());
            subject.setValue(interaction.getSubject() != null ? interaction.getSubject() : "");
            notes.setValue(interaction.getNotes() != null ? interaction.getNotes() : "");
            interactionDate.setValue(interaction.getInteractionDate());
            contactedBy.setValue(interaction.getContactedBy() != null ? interaction.getContactedBy() : "");
        }
    }

    private void validateAndSave() {
        if (interaction == null) interaction = new Interaction();

        interaction.setStudent(student.getValue());
        interaction.setType(type.getValue());
        interaction.setSubject(subject.getValue());
        interaction.setNotes(notes.getValue());
        interaction.setInteractionDate(interactionDate.getValue());
        interaction.setContactedBy(contactedBy.getValue());

        fireEvent(new SaveEvent(this, interaction));
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
