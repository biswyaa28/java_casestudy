package model;

// Furniture child class extending Asset
// Viva: Demonstrates inheritance and polymorphism by implementing getCategory() for furniture.
public class Furniture extends Asset {

    // Constructor calling parent constructor with super()
    public Furniture(String id, String name, AssetCondition condition) {
        super(id, name, AssetType.FURNITURE, condition);
    }

    // Overriding abstract method from parent
    @Override
    public String getCategory() {
        return "Furniture";
    }
}
