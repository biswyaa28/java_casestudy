package model;

import java.util.ArrayList;

// Abstract parent class representing a generic office asset
// Viva: Abstraction prevents creating a direct "new Asset()". It defines common properties for child classes.
public abstract class Asset {
    private String id;
    private String name;
    private AssetType type;
    private AssetCondition condition;
    private AssetStatus status;
    private String assignedEmployeeId; // null or empty if not assigned
    private ArrayList<String> serviceHistory; // Viva: ArrayList stores dynamic service logs

    // Constructor to initialize an asset
    public Asset(String id, String name, AssetType type, AssetCondition condition) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.condition = condition;
        this.status = AssetStatus.AVAILABLE; // All new assets start as AVAILABLE
        this.assignedEmployeeId = "";        // Not assigned to anyone yet
        this.serviceHistory = new ArrayList<String>();
    }

    // Abstract method: Every child class must implement this to return its category name
    // Viva: Demonstrates polymorphism - parent declares the method, children provide specific behavior.
    public abstract String getCategory();

    // Adds a note to the service history list
    public void addServiceHistory(String note) {
        this.serviceHistory.add(note);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssetType getType() {
        return type;
    }

    public void setType(AssetType type) {
        this.type = type;
    }

    public AssetCondition getCondition() {
        return condition;
    }

    public void setCondition(AssetCondition condition) {
        this.condition = condition;
    }

    public AssetStatus getStatus() {
        return status;
    }

    public void setStatus(AssetStatus status) {
        this.status = status;
    }

    public String getAssignedEmployeeId() {
        return assignedEmployeeId;
    }

    public void setAssignedEmployeeId(String assignedEmployeeId) {
        this.assignedEmployeeId = assignedEmployeeId;
    }

    public ArrayList<String> getServiceHistory() {
        return serviceHistory;
    }

    // Returns a simple readable string representation of the asset
    @Override
    public String toString() {
        String assignedText = assignedEmployeeId;
        if (assignedText == null || assignedText.isEmpty()) {
            assignedText = "None";
        }
        return "Asset [ID=" + id + ", Name=" + name + ", Category=" + getCategory() + 
               ", Condition=" + condition + ", Status=" + status + 
               ", AssignedTo=" + assignedText + "]";
    }
}
