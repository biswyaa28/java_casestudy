import gui.MainFrame;
import model.AssetCondition;
import model.AssetException;
import model.Employee;
import model.Furniture;
import model.Laptop;
import model.Phone;
import service.AssetManager;

import javax.swing.SwingUtilities;

// Application entry point
// Viva: Creates the central AssetManager instance, pre-populates sample data for demo, and launches the Swing GUI.
public class Main {

    public static void main(String[] args) {
        // Create the single shared instance of the business logic manager
        final AssetManager manager = new AssetManager();

        // Pre-populate sample data so the application is ready to demo right away
        try {
            manager.addEmployee(new Employee("EMP01", "Alice Smith", "IT", "alice@company.com"));
            manager.addEmployee(new Employee("EMP02", "Bob Johnson", "HR", "bob@company.com"));
            manager.addEmployee(new Employee("EMP03", "Charlie Brown", "Finance", "charlie@company.com"));

            manager.addAsset(new Laptop("AST01", "MacBook Pro 16", AssetCondition.NEW));
            manager.addAsset(new Phone("AST02", "iPhone 15", AssetCondition.GOOD));
            manager.addAsset(new Furniture("AST03", "Ergonomic Chair", AssetCondition.GOOD));
            manager.addAsset(new Laptop("AST04", "Dell Latitude", AssetCondition.DAMAGED));

            // Pre-assign one asset for quick demonstration
            manager.assignAsset("AST01", "EMP01");
        } catch (AssetException e) {
            System.out.println("Initialization notice: " + e.getMessage());
        }

        // Launch GUI on Event Dispatch Thread (standard Swing practice)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                MainFrame frame = new MainFrame(manager);
                frame.setVisible(true);
            }
        });
    }
}
