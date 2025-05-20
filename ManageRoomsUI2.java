package test;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ManageRoomsUI2 {
    private JFrame frame;
    private JTable roomsTable;
    private DefaultTableModel tableModel;
    private String[] columns = {"Room Number", "Type", "Status", "Housekeeping Task", "Price"};
    private JTextField roomNumberField;
    private JComboBox<String> roomTypeCombo;
    private JComboBox<String> roomStatusCombo;
    private JComboBox<String> housekeepingTaskCombo;
    private List<String[]> roomData = new ArrayList<>();

    public ManageRoomsUI2() {
        frame = new JFrame("Manage Rooms");
        frame.setSize(800, 450);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Room Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        frame.add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(columns, 0);
        roomsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(roomsTable);
        frame.add(scrollPane, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new GridLayout(5, 2));
        inputPanel.add(new JLabel("Room Number:"));
        roomNumberField = new JTextField();
        inputPanel.add(roomNumberField);

        inputPanel.add(new JLabel("Room Type (Single, Double, Suite):"));
        roomTypeCombo = new JComboBox<>(new String[]{"Single", "Double", "Suite"});
        inputPanel.add(roomTypeCombo);

        inputPanel.add(new JLabel("Room Status:"));
        roomStatusCombo = new JComboBox<>(new String[]{"Available", "Occupied", "Maintenance"});
        inputPanel.add(roomStatusCombo);

        inputPanel.add(new JLabel("Housekeeping Task:"));
        housekeepingTaskCombo = new JComboBox<>(new String[]{"None", "Cleaning", "Inspection"});
        inputPanel.add(housekeepingTaskCombo);
        frame.add(inputPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton submitButton = new JButton("Submit");
        JButton maintenanceDoneButton = new JButton("Maintenance Completed");
        JButton flagMaintenanceButton = new JButton("Flag for Maintenance");
        JButton viewReportButton = new JButton("View Report");
        buttonPanel.add(submitButton);
        buttonPanel.add(maintenanceDoneButton);
        buttonPanel.add(flagMaintenanceButton);
        buttonPanel.add(viewReportButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(e -> collectAndDeployRoomData());
        maintenanceDoneButton.addActionListener(e -> markMaintenanceCompleted());
        flagMaintenanceButton.addActionListener(e -> flagRoomForMaintenance());
        viewReportButton.addActionListener(e -> generateRoomReport());

        loadRooms();
        frame.setVisible(true);
    }

    private void loadRooms() {
        tableModel.setRowCount(0);
        for (String[] data : roomData) {
            tableModel.addRow(new Object[]{data[0], data[1], data[2], data[3], getRoomPrice(data[1])});
        }
    }

    private void collectAndDeployRoomData() {
        String roomNumber = roomNumberField.getText();
        String roomType = (String) roomTypeCombo.getSelectedItem();
        String roomStatus = (String) roomStatusCombo.getSelectedItem();
        String housekeepingTask = (String) housekeepingTaskCombo.getSelectedItem();

        if (!roomNumber.isEmpty()) {
            roomData.add(new String[]{roomNumber, roomType, roomStatus, housekeepingTask});
            loadRooms();
        } else {
            JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void flagRoomForMaintenance() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow != -1) {
            roomData.get(selectedRow)[2] = "Maintenance";
            roomData.get(selectedRow)[3] = "Cleaning";
            loadRooms();
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a room to flag for maintenance.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void markMaintenanceCompleted() {
        int selectedRow = roomsTable.getSelectedRow();
        if (selectedRow != -1) {
            if ("Maintenance".equals(roomData.get(selectedRow)[2])) {
                roomData.get(selectedRow)[2] = "Available";
                roomData.get(selectedRow)[3] = "None";
                loadRooms();
                JOptionPane.showMessageDialog(frame, "Room marked as available.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Selected room is not currently under maintenance.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a room to mark as available.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generateRoomReport() {
        int singleOccupied = 0, doubleOccupied = 0, suiteOccupied = 0, maintenanceCount = 0, availableCount = 0;

        for (String[] room : roomData) {
            String type = room[1];
            String status = room[2];
            if ("Occupied".equalsIgnoreCase(status)) {
                switch (type) {
                    case "Single" -> singleOccupied++;
                    case "Double" -> doubleOccupied++;
                    case "Suite" -> suiteOccupied++;
                }
            } else if ("Maintenance".equalsIgnoreCase(status)) {
                maintenanceCount++;
            } else if ("Available".equalsIgnoreCase(status)) {
                availableCount++;
            }
        }

        JOptionPane.showMessageDialog(frame, "Room Report:\n" +
                "Single Rooms Occupied: " + singleOccupied + "\n" +
                "Double Rooms Occupied: " + doubleOccupied + "\n" +
                "Suite Rooms Occupied: " + suiteOccupied + "\n" +
                "Rooms in Maintenance: " + maintenanceCount + "\n" +
                "Rooms Available: " + availableCount,
                "Room Report", JOptionPane.INFORMATION_MESSAGE);
    }

    private double getRoomPrice(String roomType) {
        return switch (roomType) {
            case "Single" -> 100.0;
            case "Double" -> 150.0;
            case "Suite" -> 250.0;
            default -> 0.0;
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ManageRoomsUI2::new);
    }
}
