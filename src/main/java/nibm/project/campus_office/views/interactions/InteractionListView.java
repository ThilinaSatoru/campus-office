package nibm.project.campus_office.views.interactions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.entity.Interaction;
import nibm.project.campus_office.repository.InteractionRepository;
import nibm.project.campus_office.repository.StudentRepository;
import nibm.project.campus_office.views.MainLayout;

@Route(value = "interactions", layout = MainLayout.class)
@PageTitle("Interactions | Diploma CRM")
@PermitAll
public class InteractionListView extends VerticalLayout {

    private final InteractionRepository interactionRepo;
    private final StudentRepository studentRepo;
    private final Grid<Interaction> grid = new Grid<>(Interaction.class, false);
    private InteractionForm form;

    public InteractionListView(InteractionRepository interactionRepo, StudentRepository studentRepo) {
        this.interactionRepo = interactionRepo;
        this.studentRepo = studentRepo;
        setSizeFull();

        configureGrid();
        configureForm();

        add(getToolbar(), getContent());
        updateList();
        closeEditor();
    }

    private void configureGrid() {
        grid.setSizeFull();
        grid.addColumn(i -> i.getStudent().getFirstName() + " " + i.getStudent().getLastName())
                .setHeader("Student").setSortable(true);
        grid.addColumn(Interaction::getType).setHeader("Type");
        grid.addColumn(Interaction::getSubject).setHeader("Subject").setSortable(true);
        grid.addColumn(i -> i.getInteractionDate() != null ? i.getInteractionDate().toString() : "")
                .setHeader("Date");
        grid.addColumn(Interaction::getContactedBy).setHeader("Contacted By");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editInteraction(e.getValue()));
    }

    private void configureForm() {
        form = new InteractionForm(studentRepo.findAll());
        form.setWidth("25em");
        form.addSaveListener(this::saveInteraction);
        form.addDeleteListener(this::deleteInteraction);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("Add Interaction");
        addButton.addClickListener(e -> addInteraction());
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
        grid.setItems(interactionRepo.findAll());
    }

    private void addInteraction() {
        grid.asSingleSelect().clear();
        editInteraction(new Interaction());
    }

    private void editInteraction(Interaction interaction) {
        if (interaction == null) {
            closeEditor();
        } else {
            form.setInteraction(interaction);
            form.setVisible(true);
        }
    }

    private void closeEditor() {
        form.setInteraction(null);
        form.setVisible(false);
    }

    private void saveInteraction(InteractionForm.SaveEvent event) {
        interactionRepo.save(event.getInteraction());
        updateList();
        closeEditor();
        Notification.show("Interaction saved");
    }

    private void deleteInteraction(InteractionForm.DeleteEvent event) {
        interactionRepo.delete(event.getInteraction());
        updateList();
        closeEditor();
        Notification.show("Interaction deleted");
    }
}
