package model;

// Phone child class extending Asset
// Viva: Demonstrates inheritance and polymorphism by implementing getCategory() for phones.
public class Phone extends Asset {

    // Constructor calling parent constructor with super()
    public Phone(String id, String name, AssetCondition condition) {
        super(id, name, AssetType.PHONE, condition);
    }

    // Overriding abstract method from parent
    @Override
    public String getCategory() {
        return "Phone";
    }
}
