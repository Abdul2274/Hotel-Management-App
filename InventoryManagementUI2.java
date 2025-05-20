package test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class InventoryManagementUI2 {
    private JFrame frame;
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private String[] columns = {"Item ID", "Item Name", "Department", "Stock Level"};
    private JTextField itemNameField, departmentField, stockLevelField;

    public InventoryManagementUI2() {
        frame = new JFrame("Inventory Management");
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Hotel Inventory Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(columns, 0);
        inventoryTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Item Name:"));
        itemNameField = new JTextField();
        inputPanel.add(itemNameField);
        inputPanel.add(new JLabel("Department:"));
        departmentField = new JTextField();
        inputPanel.add(departmentField);
        inputPanel.add(new JLabel("Stock Level:"));
        stockLevelField = new JTextField();
        inputPanel.add(stockLevelField);
        frame.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Add Item");
        JButton generateReportButton = new JButton("Generate Report");
        buttonPanel.add(submitButton);
        buttonPanel.add(generateReportButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                collectAndDeployInventoryData();
            }
        });

        generateReportButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateUsageReport();
            }
        });

        loadInventory();
        frame.setVisible(true);
    }

    private void loadInventory() {
        tableModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, item_name, department, stock_level FROM inventory")) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("item_name"),
                    rs.getString("department"),
                    rs.getInt("stock_level")
                });
                if (rs.getInt("stock_level") < 10) {
                    JOptionPane.showMessageDialog(frame, "Low stock alert for item: " + rs.getString("item_name"), "Low Stock Alert", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void collectAndDeployInventoryData() {
        String itemName = itemNameField.getText();
        String department = departmentField.getText();
        String stockLevel = stockLevelField.getText();

        if (!itemName.isEmpty() && !department.isEmpty() && !stockLevel.isEmpty()) {
            deployInventoryDataToBackend(itemName, department, stockLevel);
        } else {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deployInventoryDataToBackend(String itemName, String department, String stockLevel) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO inventory (item_name, department, stock_level) VALUES (?, ?, ?)")) {
            stmt.setString(1, itemName);
            stmt.setString(2, department);
            stmt.setInt(3, Integer.parseInt(stockLevel));
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        loadInventory();
    }

    private void generateUsageReport() {
        StringBuilder report = new StringBuilder("Inventory Usage Report:\n\n");
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT department, SUM(stock_level) AS total_stock FROM inventory GROUP BY department")) {
            while (rs.next()) {
                report.append("Department: ").append(rs.getString("department"))
                      .append(", Total Stock: ").append(rs.getInt("total_stock"))
                      .append("\n");
            }
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(frame, report.toString(), "Usage Report", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InventoryManagementUI2();
            }
        });
    }
}
