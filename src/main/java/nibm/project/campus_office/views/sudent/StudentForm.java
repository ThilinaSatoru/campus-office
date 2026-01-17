package nibm.project.campus_office.views.sudent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import nibm.project.campus_office.entity.Student;
import nibm.project.campus_office.enums.StudentStatus;

public class StudentForm extends FormLayout {

    TextField studentId = new TextField("Student ID");
    TextField firstName = new TextField("First Name");
    TextField lastName = new TextField("Last Name");
    EmailField email = new EmailField("Email");
    TextField phone = new TextField("Phone");
    ComboBox<StudentStatus> status = new ComboBox<>("Status");
    DatePicker enrollmentDate = new DatePicker("Enrollment Date");
    DatePicker graduationDate = new DatePicker("Graduation Date");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private Student student;

    public StudentForm() {
        addClassName("student-form");

        status.setItems(StudentStatus.values());
        status.setItemLabelGenerator(StudentStatus::name);

        add(studentId, firstName, lastName, email, phone, status,
                enrollmentDate, graduationDate, createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new DeleteEvent(this, student)));
        close.addClickListener(e -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, close);
    }

    public void setStudent(Student student) {
        this.student = student;
        if (student != null) {
            studentId.setValue(student.getStudentId() != null ? student.getStudentId() : "");
            firstName.setValue(student.getFirstName() != null ? student.getFirstName() : "");
            lastName.setValue(student.getLastName() != null ? student.getLastName() : "");
            email.setValue(student.getEmail() != null ? student.getEmail() : "");
            phone.setValue(student.getPhone() != null ? student.getPhone() : "");
            status.setValue(student.getStatus());
            enrollmentDate.setValue(student.getEnrollmentDate());
            graduationDate.setValue(student.getGraduationDate());
        }
    }

    private void validateAndSave() {
        if (student == null) student = new Student();

        student.setStudentId(studentId.getValue());
        student.setFirstName(firstName.getValue());
        student.setLastName(lastName.getValue());
        student.setEmail(email.getValue());
        student.setPhone(phone.getValue());
        student.setStatus(status.getValue());
        student.setEnrollmentDate(enrollmentDate.getValue());
        student.setGraduationDate(graduationDate.getValue());

        fireEvent(new SaveEvent(this, student));
    }

    // Events
    public static abstract class StudentFormEvent extends ComponentEvent<StudentForm> {
        private final Student student;

        protected StudentFormEvent(StudentForm source, Student student) {
            super(source, false);
            this.student = student;
        }

        public Student getStudent() {
            return student;
        }
    }

    public static class SaveEvent extends StudentFormEvent {
        SaveEvent(StudentForm source, Student student) {
            super(source, student);
        }
    }

    public static class DeleteEvent extends StudentFormEvent {
        DeleteEvent(StudentForm source, Student student) {
            super(source, student);
        }
    }

    public static class CloseEvent extends StudentFormEvent {
        CloseEvent(StudentForm source) {
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