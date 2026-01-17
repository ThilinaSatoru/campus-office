package nibm.project.campus_office.views.course;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.entity.Course;
import nibm.project.campus_office.repository.CourseRepository;
import nibm.project.campus_office.repository.InstructorRepository;
import nibm.project.campus_office.views.MainLayout;

@Route(value = "courses", layout = MainLayout.class)
@PageTitle("Courses | Diploma CRM")
@PermitAll
public class CourseListView extends VerticalLayout {

    private final CourseRepository courseRepo;
    private final InstructorRepository instructorRepo;
    private final Grid<Course> grid = new Grid<>(Course.class, false);
    private CourseForm form;

    public CourseListView(CourseRepository courseRepo, InstructorRepository instructorRepo) {
        this.courseRepo = courseRepo;
        this.instructorRepo = instructorRepo;
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(Course::getCourseCode).setHeader("Code").setSortable(true);
        grid.addColumn(Course::getTitle).setHeader("Title").setSortable(true);
        grid.addColumn(Course::getCredits).setHeader("Credits");
        grid.addColumn(Course::getDurationWeeks).setHeader("Duration (weeks)");
        grid.addColumn(Course::getLevel).setHeader("Level");
        grid.addColumn(c -> c.getInstructor() != null ?
                        c.getInstructor().getFirstName() + " " + c.getInstructor().getLastName() : "")
                .setHeader("Instructor");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editCourse(e.getValue()));
    }

    private void configureForm() {
        form = new CourseForm(instructorRepo.findAll());
        form.setWidth("25em");
        form.addSaveListener(this::saveCourse);
        form.addDeleteListener(this::deleteCourse);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("Add Course");
        addButton.addClickListener(e -> addCourse());
        return new HorizontalLayout(addButton);
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setSizeFull();
        return content;
    }

    private void updateList() {
        grid.setItems(courseRepo.findAll());
    }

    private void addCourse() {
        grid.asSingleSelect().clear();
        editCourse(new Course());
    }

    private void editCourse(Course course) {
        if (course == null) {
            closeEditor();
        } else {
            form.setCourse(course);
            form.setVisible(true);
        }
    }

    private void closeEditor() {
        form.setCourse(null);
        form.setVisible(false);
    }

    private void saveCourse(CourseForm.SaveEvent event) {
        courseRepo.save(event.getCourse());
        updateList();
        closeEditor();
        Notification.show("Course saved");
    }

    private void deleteCourse(CourseForm.DeleteEvent event) {
        courseRepo.delete(event.getCourse());
        updateList();
        closeEditor();
        Notification.show("Course deleted");
    }
}
