package nibm.project.campus_office.views.enroll;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.entity.Enrollment;
import nibm.project.campus_office.service.CourseService;
import nibm.project.campus_office.service.EnrollmentService;
import nibm.project.campus_office.service.StudentService;
import nibm.project.campus_office.views.MainLayout;
import org.springframework.transaction.annotation.Transactional;

@Route(value = "enrollments", layout = MainLayout.class)
@PageTitle("Enrollments | Diploma CRM")
@PermitAll
@Transactional(readOnly = true)
public class EnrollmentListView extends VerticalLayout {

    private final EnrollmentService enrollmentService;
    private final StudentService studentService;
    private final CourseService courseService;
    private final Grid<Enrollment> grid = new Grid<>(Enrollment.class, false);
    private EnrollmentForm form;

    public EnrollmentListView(EnrollmentService enrollmentService, StudentService studentService,
                              CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
        this.courseService = courseService;
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(e -> e.getStudent().getFirstName() + " " + e.getStudent().getLastName())
                .setHeader("Student").setSortable(true);
        grid.addColumn(e -> e.getCourse().getTitle()).setHeader("Course").setSortable(true);
        grid.addColumn(e -> e.getEnrollmentDate() != null ? e.getEnrollmentDate().toString() : "")
                .setHeader("Enrollment Date");
        grid.addColumn(Enrollment::getStatus).setHeader("Status");
        grid.addColumn(Enrollment::getGrade).setHeader("Grade");
        grid.addColumn(e -> e.getCompletionDate() != null ? e.getCompletionDate().toString() : "")
                .setHeader("Completion Date");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editEnrollment(e.getValue()));
    }

    private void configureForm() {
        form = new EnrollmentForm(studentService.findAll(), courseService.findAll());
        form.setWidth("25em");
        form.addSaveListener(this::saveEnrollment);
        form.addDeleteListener(this::deleteEnrollment);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("Add Enrollment");
        addButton.addClickListener(e -> addEnrollment());
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
        grid.setItems(enrollmentService.findAll());
    }

    private void addEnrollment() {
        grid.asSingleSelect().clear();
        editEnrollment(new Enrollment());
    }

    private void editEnrollment(Enrollment enrollment) {
        if (enrollment == null) {
            closeEditor();
        } else {
            form.setEnrollment(enrollment);
            form.setVisible(true);
        }
    }

    private void closeEditor() {
        form.setEnrollment(null);
        form.setVisible(false);
    }

    private void saveEnrollment(EnrollmentForm.SaveEvent event) {
        enrollmentService.save(event.getEnrollment());
        updateList();
        closeEditor();
        Notification.show("Enrollment saved");
    }

    private void deleteEnrollment(EnrollmentForm.DeleteEvent event) {
        enrollmentService.delete(event.getEnrollment());
        updateList();
        closeEditor();
        Notification.show("Enrollment deleted");
    }
}