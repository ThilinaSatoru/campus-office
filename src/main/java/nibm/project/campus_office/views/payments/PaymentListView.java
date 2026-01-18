package nibm.project.campus_office.views.payments;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import nibm.project.campus_office.entity.Payment;
import nibm.project.campus_office.repository.PaymentRepository;
import nibm.project.campus_office.repository.StudentRepository;
import nibm.project.campus_office.views.MainLayout;

@Route(value = "payments", layout = MainLayout.class)
@PageTitle("Payments | Diploma CRM")
@PermitAll
public class PaymentListView extends VerticalLayout {

    private final PaymentRepository paymentRepo;
    private final StudentRepository studentRepo;
    private final Grid<Payment> grid = new Grid<>(Payment.class, false);
    private PaymentForm form;

    public PaymentListView(PaymentRepository paymentRepo, StudentRepository studentRepo) {
        this.paymentRepo = paymentRepo;
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
        grid.addColumn(p -> p.getStudent().getFirstName() + " " + p.getStudent().getLastName())
                .setHeader("Student").setSortable(true);
        grid.addColumn(Payment::getAmount).setHeader("Amount");
        grid.addColumn(p -> p.getPaymentDate() != null ? p.getPaymentDate().toString() : "")
                .setHeader("Payment Date");
        grid.addColumn(p -> p.getDueDate() != null ? p.getDueDate().toString() : "")
                .setHeader("Due Date");
        grid.addColumn(Payment::getStatus).setHeader("Status");
        grid.addColumn(Payment::getMethod).setHeader("Method");
        grid.addColumn(Payment::getTransactionId).setHeader("Transaction ID");

        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.asSingleSelect().addValueChangeListener(e -> editPayment(e.getValue()));
    }

    private void configureForm() {
        form = new PaymentForm(studentRepo.findAll());
        form.setWidth("25em");
        form.addSaveListener(this::savePayment);
        form.addDeleteListener(this::deletePayment);
        form.addCloseListener(e -> closeEditor());
    }

    private HorizontalLayout getToolbar() {
        Button addButton = new Button("Add Payment");
        addButton.addClickListener(e -> addPayment());
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
        grid.setItems(paymentRepo.findAll());
    }

    private void addPayment() {
        grid.asSingleSelect().clear();
        editPayment(new Payment());
    }

    private void editPayment(Payment payment) {
        if (payment == null) {
            closeEditor();
        } else {
            form.setPayment(payment);
            form.setVisible(true);
        }
    }

    private void closeEditor() {
        form.setPayment(null);
        form.setVisible(false);
    }

    private void savePayment(PaymentForm.SaveEvent event) {
        paymentRepo.save(event.getPayment());
        updateList();
        closeEditor();
        Notification.show("Payment saved");
    }

    private void deletePayment(PaymentForm.DeleteEvent event) {
        paymentRepo.delete(event.getPayment());
        updateList();
        closeEditor();
        Notification.show("Payment deleted");
    }
}