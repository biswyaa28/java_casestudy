package model;

// OtherAsset child class extending Asset for miscellaneous assets (monitors, projectors, etc.)
// Viva: Demonstrates flexibility of inheritance to support ANY asset type mentioned in problem statement.
public class OtherAsset extends Asset {

    // Constructor calling parent constructor with super()
    public OtherAsset(String id, String name, AssetCondition condition) {
        super(id, name, AssetType.OTHER, condition);
    }

    // Overriding abstract method from parent
    @Override
    public String getCategory() {
        return "Other";
    }
}
