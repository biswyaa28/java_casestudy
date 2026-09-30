package model;

// Class representing an allocation log entry (ASSIGN, TRANSFER, RETURN)
// Viva: Keeps historical track of who owned which asset and when.
public class Record {
    private String assetId;
    private String fromEmployeeId; // Empty or "None" for new assignment
    private String toEmployeeId;   // Empty or "None" for return
    private String action;         // "ASSIGN", "TRANSFER", or "RETURN"
    private String date;           // Stored simply as a String (e.g. "2026-09-30")

    // Constructor to initialize a transaction record
    public Record(String assetId, String fromEmployeeId, String toEmployeeId, String action, String date) {
        this.assetId = assetId;
        this.fromEmployeeId = fromEmployeeId;
        this.toEmployeeId = toEmployeeId;
        this.action = action;
        this.date = date;
    }

    // Getters and Setters
    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getFromEmployeeId() {
        return fromEmployeeId;
    }

    public void setFromEmployeeId(String fromEmployeeId) {
        this.fromEmployeeId = fromEmployeeId;
    }

    public String getToEmployeeId() {
        return toEmployeeId;
    }

    public void setToEmployeeId(String toEmployeeId) {
        this.toEmployeeId = toEmployeeId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    // Returns a readable line for display or printing
    @Override
    public String toString() {
        String fromText = fromEmployeeId;
        if (fromText == null || fromText.isEmpty()) {
            fromText = "N/A";
        }

        String toText = toEmployeeId;
        if (toText == null || toText.isEmpty()) {
            toText = "N/A";
        }

        return "[" + date + "] Action: " + action + " | Asset: " + assetId + 
               " | From: " + fromText + 
               " | To: " + toText;
    }
}
