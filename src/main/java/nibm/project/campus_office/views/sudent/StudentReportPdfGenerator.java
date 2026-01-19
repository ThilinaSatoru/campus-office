package nibm.project.campus_office.views.sudent;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import nibm.project.campus_office.entity.*;
import nibm.project.campus_office.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class StudentReportPdfGenerator {

    private final StudentRepository studentRepository;

    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb SECTION_COLOR = new DeviceRgb(52, 152, 219);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public StudentReportPdfGenerator(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public byte[] generateStudentReport(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Initialize lazy collections
            student.getEnrollments().size();
            student.getPayments().size();
            student.getInteractions().size();

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Header
            addHeader(document, student);

            // Basic Information
            addBasicInfo(document, student);

            // Enrollments
            addEnrollments(document, student);

            // Payments
            addPayments(document, student);

            // Interactions
            addInteractions(document, student);

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error generating PDF report: " + e.getMessage(), e);
        }
    }

    private void addHeader(Document document, Student student) {
        Paragraph header = new Paragraph("STUDENT REPORT")
                .setFontSize(24)
                .setBold()
                .setFontColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(10);
        document.add(header);

        Paragraph studentName = new Paragraph(student.getFirstName() + " " + student.getLastName())
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(studentName);
    }

    private void addBasicInfo(Document document, Student student) {
        addSectionTitle(document, "Basic Information");

        Table table = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .useAllAvailableWidth();

        addInfoRow(table, "Student ID:", student.getStudentId());
        addInfoRow(table, "Email:", student.getEmail());
        addInfoRow(table, "Phone:", student.getPhone());
        addInfoRow(table, "Status:", student.getStatus() != null ? student.getStatus().toString() : "N/A");
        addInfoRow(table, "Enrollment Date:",
                student.getEnrollmentDate() != null ? student.getEnrollmentDate().format(DATE_FORMATTER) : "N/A");
        addInfoRow(table, "Graduation Date:",
                student.getGraduationDate() != null ? student.getGraduationDate().format(DATE_FORMATTER) : "N/A");

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addEnrollments(Document document, Student student) {
        addSectionTitle(document, "Course Enrollments");

        if (student.getEnrollments().isEmpty()) {
            document.add(new Paragraph("No enrollments found.").setItalic());
            document.add(new Paragraph("\n"));
            return;
        }

        Table table = new Table(UnitValue.createPercentArray(new float[]{20, 30, 15, 15, 20}))
                .useAllAvailableWidth();

        addTableHeader(table, "Course Code", "Course Title", "Status", "Grade", "Enrollment Date");

        for (Enrollment enrollment : student.getEnrollments()) {
            Course course = enrollment.getCourse();
            table.addCell(createCell(course != null ? course.getCourseCode() : "N/A"));
            table.addCell(createCell(course != null ? course.getTitle() : "N/A"));
            table.addCell(createCell(enrollment.getStatus() != null ? enrollment.getStatus().toString() : "N/A"));
            table.addCell(createCell(enrollment.getGrade() != null ? String.format("%.2f", enrollment.getGrade()) : "N/A"));
            table.addCell(createCell(enrollment.getEnrollmentDate() != null ?
                    enrollment.getEnrollmentDate().format(DATE_FORMATTER) : "N/A"));
        }

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addPayments(Document document, Student student) {
        addSectionTitle(document, "Payment History");

        if (student.getPayments().isEmpty()) {
            document.add(new Paragraph("No payments found.").setItalic());
            document.add(new Paragraph("\n"));
            return;
        }

        Table table = new Table(UnitValue.createPercentArray(new float[]{15, 15, 15, 15, 20, 20}))
                .useAllAvailableWidth();

        addTableHeader(table, "Amount", "Status", "Method", "Payment Date", "Due Date", "Transaction ID");

        for (Payment payment : student.getPayments()) {
            table.addCell(createCell(payment.getAmount() != null ? "Rs. " + payment.getAmount() : "N/A"));
            table.addCell(createCell(payment.getStatus() != null ? payment.getStatus().toString() : "N/A"));
            table.addCell(createCell(payment.getMethod() != null ? payment.getMethod().toString() : "N/A"));
            table.addCell(createCell(payment.getPaymentDate() != null ?
                    payment.getPaymentDate().format(DATE_FORMATTER) : "N/A"));
            table.addCell(createCell(payment.getDueDate() != null ?
                    payment.getDueDate().format(DATE_FORMATTER) : "N/A"));
            table.addCell(createCell(payment.getTransactionId() != null ? payment.getTransactionId() : "N/A"));
        }

        document.add(table);
        document.add(new Paragraph("\n"));
    }

    private void addInteractions(Document document, Student student) {
        addSectionTitle(document, "Interaction History");

        if (student.getInteractions().isEmpty()) {
            document.add(new Paragraph("No interactions found.").setItalic());
            document.add(new Paragraph("\n"));
            return;
        }

        Table table = new Table(UnitValue.createPercentArray(new float[]{15, 25, 20, 20, 20}))
                .useAllAvailableWidth();

        addTableHeader(table, "Type", "Subject", "Date", "Contacted By", "Notes");

        for (Interaction interaction : student.getInteractions()) {
            table.addCell(createCell(interaction.getType() != null ? interaction.getType().toString() : "N/A"));
            table.addCell(createCell(interaction.getSubject()));
            table.addCell(createCell(interaction.getInteractionDate() != null ?
                    interaction.getInteractionDate().format(DATETIME_FORMATTER) : "N/A"));
            table.addCell(createCell(interaction.getContactedBy() != null ? interaction.getContactedBy() : "N/A"));
            table.addCell(createCell(interaction.getNotes() != null ?
                    (interaction.getNotes().length() > 50 ? interaction.getNotes().substring(0, 47) + "..." : interaction.getNotes())
                    : "N/A"));
        }

        document.add(table);
    }

    private void addSectionTitle(Document document, String title) {
        Paragraph section = new Paragraph(title)
                .setFontSize(14)
                .setBold()
                .setFontColor(SECTION_COLOR)
                .setMarginBottom(10)
                .setMarginTop(5);
        document.add(section);
    }

    private void addInfoRow(Table table, String label, String value) {
        table.addCell(createCell(label).setBold());
        table.addCell(createCell(value != null ? value : "N/A"));
    }

    private void addTableHeader(Table table, String... headers) {
        for (String header : headers) {
            table.addHeaderCell(createCell(header)
                    .setBold()
                    .setBackgroundColor(SECTION_COLOR)
                    .setFontColor(ColorConstants.WHITE));
        }
    }

    private Cell createCell(String content) {
        return new Cell().add(new Paragraph(content).setFontSize(10));
    }
}
