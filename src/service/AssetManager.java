package service;

import model.Asset;
import model.AssetCondition;
import model.AssetException;
import model.AssetStatus;
import model.Employee;
import model.Record;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

// Service class handling all business logic, validations, and storage
// Viva: Combines HashMap for fast O(1) lookups by ID and ArrayList for sequential records and lists.
public class AssetManager {

    // HashMap provides fast key-value lookup using the ID as key
    private HashMap<String, Asset> assetMap;
    private HashMap<String, Employee> employeeMap;

    // ArrayList maintains chronological order of history records
    private ArrayList<Record> recordList;

    // Constructor initializing the collections
    public AssetManager() {
        this.assetMap = new HashMap<String, Asset>();
        this.employeeMap = new HashMap<String, Employee>();
        this.recordList = new ArrayList<Record>();
    }

    // Adds a new employee after checking for non-empty fields and unique ID
    public void addEmployee(Employee e) throws AssetException {
        if (e == null) {
            throw new AssetException("Employee cannot be null.");
        }
        if (e.getId() == null || e.getId().trim().isEmpty()) {
            throw new AssetException("Employee ID cannot be empty.");
        }
        if (e.getName() == null || e.getName().trim().isEmpty()) {
            throw new AssetException("Employee name cannot be empty.");
        }
        if (e.getDepartment() == null || e.getDepartment().trim().isEmpty()) {
            throw new AssetException("Department cannot be empty.");
        }
        if (e.getEmail() == null || e.getEmail().trim().isEmpty()) {
            throw new AssetException("Email cannot be empty.");
        }
        // Check if ID is already registered
        if (employeeMap.containsKey(e.getId().trim())) {
            throw new AssetException("Employee with ID '" + e.getId() + "' already exists.");
        }

        e.setId(e.getId().trim());
        e.setName(e.getName().trim());
        e.setDepartment(e.getDepartment().trim());
        e.setEmail(e.getEmail().trim());

        employeeMap.put(e.getId(), e);
    }

    // Adds a new asset after checking for non-empty fields and unique ID
    public void addAsset(Asset a) throws AssetException {
        if (a == null) {
            throw new AssetException("Asset cannot be null.");
        }
        if (a.getId() == null || a.getId().trim().isEmpty()) {
            throw new AssetException("Asset ID cannot be empty.");
        }
        if (a.getName() == null || a.getName().trim().isEmpty()) {
            throw new AssetException("Asset name cannot be empty.");
        }
        if (a.getType() == null) {
            throw new AssetException("Asset type must be selected.");
        }
        if (a.getCondition() == null) {
            throw new AssetException("Asset condition must be selected.");
        }
        // Check if ID is already registered
        if (assetMap.containsKey(a.getId().trim())) {
            throw new AssetException("Asset with ID '" + a.getId() + "' already exists.");
        }

        a.setId(a.getId().trim());
        a.setName(a.getName().trim());

        assetMap.put(a.getId(), a);
    }

    // Updates physical condition of an existing asset
    public void updateCondition(String assetId, AssetCondition condition) throws AssetException {
        if (assetId == null || assetId.trim().isEmpty()) {
            throw new AssetException("Asset ID cannot be empty.");
        }
        if (condition == null) {
            throw new AssetException("Asset condition cannot be null.");
        }

        Asset asset = assetMap.get(assetId.trim());
        if (asset == null) {
            throw new AssetException("Asset with ID '" + assetId + "' not found.");
        }

        asset.setCondition(condition);
    }

    // Appends a new service/repair note to an asset's service history
    public void addServiceNote(String assetId, String note) throws AssetException {
        if (assetId == null || assetId.trim().isEmpty()) {
            throw new AssetException("Asset ID cannot be empty.");
        }
        if (note == null || note.trim().isEmpty()) {
            throw new AssetException("Service note cannot be empty.");
        }

        Asset asset = assetMap.get(assetId.trim());
        if (asset == null) {
            throw new AssetException("Asset with ID '" + assetId + "' not found.");
        }

        String currentDate = LocalDate.now().toString();
        asset.addServiceHistory("[" + currentDate + "] " + note.trim());
    }

    // Assigns an available and operational asset to an employee
    public void assignAsset(String assetId, String employeeId) throws AssetException {
        if (assetId == null || assetId.trim().isEmpty()) {
            throw new AssetException("Asset ID cannot be empty.");
        }
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new AssetException("Employee ID cannot be empty.");
        }

        Asset asset = assetMap.get(assetId.trim());
        if (asset == null) {
            throw new AssetException("Asset with ID '" + assetId + "' not found.");
        }

        Employee employee = employeeMap.get(employeeId.trim());
        if (employee == null) {
            throw new AssetException("Employee with ID '" + employeeId + "' not found.");
        }

        // Rule: Asset must be AVAILABLE
        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new AssetException("Asset '" + assetId + "' is already assigned to employee '" + asset.getAssignedEmployeeId() + "'.");
        }

        // Rule: Cannot assign DAMAGED or UNDER_REPAIR assets
        if (asset.getCondition() == AssetCondition.DAMAGED || asset.getCondition() == AssetCondition.UNDER_REPAIR) {
            throw new AssetException("Cannot assign asset in '" + asset.getCondition() + "' condition. Please repair first.");
        }

        // Update asset state
        asset.setStatus(AssetStatus.ASSIGNED);
        asset.setAssignedEmployeeId(employee.getId());

        // Add history record
        String currentDate = LocalDate.now().toString();
        Record record = new Record(asset.getId(), "None", employee.getId(), "ASSIGN", currentDate);
        recordList.add(record);
    }

    // Transfers an already assigned asset from its current holder to a new employee
    public void transferAsset(String assetId, String newEmployeeId) throws AssetException {
        if (assetId == null || assetId.trim().isEmpty()) {
            throw new AssetException("Asset ID cannot be empty.");
        }
        if (newEmployeeId == null || newEmployeeId.trim().isEmpty()) {
            throw new AssetException("New Employee ID cannot be empty.");
        }

        Asset asset = assetMap.get(assetId.trim());
        if (asset == null) {
            throw new AssetException("Asset with ID '" + assetId + "' not found.");
        }

        // Rule: Asset must be ASSIGNED
        if (asset.getStatus() != AssetStatus.ASSIGNED) {
            throw new AssetException("Cannot transfer asset '" + assetId + "' because it is not currently assigned.");
        }

        Employee newEmployee = employeeMap.get(newEmployeeId.trim());
        if (newEmployee == null) {
            throw new AssetException("Employee with ID '" + newEmployeeId + "' not found.");
        }

        String currentHolderId = asset.getAssignedEmployeeId();
        // Rule: New employee must be different from current holder
        if (currentHolderId != null && currentHolderId.equalsIgnoreCase(newEmployee.getId())) {
            throw new AssetException("Asset is already assigned to this employee (" + newEmployeeId + ").");
        }

        // Update asset state
        asset.setAssignedEmployeeId(newEmployee.getId());

        // Add history record
        String currentDate = LocalDate.now().toString();
        Record record = new Record(asset.getId(), currentHolderId, newEmployee.getId(), "TRANSFER", currentDate);
        recordList.add(record);
    }

    // Returns an assigned asset back to company inventory (makes it AVAILABLE)
    public void returnAsset(String assetId) throws AssetException {
        if (assetId == null || assetId.trim().isEmpty()) {
            throw new AssetException("Asset ID cannot be empty.");
        }

        Asset asset = assetMap.get(assetId.trim());
        if (asset == null) {
            throw new AssetException("Asset with ID '" + assetId + "' not found.");
        }

        // Rule: Asset must be ASSIGNED to be returned
        if (asset.getStatus() != AssetStatus.ASSIGNED) {
            throw new AssetException("Asset '" + assetId + "' is not assigned to anyone, cannot return.");
        }

        String previousHolderId = asset.getAssignedEmployeeId();

        // Update asset state
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setAssignedEmployeeId("");

        // Add history record
        String currentDate = LocalDate.now().toString();
        Record record = new Record(asset.getId(), previousHolderId, "None", "RETURN", currentDate);
        recordList.add(record);
    }

    // Searches for assets whose ID or Name contains the keyword (case-insensitive)
    public ArrayList<Asset> searchAsset(String keyword) {
        ArrayList<Asset> resultList = new ArrayList<Asset>();
        if (keyword == null) {
            return resultList;
        }

        String lowerKeyword = keyword.trim().toLowerCase();

        // Plain for-each loop over all values in assetMap
        for (Asset asset : assetMap.values()) {
            String lowerId = asset.getId().toLowerCase();
            String lowerName = asset.getName().toLowerCase();

            if (lowerId.contains(lowerKeyword) || lowerName.contains(lowerKeyword)) {
                resultList.add(asset);
            }
        }

        return resultList;
    }

    // Finds an asset by its exact ID
    public Asset getAssetById(String assetId) {
        if (assetId == null) {
            return null;
        }
        return assetMap.get(assetId.trim());
    }

    // Finds an employee by their exact ID
    public Employee getEmployeeById(String employeeId) {
        if (employeeId == null) {
            return null;
        }
        return employeeMap.get(employeeId.trim());
    }

    // Returns all assets as an ArrayList
    public ArrayList<Asset> getAllAssets() {
        ArrayList<Asset> list = new ArrayList<Asset>();
        for (Asset a : assetMap.values()) {
            list.add(a);
        }
        return list;
    }

    // Returns all employees as an ArrayList
    public ArrayList<Employee> getAllEmployees() {
        ArrayList<Employee> list = new ArrayList<Employee>();
        for (Employee e : employeeMap.values()) {
            list.add(e);
        }
        return list;
    }

    // Returns all transaction history records
    public ArrayList<Record> getRecords() {
        return recordList;
    }

    // Returns total count of registered assets
    public int countTotal() {
        return assetMap.size();
    }

    // Returns total count of assets matching a specific status
    public int countByStatus(AssetStatus status) {
        int count = 0;
        for (Asset a : assetMap.values()) {
            if (a.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    // Returns total count of assets matching a specific condition
    public int countByCondition(AssetCondition condition) {
        int count = 0;
        for (Asset a : assetMap.values()) {
            if (a.getCondition() == condition) {
                count++;
            }
        }
        return count;
    }

    // Finds all assets currently assigned to a specific employee ID
    public ArrayList<Asset> getAssetsOfEmployee(String employeeId) {
        ArrayList<Asset> list = new ArrayList<Asset>();
        if (employeeId == null || employeeId.trim().isEmpty()) {
            return list;
        }

        String targetId = employeeId.trim();
        for (Asset a : assetMap.values()) {
            if (targetId.equalsIgnoreCase(a.getAssignedEmployeeId())) {
                list.add(a);
            }
        }
        return list;
    }
}
