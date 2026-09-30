package gui;

import model.Asset;
import model.AssetCondition;
import model.AssetException;
import model.AssetStatus;
import model.AssetType;
import model.Employee;
import model.Furniture;
import model.Laptop;
import model.OtherAsset;
import model.Phone;
import model.Record;
import service.AssetManager;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

// Main window of the application extending JFrame
// Viva: Demonstrates GUI programming using Swing components, event handling with ActionListeners, and JTable.
public class MainFrame extends JFrame {

    // Reference to the shared business service
    private AssetManager manager;

    // GUI Components for Tab 1 - Employees
    private JTextField empIdField;
    private JTextField empNameField;
    private JTextField empDeptField;
    private JTextField empEmailField;
    private DefaultTableModel employeeTableModel;
    private JTable employeeTable;

    // GUI Components for Tab 2 - Assets
    private JTextField assetIdField;
    private JTextField assetNameField;
    private JComboBox<AssetType> assetTypeCombo;
    private JTextField searchField;
    private JComboBox<AssetCondition> conditionCombo;
    private JTextField serviceNoteField;
    private DefaultTableModel assetTableModel;
    private JTable assetTable;

    // GUI Components for Tab 3 - Assign / Transfer / Return
    private JComboBox<String> allocationAssetCombo;
    private JComboBox<String> allocationEmpCombo;
    private DefaultTableModel recordTableModel;
    private JTable recordTable;

    // GUI Components for Tab 4 - Reports
    private JTextArea reportTextArea;

    // Constructor to set up the window and initialize tabs
    public MainFrame(AssetManager manager) {
        super("Office Asset Allocation System");
        this.manager = manager;

        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen

        // Create tabbed container
        final JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Employees", createEmployeePanel());
        tabbedPane.addTab("Assets", createAssetPanel());
        tabbedPane.addTab("Assign / Transfer / Return", createAllocationPanel());
        tabbedPane.addTab("Reports", createReportPanel());

        // Automatically refresh report or tables when user switches tabs
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabbedPane.getSelectedIndex();
                if (selectedIndex == 0) {
                    refreshEmployeeTable();
                } else if (selectedIndex == 1) {
                    refreshAssetTable(manager.getAllAssets());
                } else if (selectedIndex == 2) {
                    refreshAllocationCombos();
                    refreshRecordTable();
                } else if (selectedIndex == 3) {
                    generateReport();
                }
            }
        });

        add(tabbedPane);

        // Load initial data into tables and combos
        refreshEmployeeTable();
        refreshAssetTable(manager.getAllAssets());
        refreshAllocationCombos();
        refreshRecordTable();
        generateReport();
    }

    // -------------------------------------------------------------------------
    // TAB 1: EMPLOYEES PANEL
    // -------------------------------------------------------------------------

    // Creates Tab 1 (Employees panel) with input form and JTable
    private JPanel createEmployeePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top form panel for entering employee details
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.add(new JLabel("Employee ID:"));
        empIdField = new JTextField();
        formPanel.add(empIdField);

        formPanel.add(new JLabel("Full Name:"));
        empNameField = new JTextField();
        formPanel.add(empNameField);

        formPanel.add(new JLabel("Department:"));
        empDeptField = new JTextField();
        formPanel.add(empDeptField);

        formPanel.add(new JLabel("Email:"));
        empEmailField = new JTextField();
        formPanel.add(empEmailField);

        JButton addEmpButton = new JButton("Add Employee");
        // Anonymous inner class for button click
        addEmpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddEmployee();
            }
        });

        JButton clearButton = new JButton("Clear Form");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearEmployeeForm();
            }
        });

        formPanel.add(addEmpButton);
        formPanel.add(clearButton);

        panel.add(formPanel, BorderLayout.NORTH);

        // Center table showing all registered employees
        String[] columns = {"Employee ID", "Name", "Department", "Email"};
        employeeTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        employeeTable = new JTable(employeeTableModel);
        panel.add(new JScrollPane(employeeTable), BorderLayout.CENTER);

        return panel;
    }

    // Handles adding a new employee when "Add Employee" button is clicked
    private void handleAddEmployee() {
        String id = empIdField.getText();
        String name = empNameField.getText();
        String dept = empDeptField.getText();
        String email = empEmailField.getText();

        try {
            Employee emp = new Employee(id, name, dept, email);
            manager.addEmployee(emp);
            JOptionPane.showMessageDialog(this, "Employee added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearEmployeeForm();
            refreshEmployeeTable();
            refreshAllocationCombos(); // Update employee combo in Tab 3
        } catch (AssetException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Clears employee input text fields
    private void clearEmployeeForm() {
        empIdField.setText("");
        empNameField.setText("");
        empDeptField.setText("");
        empEmailField.setText("");
    }

    // Reloads all employee rows into the employee table
    public void refreshEmployeeTable() {
        employeeTableModel.setRowCount(0);
        ArrayList<Employee> list = manager.getAllEmployees();
        for (int i = 0; i < list.size(); i++) {
            Employee e = list.get(i);
            Object[] row = {e.getId(), e.getName(), e.getDepartment(), e.getEmail()};
            employeeTableModel.addRow(row);
        }
    }

    // -------------------------------------------------------------------------
    // TAB 2: ASSETS PANEL
    // -------------------------------------------------------------------------

    // Creates Tab 2 (Assets panel) with registration, search, condition update, service note, and table
    private JPanel createAssetPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top container holding Add form and Search bar
        JPanel topContainer = new JPanel(new GridLayout(2, 1));

        // Form for adding a new asset
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addPanel.add(new JLabel("Asset ID:"));
        assetIdField = new JTextField(6);
        addPanel.add(assetIdField);

        addPanel.add(new JLabel("Name:"));
        assetNameField = new JTextField(10);
        addPanel.add(assetNameField);

        addPanel.add(new JLabel("Type:"));
        assetTypeCombo = new JComboBox<AssetType>(AssetType.values());
        addPanel.add(assetTypeCombo);

        JButton addAssetBtn = new JButton("Add Asset");
        addAssetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddAsset();
            }
        });
        addPanel.add(addAssetBtn);
        topContainer.add(addPanel);

        // Search bar panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search (ID or Name):"));
        searchField = new JTextField(12);
        searchPanel.add(searchField);

        JButton searchBtn = new JButton("Search");
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleSearchAsset();
            }
        });
        searchPanel.add(searchBtn);

        JButton showAllBtn = new JButton("Show All");
        showAllBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                refreshAssetTable(manager.getAllAssets());
            }
        });
        searchPanel.add(showAllBtn);
        topContainer.add(searchPanel);

        panel.add(topContainer, BorderLayout.NORTH);

        // Center table showing assets
        String[] columns = {"ID", "Name", "Category", "Condition", "Status", "Assigned To"};
        assetTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        assetTable = new JTable(assetTableModel);
        panel.add(new JScrollPane(assetTable), BorderLayout.CENTER);

        // Bottom panel for operations on the selected asset (update condition & add service note)
        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));

        // Row 1: Condition update
        JPanel conditionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        conditionPanel.add(new JLabel("New Condition for Selected:"));
        conditionCombo = new JComboBox<AssetCondition>(AssetCondition.values());
        conditionPanel.add(conditionCombo);

        JButton updateCondBtn = new JButton("Update Condition");
        updateCondBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleUpdateCondition();
            }
        });
        conditionPanel.add(updateCondBtn);
        bottomPanel.add(conditionPanel);

        // Row 2: Service note
        JPanel servicePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        servicePanel.add(new JLabel("Service Note for Selected:"));
        serviceNoteField = new JTextField(20);
        servicePanel.add(serviceNoteField);

        JButton addNoteBtn = new JButton("Add Service Note");
        addNoteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAddServiceNote();
            }
        });
        servicePanel.add(addNoteBtn);

        JButton viewHistoryBtn = new JButton("View Service History");
        viewHistoryBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleViewServiceHistory();
            }
        });
        servicePanel.add(viewHistoryBtn);

        bottomPanel.add(servicePanel);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Handles creating the appropriate Asset subclass and adding it via manager
    private void handleAddAsset() {
        String id = assetIdField.getText();
        String name = assetNameField.getText();
        AssetType type = (AssetType) assetTypeCombo.getSelectedItem();
        AssetCondition defaultCondition = AssetCondition.NEW;

        Asset newAsset;
        if (type == AssetType.LAPTOP) {
            newAsset = new Laptop(id, name, defaultCondition);
        } else if (type == AssetType.PHONE) {
            newAsset = new Phone(id, name, defaultCondition);
        } else if (type == AssetType.FURNITURE) {
            newAsset = new Furniture(id, name, defaultCondition);
        } else {
            newAsset = new OtherAsset(id, name, defaultCondition);
        }

        try {
            manager.addAsset(newAsset);
            JOptionPane.showMessageDialog(this, "Asset added successfully as " + newAsset.getCategory() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            assetIdField.setText("");
            assetNameField.setText("");
            refreshAssetTable(manager.getAllAssets());
            refreshAllocationCombos(); // Update asset combo in Tab 3
        } catch (AssetException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Handles asset search filter
    private void handleSearchAsset() {
        String keyword = searchField.getText();
        if (keyword.trim().isEmpty()) {
            refreshAssetTable(manager.getAllAssets());
        } else {
            ArrayList<Asset> filtered = manager.searchAsset(keyword);
            refreshAssetTable(filtered);
        }
    }

    // Updates condition of selected table row
    private void handleUpdateCondition() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an asset from the table first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String assetId = (String) assetTableModel.getValueAt(selectedRow, 0);
        AssetCondition selectedCondition = (AssetCondition) conditionCombo.getSelectedItem();

        try {
            manager.updateCondition(assetId, selectedCondition);
            JOptionPane.showMessageDialog(this, "Condition updated to " + selectedCondition + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAssetTable(manager.getAllAssets());
        } catch (AssetException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Adds a service note to the selected asset
    private void handleAddServiceNote() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an asset from the table first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String assetId = (String) assetTableModel.getValueAt(selectedRow, 0);
        String note = serviceNoteField.getText();

        try {
            manager.addServiceNote(assetId, note);
            JOptionPane.showMessageDialog(this, "Service note added to " + assetId + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            serviceNoteField.setText("");
        } catch (AssetException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Displays service history of the selected asset in a dialog
    private void handleViewServiceHistory() {
        int selectedRow = assetTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an asset from the table first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String assetId = (String) assetTableModel.getValueAt(selectedRow, 0);
        Asset a = manager.getAssetById(assetId);
        if (a != null) {
            ArrayList<String> history = a.getServiceHistory();
            if (history.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No service history recorded for asset " + assetId, "Service History", JOptionPane.INFORMATION_MESSAGE);
            } else {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < history.size(); i++) {
                    sb.append(history.get(i)).append("\n");
                }
                JOptionPane.showMessageDialog(this, sb.toString(), "Service History for " + assetId, JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    // Reloads asset rows into the asset table
    public void refreshAssetTable(ArrayList<Asset> list) {
        assetTableModel.setRowCount(0);
        for (int i = 0; i < list.size(); i++) {
            Asset a = list.get(i);
            String assigned = a.getAssignedEmployeeId();
            if (assigned == null || assigned.isEmpty()) {
                assigned = "None";
            }
            Object[] row = {
                    a.getId(),
                    a.getName(),
                    a.getCategory(),
                    a.getCondition(),
                    a.getStatus(),
                    assigned
            };
            assetTableModel.addRow(row);
        }
    }

    // -------------------------------------------------------------------------
    // TAB 3: ASSIGN / TRANSFER / RETURN PANEL
    // -------------------------------------------------------------------------

    // Creates Tab 3 with Asset & Employee combo boxes, action buttons, and Record table
    private JPanel createAllocationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top control panel with combo boxes and action buttons
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));

        // Row 1: Selectors
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(new JLabel("Select Asset:"));
        allocationAssetCombo = new JComboBox<String>();
        selectionPanel.add(allocationAssetCombo);

        selectionPanel.add(new JLabel("Select Employee:"));
        allocationEmpCombo = new JComboBox<String>();
        selectionPanel.add(allocationEmpCombo);
        topPanel.add(selectionPanel);

        // Row 2: Action buttons (Assign, Transfer, Return)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton assignBtn = new JButton("Assign");
        assignBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleAssign();
            }
        });
        buttonPanel.add(assignBtn);

        JButton transferBtn = new JButton("Transfer");
        transferBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTransfer();
            }
        });
        buttonPanel.add(transferBtn);

        JButton returnBtn = new JButton("Return");
        returnBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleReturn();
            }
        });
        buttonPanel.add(returnBtn);

        topPanel.add(buttonPanel);
        panel.add(topPanel, BorderLayout.NORTH);

        // Center table showing the allocation audit trail
        String[] columns = {"Asset ID", "From Employee", "To Employee", "Action", "Date"};
        recordTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        recordTable = new JTable(recordTableModel);
        panel.add(new JScrollPane(recordTable), BorderLayout.CENTER);

        return panel;
    }

    // Helper to extract ID from "id - name" combo string
    private String extractIdFromCombo(String selectedItem) {
        if (selectedItem == null || selectedItem.isEmpty()) {
            return "";
        }
        int dashIndex = selectedItem.indexOf(" - ");
        if (dashIndex != -1) {
            return selectedItem.substring(0, dashIndex).trim();
        }
        return selectedItem.trim();
    }

    // Handles Assign button click
    private void handleAssign() {
        if (allocationAssetCombo.getItemCount() == 0 || allocationEmpCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please ensure both assets and employees exist.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String assetItem = (String) allocationAssetCombo.getSelectedItem();
        String empItem = (String) allocationEmpCombo.getSelectedItem();

        String assetId = extractIdFromCombo(assetItem);
        String empId = extractIdFromCombo(empItem);

        try {
            manager.assignAsset(assetId, empId);
            JOptionPane.showMessageDialog(this, "Asset " + assetId + " successfully assigned to " + empId + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAllocationCombos();
            refreshAssetTable(manager.getAllAssets());
            refreshRecordTable();
        } catch (AssetException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Assignment Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Handles Transfer button click
    private void handleTransfer() {
        if (allocationAssetCombo.getItemCount() == 0 || allocationEmpCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Please ensure both assets and employees exist.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String assetItem = (String) allocationAssetCombo.getSelectedItem();
        String empItem = (String) allocationEmpCombo.getSelectedItem();

        String assetId = extractIdFromCombo(assetItem);
        String empId = extractIdFromCombo(empItem);

        try {
            manager.transferAsset(assetId, empId);
            JOptionPane.showMessageDialog(this, "Asset " + assetId + " successfully transferred to " + empId + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAllocationCombos();
            refreshAssetTable(manager.getAllAssets());
            refreshRecordTable();
        } catch (AssetException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Transfer Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Handles Return button click
    private void handleReturn() {
        if (allocationAssetCombo.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No assets available to return.", "Selection Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String assetItem = (String) allocationAssetCombo.getSelectedItem();
        String assetId = extractIdFromCombo(assetItem);

        try {
            manager.returnAsset(assetId);
            JOptionPane.showMessageDialog(this, "Asset " + assetId + " successfully returned to inventory!", "Success", JOptionPane.INFORMATION_MESSAGE);
            refreshAllocationCombos();
            refreshAssetTable(manager.getAllAssets());
            refreshRecordTable();
        } catch (AssetException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Return Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Reloads allocation combo boxes with formatted "id - name" items
    public void refreshAllocationCombos() {
        allocationAssetCombo.removeAllItems();
        ArrayList<Asset> assets = manager.getAllAssets();
        for (int i = 0; i < assets.size(); i++) {
            Asset a = assets.get(i);
            allocationAssetCombo.addItem(a.getId() + " - " + a.getName());
        }

        allocationEmpCombo.removeAllItems();
        ArrayList<Employee> employees = manager.getAllEmployees();
        for (int i = 0; i < employees.size(); i++) {
            Employee e = employees.get(i);
            allocationEmpCombo.addItem(e.getId() + " - " + e.getName());
        }
    }

    // Reloads history records into the allocation record table
    public void refreshRecordTable() {
        recordTableModel.setRowCount(0);
        ArrayList<Record> records = manager.getRecords();
        for (int i = 0; i < records.size(); i++) {
            Record r = records.get(i);
            Object[] row = {
                    r.getAssetId(),
                    r.getFromEmployeeId(),
                    r.getToEmployeeId(),
                    r.getAction(),
                    r.getDate()
            };
            recordTableModel.addRow(row);
        }
    }

    // -------------------------------------------------------------------------
    // TAB 4: REPORTS PANEL
    // -------------------------------------------------------------------------

    // Creates Tab 4 with a Generate Report button and a formatted read-only JTextArea
    private JPanel createReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Top control bar with Generate Report button
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton generateBtn = new JButton("Generate Report");
        generateBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });
        topBar.add(generateBtn);
        panel.add(topBar, BorderLayout.NORTH);

        // Center text area inside JScrollPane
        reportTextArea = new JTextArea();
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13)); // Clean monospaced font for aligned report
        panel.add(new JScrollPane(reportTextArea), BorderLayout.CENTER);

        return panel;
    }

    // Compiles summary statistics and employee asset listings into reportTextArea
    // Viva: Demonstrates string formatting, calling service report methods, and nested iteration.
    public void generateReport() {
        if (reportTextArea == null) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================\n");
        sb.append("                 OFFICE ASSET ALLOCATION SUMMARY REPORT                 \n");
        sb.append("========================================================================\n\n");

        // 1. Total Assets
        sb.append("1. OVERALL ASSET COUNTS:\n");
        sb.append("   - Total Assets Registered : ").append(manager.countTotal()).append("\n\n");

        // 2. Count by Status
        sb.append("2. ASSET COUNT BY STATUS:\n");
        sb.append("   - Available : ").append(manager.countByStatus(AssetStatus.AVAILABLE)).append("\n");
        sb.append("   - Assigned  : ").append(manager.countByStatus(AssetStatus.ASSIGNED)).append("\n\n");

        // 3. Count by Condition
        sb.append("3. ASSET COUNT BY CONDITION:\n");
        sb.append("   - New          : ").append(manager.countByCondition(AssetCondition.NEW)).append("\n");
        sb.append("   - Good         : ").append(manager.countByCondition(AssetCondition.GOOD)).append("\n");
        sb.append("   - Damaged      : ").append(manager.countByCondition(AssetCondition.DAMAGED)).append("\n");
        sb.append("   - Under Repair : ").append(manager.countByCondition(AssetCondition.UNDER_REPAIR)).append("\n\n");

        // 4. Detailed Employee-wise Asset Allocation
        sb.append("4. EMPLOYEE ASSET ALLOCATION BREAKDOWN:\n");
        sb.append("------------------------------------------------------------------------\n");

        ArrayList<Employee> employees = manager.getAllEmployees();
        if (employees.isEmpty()) {
            sb.append("   No employees registered in the system.\n");
        } else {
            for (int i = 0; i < employees.size(); i++) {
                Employee emp = employees.get(i);
                sb.append("Employee: ").append(emp.getName()).append(" (ID: ").append(emp.getId())
                        .append(", Dept: ").append(emp.getDepartment()).append(")\n");

                ArrayList<Asset> assignedAssets = manager.getAssetsOfEmployee(emp.getId());
                if (assignedAssets.isEmpty()) {
                    sb.append("   -> No assets currently assigned.\n");
                } else {
                    for (int j = 0; j < assignedAssets.size(); j++) {
                        Asset a = assignedAssets.get(j);
                        sb.append("   -> [").append(a.getId()).append("] ")
                                .append(a.getName()).append(" (")
                                .append(a.getCategory()).append(", Condition: ")
                                .append(a.getCondition()).append(")\n");
                    }
                }
                sb.append("\n");
            }
        }

        sb.append("========================================================================\n");
        sb.append("                            END OF REPORT                               \n");
        sb.append("========================================================================\n");

        reportTextArea.setText(sb.toString());
    }
}
