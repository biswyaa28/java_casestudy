package model;

// Custom checked exception for any asset-related business rule failure
// Viva: Inherits from Exception class so calling methods must handle it using try-catch or declare it with throws.
public class AssetException extends Exception {
    
    // Constructor taking error message
    public AssetException(String message) {
        super(message);
    }
}
