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

    private Course course;

    public CourseForm(List<Instructor> instructors) {
        addClassName("course-form");

        level.setItems(CourseLevel.values());
        instructor.setItems(instructors);
        instructor.setItemLabelGenerator(i -> i.getFirstName() + " " + i.getLastName());

        add(courseCode, title, description, credits, durationWeeks, level, instructor,
                createButtonsLayout());
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
        if (course != null) {
            courseCode.setValue(course.getCourseCode() != null ? course.getCourseCode() : "");
            title.setValue(course.getTitle() != null ? course.getTitle() : "");
            description.setValue(course.getDescription() != null ? course.getDescription() : "");
            credits.setValue(course.getCredits());
            durationWeeks.setValue(course.getDurationWeeks());
            level.setValue(course.getLevel());
            instructor.setValue(course.getInstructor());
        }
    }

    private void validateAndSave() {
        if (course == null) course = new Course();

        course.setCourseCode(courseCode.getValue());
        course.setTitle(title.getValue());
        course.setDescription(description.getValue());
        course.setCredits(credits.getValue());
        course.setDurationWeeks(durationWeeks.getValue());
        course.setLevel(level.getValue());
        course.setInstructor(instructor.getValue());

        fireEvent(new SaveEvent(this, course));
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
