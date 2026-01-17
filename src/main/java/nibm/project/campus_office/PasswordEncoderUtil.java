package nibm.project.campus_office;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // Test passwords
        String[] passwords = {"admin", "staff123", "instructor456"};

        System.out.println("=== BCrypt Password Encoder Test ===\n");

        for (String password : passwords) {
            String encoded = encoder.encode(password);
            System.out.println("Plain text: " + password);
            System.out.println("Encoded:    " + encoded);
            System.out.println("Matches:    " + encoder.matches(password, encoded));
            System.out.println();
        }

        // SQL Insert statements
        System.out.println("=== SQL INSERT Statements ===\n");
        System.out.println("-- Admin user (password: admin)");
        System.out.println("INSERT INTO users (username, password, first_name, last_name, email, role, enabled, account_non_locked, active, created_at, updated_at)");
        System.out.println("VALUES ('admin', '" + encoder.encode("admin") + "', 'Admin', 'User', 'admin@example.com', 'ADMIN', true, true, true, NOW(), NOW());\n");

        System.out.println("-- Staff user (password: staff123)");
        System.out.println("INSERT INTO users (username, password, first_name, last_name, email, role, enabled, account_non_locked, active, created_at, updated_at)");
        System.out.println("VALUES ('staff', '" + encoder.encode("staff123") + "', 'Staff', 'Member', 'staff@example.com', 'STAFF', true, true, true, NOW(), NOW());\n");

        System.out.println("-- Instructor user (password: instructor456)");
        System.out.println("INSERT INTO users (username, password, first_name, last_name, email, role, enabled, account_non_locked, active, created_at, updated_at)");
        System.out.println("VALUES ('instructor', '" + encoder.encode("instructor456") + "', 'John', 'Doe', 'instructor@example.com', 'INSTRUCTOR', true, true, true, NOW(), NOW());");
    }
}
