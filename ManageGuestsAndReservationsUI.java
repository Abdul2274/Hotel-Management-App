package test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ManageGuestsAndReservationsUI {
    private JFrame frame;
    private JTable guestsTable, reservationsTable;
    private DefaultTableModel guestsTableModel, reservationsTableModel;
    private String[] guestColumns = {"Guest ID", "Name", "Email", "Phone"};
    private String[] reservationColumns = {
        "Reservation ID", "Room Number", "Guest Name", "Email", "Phone",
        "Check-in", "Check-out", "Room Preferences", "Payment Status", "Special Requests", "Late Checkout"
    };
    private JTextField nameField, emailField, phoneField;
    private JTextField roomNumberField, guestNameField, checkinField, checkoutField,
                       roomPreferencesField, paymentStatusField, specialRequestsField,
                       reservationEmailField, reservationPhoneField;
    private JCheckBox lateCheckoutCheckbox;

    public ManageGuestsAndReservationsUI() {
        frame = new JFrame("Manage Guests and Reservations");
        frame.setSize(950, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Guest and Reservations Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Guests Tab
        JPanel guestsPanel = new JPanel(new BorderLayout());
        guestsTableModel = new DefaultTableModel(guestColumns, 0);
        guestsTable = new JTable(guestsTableModel);
        JScrollPane guestsScrollPane = new JScrollPane(guestsTable);
        guestsPanel.add(guestsScrollPane, BorderLayout.CENTER);

        JPanel guestsInputPanel = new JPanel(new GridLayout(3, 2));
        guestsInputPanel.add(new JLabel("Guest Name:"));
        nameField = new JTextField();
        guestsInputPanel.add(nameField);
        guestsInputPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        guestsInputPanel.add(emailField);
        guestsInputPanel.add(new JLabel("Phone:"));
        phoneField = new JTextField();
        guestsInputPanel.add(phoneField);
        guestsPanel.add(guestsInputPanel, BorderLayout.NORTH);

        JPanel guestsButtonPanel = new JPanel();
        JButton submitGuestButton = new JButton("Submit");
        JButton viewGuestsButton = new JButton("View Guests");
        JButton guestHistoryButton = new JButton("View History");
        guestsButtonPanel.add(submitGuestButton);
        guestsButtonPanel.add(viewGuestsButton);
        guestsButtonPanel.add(guestHistoryButton);
        guestsPanel.add(guestsButtonPanel, BorderLayout.SOUTH);

        submitGuestButton.addActionListener(e -> collectAndDeployGuestData());
        viewGuestsButton.addActionListener(e -> loadGuests());
        guestHistoryButton.addActionListener(e -> showGuestHistory());

        tabbedPane.addTab("Guests", guestsPanel);

        // Reservations Tab
        JPanel reservationsPanel = new JPanel(new BorderLayout());
        reservationsTableModel = new DefaultTableModel(reservationColumns, 0);
        reservationsTable = new JTable(reservationsTableModel);
        JScrollPane reservationsScrollPane = new JScrollPane(reservationsTable);
        reservationsPanel.add(reservationsScrollPane, BorderLayout.CENTER);

        JPanel reservationsInputPanel = new JPanel(new GridLayout(11, 2));
        reservationsInputPanel.add(new JLabel("Room Number:"));
        roomNumberField = new JTextField();
        reservationsInputPanel.add(roomNumberField);
        reservationsInputPanel.add(new JLabel("Guest Name:"));
        guestNameField = new JTextField();
        reservationsInputPanel.add(guestNameField);
        reservationsInputPanel.add(new JLabel("Email:"));
        reservationEmailField = new JTextField();
        reservationsInputPanel.add(reservationEmailField);
        reservationsInputPanel.add(new JLabel("Phone:"));
        reservationPhoneField = new JTextField();
        reservationsInputPanel.add(reservationPhoneField);
        reservationsInputPanel.add(new JLabel("Check-in Date (YYYY-MM-DD):"));
        checkinField = new JTextField();
        reservationsInputPanel.add(checkinField);
        reservationsInputPanel.add(new JLabel("Check-out Date (YYYY-MM-DD):"));
        checkoutField = new JTextField();
        reservationsInputPanel.add(checkoutField);
        reservationsInputPanel.add(new JLabel("Room Preferences:"));
        roomPreferencesField = new JTextField();
        reservationsInputPanel.add(roomPreferencesField);
        reservationsInputPanel.add(new JLabel("Payment Status:"));
        paymentStatusField = new JTextField();
        reservationsInputPanel.add(paymentStatusField);
        reservationsInputPanel.add(new JLabel("Special Requests:"));
        specialRequestsField = new JTextField();
        reservationsInputPanel.add(specialRequestsField);
        reservationsInputPanel.add(new JLabel("Late Check-Out:"));
        lateCheckoutCheckbox = new JCheckBox();
        reservationsInputPanel.add(lateCheckoutCheckbox);
        reservationsPanel.add(reservationsInputPanel, BorderLayout.NORTH);

        JPanel reservationsButtonPanel = new JPanel();
        JButton submitReservationButton = new JButton("Submit");
        JButton viewReservationsButton = new JButton("View Reservations");
        JButton deleteReservationButton = new JButton("Delete");
        JButton searchReservationButton = new JButton("Search");
        reservationsButtonPanel.add(submitReservationButton);
        reservationsButtonPanel.add(viewReservationsButton);
        reservationsButtonPanel.add(deleteReservationButton);
        reservationsButtonPanel.add(searchReservationButton);
        reservationsPanel.add(reservationsButtonPanel, BorderLayout.SOUTH);

        submitReservationButton.addActionListener(e -> collectAndDeployReservationData());
        viewReservationsButton.addActionListener(e -> loadReservations());
        deleteReservationButton.addActionListener(e -> deleteReservation());
        searchReservationButton.addActionListener(e -> searchReservations());

        tabbedPane.addTab("Reservations", reservationsPanel);

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void assignRoomToGuest(String roomNumber) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE rooms SET status = 'Occupied' WHERE room_number = ?")) {
            stmt.setString(1, roomNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to assign room: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void collectAndDeployGuestData() {
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        if (!name.isEmpty() && !email.isEmpty() && !phone.isEmpty()) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO guests (name, email, phone) VALUES (?, ?, ?)")) {
                stmt.setString(1, name);
                stmt.setString(2, email);
                stmt.setString(3, phone);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadGuests();
        } else {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void collectAndDeployReservationData() {
        String roomNumber = roomNumberField.getText();
        String guestName = guestNameField.getText();
        String email = reservationEmailField.getText();
        String phone = reservationPhoneField.getText();
        String checkin = checkinField.getText();
        String checkout = checkoutField.getText();
        String roomPreferences = roomPreferencesField.getText();
        String paymentStatus = paymentStatusField.getText();
        String specialRequests = specialRequestsField.getText();
        boolean lateCheckout = lateCheckoutCheckbox.isSelected();

        if (!roomNumber.isEmpty() && !guestName.isEmpty() && !email.isEmpty() && !phone.isEmpty()
                && !checkin.isEmpty() && !checkout.isEmpty()) {
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO reservations (room_number, guest_name, email, phone, checkin_date, checkout_date, room_preferences, payment_status, special_requests, late_checkout) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                stmt.setString(1, roomNumber);
                stmt.setString(2, guestName);
                stmt.setString(3, email);
                stmt.setString(4, phone);
                stmt.setString(5, checkin);
                stmt.setString(6, checkout);
                stmt.setString(7, roomPreferences);
                stmt.setString(8, paymentStatus);
                stmt.setString(9, specialRequests);
                stmt.setBoolean(10, lateCheckout);
                stmt.executeUpdate();
                assignRoomToGuest(roomNumber);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadReservations();
        } else {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadGuests() {
        guestsTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM guests")) {
            while (rs.next()) {
                guestsTableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadReservations() {
        reservationsTableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM reservations")) {
            while (rs.next()) {
                reservationsTableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("room_number"),
                    rs.getString("guest_name"),
                    rs.getString("email"),
                    rs.getString("phone"),
                    rs.getString("checkin_date"),
                    rs.getString("checkout_date"),
                    rs.getString("room_preferences"),
                    rs.getString("payment_status"),
                    rs.getString("special_requests"),
                    rs.getBoolean("late_checkout") ? "Yes" : "No"
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteReservation() {
        int selectedRow = reservationsTable.getSelectedRow();
        if (selectedRow != -1) {
            int reservationId = (int) reservationsTableModel.getValueAt(selectedRow, 0);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM reservations WHERE id = ?")) {
                stmt.setInt(1, reservationId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            loadReservations();
        }
    }

    private void searchReservations() {
        String query = JOptionPane.showInputDialog(frame, "Enter guest name or room number:");
        if (query != null && !query.trim().isEmpty()) {
            reservationsTableModel.setRowCount(0);
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reservations WHERE guest_name LIKE ? OR room_number LIKE ?")) {
                stmt.setString(1, "%" + query + "%");
                stmt.setString(2, "%" + query + "%");
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    reservationsTableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("room_number"),
                        rs.getString("guest_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("checkin_date"),
                        rs.getString("checkout_date"),
                        rs.getString("room_preferences"),
                        rs.getString("payment_status"),
                        rs.getString("special_requests"),
                        rs.getBoolean("late_checkout") ? "Yes" : "No"
                    });
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

   private void showGuestHistory() {
        int selectedRow = guestsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a guest from the table.");
            return;
        }

        String guestName = guestsTableModel.getValueAt(selectedRow, 1).toString();

        StringBuilder history = new StringBuilder("Reservation History for " + guestName + ":\n\n");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM reservations WHERE guest_name = ?")) {
            stmt.setString(1, guestName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                history.append("Room: ").append(rs.getString("room_number"))
                       .append(", Check-in: ").append(rs.getString("checkin_date"))
                       .append(", Check-out: ").append(rs.getString("checkout_date"))
                       .append(", Payment: ").append(rs.getString("payment_status"))
                       .append(", Late Check-Out: ").append(rs.getBoolean("late_checkout") ? "Yes" : "No")
                       .append("\n");
            }
            JOptionPane.showMessageDialog(frame, history.toString(), "Guest History", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }  
   

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageGuestsAndReservationsUI::new);
    }
}
