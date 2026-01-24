package nibm.project.campus_office.views.enroll;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import nibm.project.campus_office.entity.Course;
import nibm.project.campus_office.entity.Enrollment;
import nibm.project.campus_office.entity.Student;
import nibm.project.campus_office.enums.EnrollmentStatus;

import java.util.List;

public class EnrollmentForm extends FormLayout {

    ComboBox<Student> student = new ComboBox<>("Student");
    ComboBox<Course> course = new ComboBox<>("Course");
    DatePicker enrollmentDate = new DatePicker("Enrollment Date");
    DatePicker completionDate = new DatePicker("Completion Date");
    ComboBox<EnrollmentStatus> status = new ComboBox<>("Status");
    NumberField grade = new NumberField("Grade (%)");
    TextArea feedback = new TextArea("Feedback");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private final Binder<Enrollment> binder = new BeanValidationBinder<>(Enrollment.class);
    private Enrollment enrollment;

    public EnrollmentForm(List<Student> students, List<Course> courses) {
        addClassName("enrollment-form");

        student.setItems(students);
        student.setItemLabelGenerator(s -> s.getFirstName() + " " + s.getLastName() + " (" + s.getStudentId() + ")");

        course.setItems(courses);
        course.setItemLabelGenerator(c -> c.getCourseCode() + " - " + c.getTitle());

        status.setItems(EnrollmentStatus.values());
        grade.setMin(0);
        grade.setMax(100);

        configureValidation();

        add(student, course, enrollmentDate, status, grade, completionDate, feedback, createButtonsLayout());
    }

    private void configureValidation() {
        // Student - Required
        binder.forField(student)
                .asRequired("Student is required")
                .bind(Enrollment::getStudent, Enrollment::setStudent);
        student.setHelperText("Select student to enroll");

        // Course - Required
        binder.forField(course)
                .asRequired("Course is required")
                .bind(Enrollment::getCourse, Enrollment::setCourse);
        course.setHelperText("Select course for enrollment");

        // Enrollment Date - Required
        binder.forField(enrollmentDate)
                .asRequired("Enrollment date is required")
                .bind(Enrollment::getEnrollmentDate, Enrollment::setEnrollmentDate);
        enrollmentDate.setHelperText("Select enrollment date");

        // Status - Required
        binder.forField(status)
                .asRequired("Status is required")
                .bind(Enrollment::getStatus, Enrollment::setStatus);
        status.setHelperText("Select enrollment status");

        // Grade - Optional, must be 0-100 if provided
        binder.forField(grade)
                .withValidator(value -> value == null || (value >= 0 && value <= 100),
                        "Grade must be between 0 and 100")
                .bind(Enrollment::getGrade, Enrollment::setGrade);
        grade.setHelperText("Enter grade percentage (0-100)");
        grade.setStep(0.1);
        grade.setStepButtonsVisible(true);

        // Completion Date
        binder.forField(completionDate)
                .bind(Enrollment::getCompletionDate, Enrollment::setCompletionDate);
        completionDate.setHelperText("Select completion date (if applicable)");

        // Feedback
        binder.forField(feedback)
                .bind(Enrollment::getFeedback, Enrollment::setFeedback);
        feedback.setHelperText("Enter feedback or comments");
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new DeleteEvent(this, enrollment)));
        close.addClickListener(e -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, close);
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
        binder.readBean(enrollment);
    }

    private void validateAndSave() {
        try {
            if (enrollment == null) {
                enrollment = new Enrollment();
            }
            binder.writeBean(enrollment);
            fireEvent(new SaveEvent(this, enrollment));
        } catch (ValidationException e) {
            // Validation errors are automatically displayed on fields
        }
    }

    public static abstract class EnrollmentFormEvent extends ComponentEvent<EnrollmentForm> {
        private final Enrollment enrollment;

        protected EnrollmentFormEvent(EnrollmentForm source, Enrollment enrollment) {
            super(source, false);
            this.enrollment = enrollment;
        }

        public Enrollment getEnrollment() {
            return enrollment;
        }
    }

    public static class SaveEvent extends EnrollmentFormEvent {
        SaveEvent(EnrollmentForm source, Enrollment enrollment) {
            super(source, enrollment);
        }
    }

    public static class DeleteEvent extends EnrollmentFormEvent {
        DeleteEvent(EnrollmentForm source, Enrollment enrollment) {
            super(source, enrollment);
        }
    }

    public static class CloseEvent extends EnrollmentFormEvent {
        CloseEvent(EnrollmentForm source) {
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