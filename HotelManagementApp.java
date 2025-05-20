package test;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

// Mock Models
class User {
    private String email;
    private String password;
    private String role;

    public User(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
}

// Mock Data Store
class MockDatabase {
    private static List<User> users = new ArrayList<>();
    static {
        users.add(new User("admin@example.com", "admin123", "ADMIN"));
        users.add(new User("reception@example.com", "reception123", "RECEPTIONIST"));
    }
    public static User authenticate(String email, String password) {
        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}

// Swing-based User Interface
public class HotelManagementApp {
    public static void main(String[] args) {
        new LoginUI();
    }
}

class LoginUI {
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;

    public LoginUI() {
        frame = new JFrame("Hotel Management System - Login");
        frame.setSize(400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2));

        frame.add(new JLabel("Email:"));
        emailField = new JTextField();
        frame.add(emailField);

        frame.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        frame.add(passwordField);

        JButton loginButton = new JButton("Login");
        frame.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());
                User user = MockDatabase.authenticate(email, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(frame, "Login Successful!");
                    frame.dispose();
                    new DashboardUI(user);
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        frame.setVisible(true);
    }
}

class DashboardUI {
    private JFrame frame;

    public DashboardUI(User user) {
        frame = new JFrame("Hotel Management System - Dashboard");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 2));

        frame.add(new JLabel("Welcome, " + user.getRole() + "!"));
        JButton reservationButton = new JButton("Manage Reservations");
        JButton roomButton = new JButton("Manage Rooms");
        JButton inventoryButton = new JButton("Manage Inventory");
        JButton billingButton = new JButton("Manage Billing");

        frame.add(reservationButton);
        frame.add(roomButton);
        frame.add(inventoryButton);
        frame.add(billingButton);

        reservationButton.addActionListener(e -> new ManageGuestsAndReservationsUI());
        roomButton.addActionListener(e -> new ManageRoomsUI2());
        inventoryButton.addActionListener(e -> new InventoryManagementUI2());
        billingButton.addActionListener(e -> new BillingSystemUI2());

        frame.setVisible(true);
    }
}

class ManageReservationsUI {
    private JFrame frame;
    public ManageReservationsUI() {
        frame = new JFrame("Manage Reservations");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}

class ManageRoomsUI {
    private JFrame frame;
    public ManageRoomsUI() {
        frame = new JFrame("Manage Rooms");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}

class InventoryManagementUI {
    private JFrame frame;
    public InventoryManagementUI() {
        frame = new JFrame("Manage Inventory");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}

class BillingSystemUI {
    private JFrame frame;
    public BillingSystemUI() {
        frame = new JFrame("Manage Billing");
        frame.setSize(500, 300);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}
