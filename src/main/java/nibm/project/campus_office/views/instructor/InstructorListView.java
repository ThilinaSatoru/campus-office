package nibm.project.campus_office.views.instructor;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.entity.Instructor;
import nibm.project.campus_office.repository.InstructorRepository;
import nibm.project.campus_office.views.MainLayout;

@Route(value = "instructors", layout = MainLayout.class)
@PageTitle("Instructors | Diploma CRM")
@PermitAll
public class InstructorListView extends VerticalLayout {

    private final InstructorRepository instructorRepo;
    private final Grid<Instructor> grid = new Grid<>(Instructor.class, false);
    private InstructorForm form;

    public InstructorListView(InstructorRepository instructorRepo) {
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
        grid.addColumn(Instructor::getFirstName).setHeader("First Name").setSortable(true);
        grid.addColumn(Instructor::getLastName).setHeader("Last Name").setSortable(true);
        grid.addColumn(Instructor::getEmail).setHeader("Email");
        grid.addColumn(Instructor::getPhone).setHeader("Phone");
        grid.addColumn(Instructor::getSpecialization).setHeader("Specialization");
        grid.addColumn(i -> i.getCourses().size()).setHeader("Courses");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editInstructor(e.getValue()));
    }

    private void configureForm() {
        form = new InstructorForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveInstructor);
        form.addDeleteListener(this::deleteInstructor);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("Add Instructor");
        addButton.addClickListener(e -> addInstructor());
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
        grid.setItems(instructorRepo.findAll());
    }

    private void addInstructor() {
        grid.asSingleSelect().clear();
        editInstructor(new Instructor());
    }

    private void editInstructor(Instructor instructor) {
        if (instructor == null) {
            closeEditor();
        } else {
            form.setInstructor(instructor);
            form.setVisible(true);
        }
    }

    private void closeEditor() {
        form.setInstructor(null);
        form.setVisible(false);
    }

    private void saveInstructor(InstructorForm.SaveEvent event) {
        instructorRepo.save(event.getInstructor());
        updateList();
        closeEditor();
        Notification.show("Instructor saved");
    }

    private void deleteInstructor(InstructorForm.DeleteEvent event) {
        instructorRepo.delete(event.getInstructor());
        updateList();
        closeEditor();
        Notification.show("Instructor deleted");
    }
}
