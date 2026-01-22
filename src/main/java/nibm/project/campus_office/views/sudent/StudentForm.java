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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.validator.EmailValidator;
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

    private final Binder<Student> binder = new BeanValidationBinder<>(Student.class);
    private Student student;

    public StudentForm() {
        addClassName("student-form");

        status.setItems(StudentStatus.values());
        status.setItemLabelGenerator(StudentStatus::name);

        configureValidation();

        add(studentId, firstName, lastName, email, phone, status,
                enrollmentDate, graduationDate, createButtonsLayout());
    }

    private void configureValidation() {
        // Student ID - Required
        binder.forField(studentId)
                .asRequired("Student ID is required")
                .bind(Student::getStudentId, Student::setStudentId);
        studentId.setHelperText("Enter unique student ID");

        // First Name - Required
        binder.forField(firstName)
                .asRequired("First name is required")
                .bind(Student::getFirstName, Student::setFirstName);
        firstName.setHelperText("Enter student's first name");

        // Last Name
        binder.forField(lastName)
                .bind(Student::getLastName, Student::setLastName);
        lastName.setHelperText("Enter student's last name");

        // Email - Required with format validation
        binder.forField(email)
                .asRequired("Email is required")
                .withValidator(new EmailValidator("Invalid email format"))
                .bind(Student::getEmail, Student::setEmail);
        email.setHelperText("Enter valid email address");

        // Phone
        binder.forField(phone)
                .bind(Student::getPhone, Student::setPhone);
        phone.setHelperText("Enter contact number");

        // Status
        binder.forField(status)
                .bind(Student::getStatus, Student::setStatus);
        status.setHelperText("Select student status");

        // Enrollment Date
        binder.forField(enrollmentDate)
                .bind(Student::getEnrollmentDate, Student::setEnrollmentDate);
        enrollmentDate.setHelperText("Select enrollment date");

        // Graduation Date
        binder.forField(graduationDate)
                .bind(Student::getGraduationDate, Student::setGraduationDate);
        graduationDate.setHelperText("Select graduation date");
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
        binder.readBean(student);
    }

    private void validateAndSave() {
        try {
            if (student == null) {
                student = new Student();
            }
            binder.writeBean(student);
            fireEvent(new SaveEvent(this, student));
        } catch (ValidationException e) {
            // Validation errors are automatically displayed on fields
        }
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