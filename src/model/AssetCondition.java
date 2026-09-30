package model;

// Enum for physical condition of an asset
// Viva: Using enum ensures only valid conditions (NEW, GOOD, DAMAGED, UNDER_REPAIR) can be assigned.
public enum AssetCondition {
    NEW,
    GOOD,
    DAMAGED,
    UNDER_REPAIR
}
