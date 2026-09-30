import model.Asset;
import model.AssetCondition;
import model.AssetException;
import model.AssetStatus;
import model.Employee;
import model.Furniture;
import model.Laptop;
import model.Phone;
import model.Record;
import service.AssetManager;

import java.util.ArrayList;

// Temporary test console to verify business rules, workflows, and exceptions
// Viva: Demonstrates unit testing and verification of custom exceptions and collections.
public class TestConsole {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("   OFFICE ASSET ALLOCATION SYSTEM - TEST SUITE   ");
        System.out.println("==================================================\n");

        AssetManager manager = new AssetManager();

        // ---------------------------------------------------------------------
        // 1. ADD EMPLOYEES AND ASSETS
        // ---------------------------------------------------------------------
        System.out.println("--- 1. Adding Employees & Assets ---");
        try {
            manager.addEmployee(new Employee("EMP01", "Alice Smith", "IT", "alice@company.com"));
            manager.addEmployee(new Employee("EMP02", "Bob Johnson", "HR", "bob@company.com"));
            manager.addEmployee(new Employee("EMP03", "Charlie Brown", "Finance", "charlie@company.com"));
            System.out.println("[SUCCESS] Added 3 employees.");

            manager.addAsset(new Laptop("AST01", "MacBook Pro 16", AssetCondition.NEW));
            manager.addAsset(new Phone("AST02", "iPhone 15", AssetCondition.GOOD));
            manager.addAsset(new Furniture("AST03", "Ergonomic Chair", AssetCondition.GOOD));
            manager.addAsset(new Laptop("AST04", "Dell XPS 13", AssetCondition.DAMAGED));
            System.out.println("[SUCCESS] Added 4 assets (including 1 DAMAGED).");
        } catch (AssetException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        // ---------------------------------------------------------------------
        // 2. ASSIGN LAPTOP TO EMPLOYEE 1
        // ---------------------------------------------------------------------
        System.out.println("\n--- 2. Assigning Asset AST01 to EMP01 ---");
        try {
            manager.assignAsset("AST01", "EMP01");
            System.out.println("[SUCCESS] AST01 assigned to EMP01.");
        } catch (AssetException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        // ---------------------------------------------------------------------
        // 3. TRANSFER LAPTOP TO EMPLOYEE 2
        // ---------------------------------------------------------------------
        System.out.println("\n--- 3. Transferring Asset AST01 to EMP02 ---");
        try {
            manager.transferAsset("AST01", "EMP02");
            System.out.println("[SUCCESS] AST01 transferred to EMP02.");
        } catch (AssetException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        // ---------------------------------------------------------------------
        // 4. RETURN LAPTOP
        // ---------------------------------------------------------------------
        System.out.println("\n--- 4. Returning Asset AST01 ---");
        try {
            manager.returnAsset("AST01");
            System.out.println("[SUCCESS] AST01 returned successfully. Status is now AVAILABLE.");
        } catch (AssetException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        // ---------------------------------------------------------------------
        // 5. TEST ALL ERROR CASES INDIVIDUALLY
        // ---------------------------------------------------------------------
        System.out.println("\n--- 5. Testing Exception & Validation Rules ---");

        // Case 1: Duplicate employee ID
        System.out.print("[Test 5.1 Duplicate Employee ID]: ");
        try {
            manager.addEmployee(new Employee("EMP01", "Duplicate Alice", "IT", "dup@company.com"));
            System.out.println("FAILED (Should have thrown exception)");
        } catch (AssetException e) {
            System.out.println("Caught expected exception -> " + e.getMessage());
        }

        // Case 2: Duplicate asset ID
        System.out.print("[Test 5.2 Duplicate Asset ID]: ");
        try {
            manager.addAsset(new Laptop("AST01", "Another MacBook", AssetCondition.NEW));
            System.out.println("FAILED (Should have thrown exception)");
        } catch (AssetException e) {
            System.out.println("Caught expected exception -> " + e.getMessage());
        }

        // Case 3: Empty name validation
        System.out.print("[Test 5.3 Empty Employee Name]: ");
        try {
            manager.addEmployee(new Employee("EMP04", "", "Sales", "emp4@company.com"));
            System.out.println("FAILED (Should have thrown exception)");
        } catch (AssetException e) {
            System.out.println("Caught expected exception -> " + e.getMessage());
        }

        // Case 4: Assigning an already assigned asset
        System.out.print("[Test 5.4 Assigning already assigned asset]: ");
        try {
            // First assign AST02 to EMP01
            manager.assignAsset("AST02", "EMP01");
            // Now attempt assigning AST02 again to EMP02
            manager.assignAsset("AST02", "EMP02");
            System.out.println("FAILED (Should have thrown exception)");
        } catch (AssetException e) {
            System.out.println("Caught expected exception -> " + e.getMessage());
        }

        // Case 5: Assigning a DAMAGED asset
        System.out.print("[Test 5.5 Assigning DAMAGED asset (AST04)]: ");
        try {
            manager.assignAsset("AST04", "EMP01");
            System.out.println("FAILED (Should have thrown exception)");
        } catch (AssetException e) {
            System.out.println("Caught expected exception -> " + e.getMessage());
        }

        // Case 6: Assigning to an employee that doesn't exist
        System.out.print("[Test 5.6 Assigning to non-existent employee (EMP99)]: ");
        try {
            manager.assignAsset("AST03", "EMP99");
            System.out.println("FAILED (Should have thrown exception)");
        } catch (AssetException e) {
            System.out.println("Caught expected exception -> " + e.getMessage());
        }

        // Case 7: Returning an asset that is NOT assigned
        System.out.print("[Test 7 Returning unassigned asset (AST03)]: ");
        try {
            manager.returnAsset("AST03"); // AST03 is still AVAILABLE
            System.out.println("FAILED (Should have thrown exception)");
        } catch (AssetException e) {
            System.out.println("Caught expected exception -> " + e.getMessage());
        }

        // Case 8: Transferring to the same employee
        System.out.print("[Test 5.8 Transferring to same employee]: ");
        try {
            // AST02 is currently assigned to EMP01
            manager.transferAsset("AST02", "EMP01");
            System.out.println("FAILED (Should have thrown exception)");
        } catch (AssetException e) {
            System.out.println("Caught expected exception -> " + e.getMessage());
        }

        // ---------------------------------------------------------------------
        // 6. ADD SERVICE NOTE AND PRINT SERVICE HISTORY
        // ---------------------------------------------------------------------
        System.out.println("\n--- 6. Service History Test ---");
        try {
            manager.addServiceNote("AST04", "Screen cracked, sent to vendor for panel replacement.");
            manager.addServiceNote("AST04", "Replacement screen received, testing display output.");
            System.out.println("[SUCCESS] Added 2 service notes to AST04.");

            ArrayList<Asset> searchList = manager.searchAsset("AST04");
            if (!searchList.isEmpty()) {
                Asset ast4 = searchList.get(0);
                System.out.println("Service History for " + ast4.getId() + " (" + ast4.getName() + "):");
                for (String note : ast4.getServiceHistory()) {
                    System.out.println("  * " + note);
                }
            }
        } catch (AssetException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        // ---------------------------------------------------------------------
        // 7. PRINT REPORT COUNTS AND RECORD LIST
        // ---------------------------------------------------------------------
        System.out.println("\n--- 7. System Reports & Audit Trail ---");
        System.out.println("Total Assets       : " + manager.countTotal());
        System.out.println("Available Assets   : " + manager.countByStatus(AssetStatus.AVAILABLE));
        System.out.println("Assigned Assets    : " + manager.countByStatus(AssetStatus.ASSIGNED));
        System.out.println("New Condition      : " + manager.countByCondition(AssetCondition.NEW));
        System.out.println("Good Condition     : " + manager.countByCondition(AssetCondition.GOOD));
        System.out.println("Damaged Condition  : " + manager.countByCondition(AssetCondition.DAMAGED));
        System.out.println("Under Repair       : " + manager.countByCondition(AssetCondition.UNDER_REPAIR));

        System.out.println("\nAll History Records:");
        for (Record rec : manager.getRecords()) {
            System.out.println("  " + rec);
        }

        System.out.println("\nAssets currently held by EMP01:");
        ArrayList<Asset> emp1Assets = manager.getAssetsOfEmployee("EMP01");
        if (emp1Assets.isEmpty()) {
            System.out.println("  (None)");
        } else {
            for (Asset a : emp1Assets) {
                System.out.println("  " + a);
            }
        }

        System.out.println("\n==================================================");
        System.out.println("               ALL TESTS COMPLETED                ");
        System.out.println("==================================================");
    }
}
