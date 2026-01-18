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

        add(student, course, enrollmentDate, status, grade, completionDate, feedback, createButtonsLayout());
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
        if (enrollment != null) {
            student.setValue(enrollment.getStudent());
            course.setValue(enrollment.getCourse());
            enrollmentDate.setValue(enrollment.getEnrollmentDate());
            completionDate.setValue(enrollment.getCompletionDate());
            status.setValue(enrollment.getStatus());
            grade.setValue(enrollment.getGrade());
            feedback.setValue(enrollment.getFeedback() != null ? enrollment.getFeedback() : "");
        }
    }

    private void validateAndSave() {
        if (enrollment == null) enrollment = new Enrollment();

        enrollment.setStudent(student.getValue());
        enrollment.setCourse(course.getValue());
        enrollment.setEnrollmentDate(enrollmentDate.getValue());
        enrollment.setCompletionDate(completionDate.getValue());
        enrollment.setStatus(status.getValue());
        enrollment.setGrade(grade.getValue());
        enrollment.setFeedback(feedback.getValue());

        fireEvent(new SaveEvent(this, enrollment));
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