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
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
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

    private final Binder<Payment> binder = new BeanValidationBinder<>(Payment.class);
    private Payment payment;

    public PaymentForm(List<Student> students) {
        addClassName("payment-form");

        student.setItems(students);
        student.setItemLabelGenerator(s -> s.getFirstName() + " " + s.getLastName());

        status.setItems(PaymentStatus.values());
        method.setItems(PaymentMethod.values());

        amount.setPrefixComponent(new Span("$"));

        configureValidation();

        add(student, amount, dueDate, paymentDate, status, method, transactionId, notes, createButtonsLayout());
    }

    private void configureValidation() {
        // Student - Required
        binder.forField(student)
                .asRequired("Student is required")
                .bind(Payment::getStudent, Payment::setStudent);
        student.setHelperText("Select student for this payment");

        // Amount - Required, must be positive
        binder.forField(amount)
                .asRequired("Amount is required")
                .withValidator(value -> value != null && value > 0,
                        "Amount must be greater than 0")
                .withConverter(
                        value -> value != null ? BigDecimal.valueOf(value) : null,
                        value -> value != null ? value.doubleValue() : 0.0)
                .bind(Payment::getAmount, Payment::setAmount);
        amount.setHelperText("Enter payment amount");
        amount.setMin(0.01);
        amount.setStep(0.01);

        // Due Date - Required
        binder.forField(dueDate)
                .asRequired("Due date is required")
                .bind(Payment::getDueDate, Payment::setDueDate);
        dueDate.setHelperText("Select payment due date");

        // Payment Date
        binder.forField(paymentDate)
                .bind(Payment::getPaymentDate, Payment::setPaymentDate);
        paymentDate.setHelperText("Select actual payment date");

        // Status - Required
        binder.forField(status)
                .asRequired("Status is required")
                .bind(Payment::getStatus, Payment::setStatus);
        status.setHelperText("Select payment status");

        // Payment Method
        binder.forField(method)
                .bind(Payment::getMethod, Payment::setMethod);
        method.setHelperText("Select payment method");

        // Transaction ID
        binder.forField(transactionId)
                .bind(Payment::getTransactionId, Payment::setTransactionId);
        transactionId.setHelperText("Enter transaction reference ID");

        // Notes
        binder.forField(notes)
                .bind(Payment::getNotes, Payment::setNotes);
        notes.setHelperText("Add any additional notes");
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
        binder.readBean(payment);
    }

    private void validateAndSave() {
        try {
            if (payment == null) {
                payment = new Payment();
            }
            binder.writeBean(payment);
            fireEvent(new SaveEvent(this, payment));
        } catch (ValidationException e) {
            // Validation errors are automatically displayed on fields
        }
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
