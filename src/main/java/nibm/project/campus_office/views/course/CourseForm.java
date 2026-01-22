package nibm.project.campus_office.views.course;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import nibm.project.campus_office.entity.Course;
import nibm.project.campus_office.entity.Instructor;
import nibm.project.campus_office.enums.CourseLevel;

import java.util.List;

public class CourseForm extends FormLayout {

    TextField courseCode = new TextField("Course Code");
    TextField title = new TextField("Title");
    TextArea description = new TextArea("Description");
    IntegerField credits = new IntegerField("Credits");
    IntegerField durationWeeks = new IntegerField("Duration (weeks)");
    ComboBox<CourseLevel> level = new ComboBox<>("Level");
    ComboBox<Instructor> instructor = new ComboBox<>("Instructor");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private final Binder<Course> binder = new BeanValidationBinder<>(Course.class);
    private Course course;

    public CourseForm(List<Instructor> instructors) {
        addClassName("course-form");

        level.setItems(CourseLevel.values());
        instructor.setItems(instructors);
        instructor.setItemLabelGenerator(i -> i.getFirstName() + " " + i.getLastName());

        configureValidation();

        add(courseCode, title, description, credits, durationWeeks, level, instructor,
                createButtonsLayout());
    }

    private void configureValidation() {
        // Course Code - Required
        binder.forField(courseCode)
                .asRequired("Course code is required")
                .bind(Course::getCourseCode, Course::setCourseCode);
        courseCode.setHelperText("Enter unique course code");

        // Title - Required
        binder.forField(title)
                .asRequired("Title is required")
                .bind(Course::getTitle, Course::setTitle);
        title.setHelperText("Enter course title");

        // Description
        binder.forField(description)
                .bind(Course::getDescription, Course::setDescription);
        description.setHelperText("Enter course description");

        // Credits - Required, must be positive
        binder.forField(credits)
                .asRequired("Credits is required")
                .withValidator(value -> value != null && value > 0,
                        "Credits must be greater than 0")
                .bind(Course::getCredits, Course::setCredits);
        credits.setHelperText("Enter credit hours (positive number)");
        credits.setMin(1);
        credits.setStepButtonsVisible(true);

        // Duration Weeks - Required, must be positive
        binder.forField(durationWeeks)
                .asRequired("Duration is required")
                .withValidator(value -> value != null && value > 0,
                        "Duration must be greater than 0")
                .bind(Course::getDurationWeeks, Course::setDurationWeeks);
        durationWeeks.setHelperText("Enter course duration in weeks");
        durationWeeks.setMin(1);
        durationWeeks.setStepButtonsVisible(true);

        // Level - Required
        binder.forField(level)
                .asRequired("Level is required")
                .bind(Course::getLevel, Course::setLevel);
        level.setHelperText("Select course level");

        // Instructor
        binder.forField(instructor)
                .bind(Course::getInstructor, Course::setInstructor);
        instructor.setHelperText("Assign instructor (optional)");
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new DeleteEvent(this, course)));
        close.addClickListener(e -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, close);
    }

    public void setCourse(Course course) {
        this.course = course;
        binder.readBean(course);
    }

    private void validateAndSave() {
        try {
            if (course == null) {
                course = new Course();
            }
            binder.writeBean(course);
            fireEvent(new SaveEvent(this, course));
        } catch (ValidationException e) {
            // Validation errors are automatically displayed on fields
        }
    }

    // Events
    public static abstract class CourseFormEvent extends ComponentEvent<CourseForm> {
        private final Course course;

        protected CourseFormEvent(CourseForm source, Course course) {
            super(source, false);
            this.course = course;
        }

        public Course getCourse() {
            return course;
        }
    }

    public static class SaveEvent extends CourseFormEvent {
        SaveEvent(CourseForm source, Course course) {
            super(source, course);
        }
    }

    public static class DeleteEvent extends CourseFormEvent {
        DeleteEvent(CourseForm source, Course course) {
            super(source, course);
        }
    }

    public static class CloseEvent extends CourseFormEvent {
        CloseEvent(CourseForm source) {
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
