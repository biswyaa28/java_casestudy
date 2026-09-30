package model;

// Laptop child class extending Asset
// Viva: Demonstrates inheritance. Laptop inherits all fields and methods from Asset.
public class Laptop extends Asset {

    // Constructor calling parent constructor with super()
    public Laptop(String id, String name, AssetCondition condition) {
        super(id, name, AssetType.LAPTOP, condition);
    }

    // Overriding abstract method from parent
    @Override
    public String getCategory() {
        return "Laptop";
    }
}
