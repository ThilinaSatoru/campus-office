package nibm.project.campus_office.views.sudent;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.VaadinSession;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.entity.Student;
import nibm.project.campus_office.repository.StudentRepository;
import nibm.project.campus_office.views.MainLayout;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.stream.Collectors;

@Route(value = "students", layout = MainLayout.class)
@PageTitle("Students | Diploma CRM")
@PermitAll
public class StudentListView extends VerticalLayout {

    private final StudentRepository studentRepository;
    private final Grid<Student> grid = new Grid<>(Student.class, false);
    private final TextField filterText = new TextField();
    private StudentForm form;

    private final StudentReportPdfGenerator pdfGenerator;

    public StudentListView(StudentRepository studentRepository, StudentReportPdfGenerator pdfGenerator) {
        this.studentRepository = studentRepository;
        this.pdfGenerator = pdfGenerator;
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.addClassName("student-grid");
        grid.setSizeFull();
        grid.addColumn(Student::getStudentId).setHeader("Student ID").setSortable(true);
        grid.addColumn(Student::getFirstName).setHeader("First Name").setSortable(true);
        grid.addColumn(Student::getLastName).setHeader("Last Name").setSortable(true);
        grid.addColumn(Student::getEmail).setHeader("Email");
        grid.addColumn(Student::getPhone).setHeader("Phone");
        grid.addColumn(Student::getStatus).setHeader("Status");
        grid.addColumn(s -> s.getEnrollmentDate() != null ? s.getEnrollmentDate().toString() : "")
                .setHeader("Enrollment Date");

        grid.addComponentColumn(student -> {
            Button downloadBtn = new Button(new Icon(VaadinIcon.DOWNLOAD));
            downloadBtn.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            downloadBtn.addClickListener(e -> downloadStudentReport(student));
            downloadBtn.getElement().setAttribute("aria-label", "Download PDF");
            return downloadBtn;
        }).setHeader("Actions").setAutoWidth(true);

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editStudent(e.getValue()));
    }

    private void configureForm() {
        form = new StudentForm();
        form.setWidth("25em");
        form.addSaveListener(this::saveStudent);
        form.addDeleteListener(this::deleteStudent);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder("Filter by name...");
        filterText.setClearButtonVisible(true);
        filterText.setValueChangeMode(ValueChangeMode.LAZY);
        filterText.addValueChangeListener(e -> updateList());

        Button addButton = new Button("Add Student");
        addButton.addClickListener(e -> addStudent());

        HorizontalLayout toolbar = new HorizontalLayout(filterText, addButton);
        toolbar.addClassName("toolbar");
        return toolbar;
    }

    private Component getContent() {
        HorizontalLayout content = new HorizontalLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.addClassName("content");
        content.setSizeFull();
        return content;
    }

    private void updateList() {
        List<Student> students = studentRepository.findAll();
        if (!filterText.isEmpty()) {
            String filter = filterText.getValue().toLowerCase();
            students = students.stream()
                    .filter(s -> s.getFirstName().toLowerCase().contains(filter) ||
                            s.getLastName().toLowerCase().contains(filter))
                    .collect(Collectors.toList());
        }
        grid.setItems(students);
    }

    private void addStudent() {
        grid.asSingleSelect().clear();
        editStudent(new Student());
    }

    private void editStudent(Student student) {
        if (student == null) {
            closeEditor();
        } else {
            form.setStudent(student);
            form.setVisible(true);
            addClassName("editing");
        }
    }

    private void closeEditor() {
        form.setStudent(null);
        form.setVisible(false);
        removeClassName("editing");
    }

    private void saveStudent(StudentForm.SaveEvent event) {
        studentRepository.save(event.getStudent());
        updateList();
        closeEditor();
        Notification.show("Student saved successfully");
    }

    private void deleteStudent(StudentForm.DeleteEvent event) {
        studentRepository.delete(event.getStudent());
        updateList();
        closeEditor();
        Notification.show("Student deleted");
    }

    private void downloadStudentReport(Student student) {
        try {
            byte[] pdfBytes = pdfGenerator.generateStudentReport(student.getId());

            StreamResource resource = new StreamResource(
                    "student_report_" + student.getStudentId() + ".pdf",
                    () -> new ByteArrayInputStream(pdfBytes)
            );
            resource.setContentType("application/pdf");

            // Register the resource with the session
            var registration = VaadinSession.getCurrent().getResourceRegistry().registerResource(resource);

            // Open in new tab using the registered URL
            UI.getCurrent().getPage().executeJs("window.open($0, '_blank')", registration.getResourceUri().toString());

            Notification.show("Opening report in new tab");
        } catch (Exception e) {
            Notification.show("Error generating report: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
            e.printStackTrace();
        }
    }
}