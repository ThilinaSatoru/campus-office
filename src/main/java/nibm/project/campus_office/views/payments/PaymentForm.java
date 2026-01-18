package nibm.project.campus_office.views.payments;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import nibm.project.campus_office.entity.Payment;
import nibm.project.campus_office.entity.Student;
import nibm.project.campus_office.enums.PaymentMethod;
import nibm.project.campus_office.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.List;

public class PaymentForm extends FormLayout {

    ComboBox<Student> student = new ComboBox<>("Student");
    NumberField amount = new NumberField("Amount");
    DatePicker paymentDate = new DatePicker("Payment Date");
    DatePicker dueDate = new DatePicker("Due Date");
    ComboBox<PaymentStatus> status = new ComboBox<>("Status");
    ComboBox<PaymentMethod> method = new ComboBox<>("Payment Method");
    TextField transactionId = new TextField("Transaction ID");
    TextArea notes = new TextArea("Notes");

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    private Payment payment;

    public PaymentForm(List<Student> students) {
        addClassName("payment-form");

        student.setItems(students);
        student.setItemLabelGenerator(s -> s.getFirstName() + " " + s.getLastName());

        status.setItems(PaymentStatus.values());
        method.setItems(PaymentMethod.values());

        amount.setPrefixComponent(new Span("$"));

        add(student, amount, dueDate, paymentDate, status, method, transactionId, notes, createButtonsLayout());
    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickListener(e -> validateAndSave());
        delete.addClickListener(e -> fireEvent(new DeleteEvent(this, payment)));
        close.addClickListener(e -> fireEvent(new CloseEvent(this)));

        return new HorizontalLayout(save, delete, close);
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
        if (payment != null) {
            student.setValue(payment.getStudent());
            amount.setValue(payment.getAmount() != null ? payment.getAmount().doubleValue() : 0.0);
            paymentDate.setValue(payment.getPaymentDate());
            dueDate.setValue(payment.getDueDate());
            status.setValue(payment.getStatus());
            method.setValue(payment.getMethod());
            transactionId.setValue(payment.getTransactionId() != null ? payment.getTransactionId() : "");
            notes.setValue(payment.getNotes() != null ? payment.getNotes() : "");
        }
    }

    private void validateAndSave() {
        if (payment == null) payment = new Payment();

        payment.setStudent(student.getValue());
        payment.setAmount(BigDecimal.valueOf(amount.getValue()));
        payment.setPaymentDate(paymentDate.getValue());
        payment.setDueDate(dueDate.getValue());
        payment.setStatus(status.getValue());
        payment.setMethod(method.getValue());
        payment.setTransactionId(transactionId.getValue());
        payment.setNotes(notes.getValue());

        fireEvent(new SaveEvent(this, payment));
    }

    public static abstract class PaymentFormEvent extends ComponentEvent<PaymentForm> {
        private final Payment payment;

        protected PaymentFormEvent(PaymentForm source, Payment payment) {
            super(source, false);
            this.payment = payment;
        }

        public Payment getPayment() {
            return payment;
        }
    }

    public static class SaveEvent extends PaymentFormEvent {
        SaveEvent(PaymentForm source, Payment payment) {
            super(source, payment);
        }
    }

    public static class DeleteEvent extends PaymentFormEvent {
        DeleteEvent(PaymentForm source, Payment payment) {
            super(source, payment);
        }
    }

    public static class CloseEvent extends PaymentFormEvent {
        CloseEvent(PaymentForm source) {
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
