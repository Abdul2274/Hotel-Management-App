package test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class BillingSystemUI2 {
    private JFrame frame;
    private JTable billingTable, savedBillsTable;
    private DefaultTableModel tableModel, savedTableModel;
    private String[] columns = {"Guest Name", "Total"};
    private JTextField guestNameField, roomChargeField, servicesField, discountsField;
    private JComboBox<String> splitTypeCombo, roomTypeCombo, paymentMethodCombo;
    private JCheckBox lateCheckoutCheckbox;
    private List<String[]> billData = new ArrayList<>();
    private List<String[]> savedBillData = new ArrayList<>();

    public BillingSystemUI2() {
        frame = new JFrame("Billing System");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Hotel Billing System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        frame.add(titleLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel currentBillsPanel = new JPanel(new BorderLayout());
        tableModel = new DefaultTableModel(columns, 0);
        billingTable = new JTable(tableModel);
        JScrollPane currentBillsScrollPane = new JScrollPane(billingTable);
        currentBillsPanel.add(currentBillsScrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0; gbc.gridy = 0;
        inputPanel.add(new JLabel("Guest Name (comma-separated for group):"), gbc);
        guestNameField = new JTextField(20);
        gbc.gridx = 1;
        inputPanel.add(guestNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        inputPanel.add(new JLabel("Room Charge:"), gbc);
        roomChargeField = new JTextField(20);
        gbc.gridx = 1;
        inputPanel.add(roomChargeField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        inputPanel.add(new JLabel("Additional Services Charge:"), gbc);
        servicesField = new JTextField(20);
        gbc.gridx = 1;
        inputPanel.add(servicesField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        inputPanel.add(new JLabel("Discounts:"), gbc);
        discountsField = new JTextField(20);
        gbc.gridx = 1;
        inputPanel.add(discountsField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        inputPanel.add(new JLabel("Billing Type:"), gbc);
        splitTypeCombo = new JComboBox<>(new String[]{"Individual", "Group"});
        gbc.gridx = 1;
        inputPanel.add(splitTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        inputPanel.add(new JLabel("Room Type:"), gbc);
        roomTypeCombo = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        gbc.gridx = 1;
        inputPanel.add(roomTypeCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        inputPanel.add(new JLabel("Payment Method:"), gbc);
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Card"});
        gbc.gridx = 1;
        inputPanel.add(paymentMethodCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 7;
        lateCheckoutCheckbox = new JCheckBox("Late Check-Out (+$30)");
        gbc.gridwidth = 2;
        inputPanel.add(lateCheckoutCheckbox, gbc);

        currentBillsPanel.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Generate Bill");
        JButton viewChangesButton = new JButton("View Bill Breakdown");
        buttonPanel.add(submitButton);
        buttonPanel.add(viewChangesButton);
        currentBillsPanel.add(buttonPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> generateBill());
        viewChangesButton.addActionListener(e -> showBillBreakdown());

        tabbedPane.addTab("Current Bills", currentBillsPanel);

        JPanel savedBillsPanel = new JPanel(new BorderLayout());
        savedTableModel = new DefaultTableModel(columns, 0);
        savedBillsTable = new JTable(savedTableModel);
        JScrollPane savedBillsScrollPane = new JScrollPane(savedBillsTable);
        savedBillsPanel.add(savedBillsScrollPane, BorderLayout.CENTER);

        JPanel savedButtonPanel = new JPanel();
        JButton retrieveBillsButton = new JButton("Retrieve Saved Bills");
        JButton viewSavedBreakdownButton = new JButton("View Bill Breakdown");
        savedButtonPanel.add(retrieveBillsButton);
        savedButtonPanel.add(viewSavedBreakdownButton);
        savedBillsPanel.add(savedButtonPanel, BorderLayout.SOUTH);

        retrieveBillsButton.addActionListener(e -> loadSavedBills());
        viewSavedBreakdownButton.addActionListener(e -> showSavedBillBreakdown());

        tabbedPane.addTab("Saved Bills", savedBillsPanel);
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void generateBill() {
        String guestNames = guestNameField.getText();
        String roomChargeText = roomChargeField.getText();
        String servicesText = servicesField.getText();
        String discountsText = discountsField.getText();
        String billingType = (String) splitTypeCombo.getSelectedItem();
        String roomType = (String) roomTypeCombo.getSelectedItem();

        if (!guestNames.isEmpty() && !roomChargeText.isEmpty() && !servicesText.isEmpty() && !discountsText.isEmpty()) {
            try {
                double baseRoomCharge = Double.parseDouble(roomChargeText);
                double roomCharge = baseRoomCharge;
                double services = Double.parseDouble(servicesText);
                double discounts = Double.parseDouble(discountsText);
                double seasonalMarkup = 0.0;

                if ("Double".equals(roomType)) {
                    roomCharge += 20.0;
                } else if ("Suite".equals(roomType)) {
                    roomCharge += 50.0;
                }

                if (lateCheckoutCheckbox.isSelected()) {
                    roomCharge += 30.0;
                }

                Month currentMonth = LocalDate.now().getMonth();
                if (currentMonth == Month.DECEMBER || currentMonth == Month.JULY) {
                    seasonalMarkup = roomCharge * 0.2;
                    roomCharge += seasonalMarkup;
                }

                double preTaxTotal = roomCharge + services;
                double tax = preTaxTotal * 0.12;
                double total = preTaxTotal + tax - discounts;

                if ("Individual".equals(billingType)) {
                    for (String name : guestNames.split(",")) {
                        name = name.trim();
                        if (!name.isEmpty()) {
                            String[] row = {name, String.format("%.2f", total)};
                            tableModel.addRow(row);
                            billData.add(new String[]{name, String.format("%.2f", baseRoomCharge),
                                    String.format("%.2f", seasonalMarkup),
                                    String.valueOf(lateCheckoutCheckbox.isSelected()),
                                    String.format("%.2f", roomCharge),
                                    String.format("%.2f", services),
                                    String.format("%.2f", discounts),
                                    String.format("%.2f", tax),
                                    String.format("%.2f", total), billingType, roomType});
                            saveBill(new String[]{name, String.format("%.2f", roomCharge),
                                    String.format("%.2f", services),
                                    String.format("%.2f", discounts),
                                    String.format("%.2f", tax),
                                    String.format("%.2f", total)});
                        }
                    }
                } else {
                    String groupLabel = guestNames.trim();
                    String[] row = {groupLabel, String.format("%.2f", total)};
                    tableModel.addRow(row);
                    billData.add(new String[]{groupLabel, String.format("%.2f", baseRoomCharge),
                            String.format("%.2f", seasonalMarkup),
                            String.valueOf(lateCheckoutCheckbox.isSelected()),
                            String.format("%.2f", roomCharge),
                            String.format("%.2f", services),
                            String.format("%.2f", discounts),
                            String.format("%.2f", tax),
                            String.format("%.2f", total), billingType, roomType});
                    saveBill(new String[]{groupLabel, String.format("%.2f", roomCharge),
                            String.format("%.2f", services),
                            String.format("%.2f", discounts),
                            String.format("%.2f", tax),
                            String.format("%.2f", total)});
                }

                clearFields();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Please enter valid numeric values.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveBill(String[] bill) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO billing (guest_name, room_charge, services, discounts, tax, total) VALUES (?, ?, ?, ?, ?, ?)")) {
            stmt.setString(1, bill[0]);
            stmt.setDouble(2, Double.parseDouble(bill[1]));
            stmt.setDouble(3, Double.parseDouble(bill[2]));
            stmt.setDouble(4, Double.parseDouble(bill[3]));
            stmt.setDouble(5, Double.parseDouble(bill[4]));
            stmt.setDouble(6, Double.parseDouble(bill[5]));
            stmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to save bill: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showBillBreakdown() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a bill to view.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        showBreakdownFromData(billData.get(selectedRow));
    }

    private void showSavedBillBreakdown() {
        int selectedRow = savedBillsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a saved bill to view.", "No Selection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        showBreakdownFromData(savedBillData.get(selectedRow));
    }

    private void showBreakdownFromData(String[] data) {
        String guestList = data[0];
        double baseRoom = Double.parseDouble(data[1]);
        double markup = Double.parseDouble(data[2]);
        boolean isLate = Boolean.parseBoolean(data[3]);
        double finalRoom = Double.parseDouble(data[4]);
        double services = Double.parseDouble(data[5]);
        double discounts = Double.parseDouble(data[6]);
        double tax = Double.parseDouble(data[7]);
        double total = Double.parseDouble(data[8]);
        String billingType = data[9];
        String roomType = data[10];

        StringBuilder breakdown = new StringBuilder();
        breakdown.append("===== BILL BREAKDOWN =====\n")
                .append("Guests: ").append(guestList).append("\n")
                .append("Billing Type: ").append(billingType).append("\n")
                .append("Room Type: ").append(roomType).append("\n\n")
                .append(String.format("Room Charge (Base): $%.2f\n", baseRoom))
                .append(String.format("Seasonal Markup: $%.2f\n", markup));

        if (isLate) breakdown.append("Late Check-Out Surcharge: $30.00\n");

        breakdown.append(String.format("Room Charge (Final): $%.2f\n", finalRoom))
                .append(String.format("Additional Services: $%.2f\n", services))
                .append(String.format("Tax (12%%): $%.2f\n", tax))
                .append(String.format("Discounts: -$%.2f\n", discounts))
                .append("----------------------------\n")
                .append(String.format("Total: $%.2f\n", total));

        if ("Group".equalsIgnoreCase(billingType)) {
            String[] guests = guestList.split(",");
            double perPerson = total / guests.length;
            breakdown.append("\nPer Person Share:\n");
            for (String g : guests) {
                breakdown.append(g.trim()).append(": $").append(String.format("%.2f", perPerson)).append("\n");
            }
        }

        JOptionPane.showMessageDialog(frame, breakdown.toString(), "Bill Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadSavedBills() {
        savedTableModel.setRowCount(0);
        savedBillData.clear();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT guest_name, room_charge, services, discounts, tax, total FROM billing");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String guestName = rs.getString("guest_name");
                double roomCharge = rs.getDouble("room_charge");
                double services = rs.getDouble("services");
                double discounts = rs.getDouble("discounts");
                double tax = rs.getDouble("tax");
                double total = rs.getDouble("total");

                String[] rowData = {guestName, String.format("%.2f", total)};
                savedTableModel.addRow(rowData);

                String[] fullData = {guestName, "0.00", "0.00", "false", String.format("%.2f", roomCharge),
                        String.format("%.2f", services), String.format("%.2f", discounts),
                        String.format("%.2f", tax), String.format("%.2f", total), "", ""};
                savedBillData.add(fullData);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to retrieve bills: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void clearFields() {
    	guestNameField.setText(""); 
        roomChargeField.setText("");
        servicesField.setText("");
        discountsField.setText("");
        lateCheckoutCheckbox.setSelected(false);
        splitTypeCombo.setSelectedIndex(0);       // Reset to "Individual"
        roomTypeCombo.setSelectedIndex(0);        // Reset to "Single"
        paymentMethodCombo.setSelectedIndex(0);   // Reset to "Cash"
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BillingSystemUI2::new);
    }
}