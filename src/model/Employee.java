package model;

// Class representing an employee
// Viva: Demonstrates encapsulation. Fields are private and accessed via getters and setters.
public class Employee {
    private String id;
    private String name;
    private String department;
    private String email;

    // Constructor to initialize an employee
    public Employee(String id, String name, String department, String email) {
        this.id = id;
        this.name = name;
        this.department = department;
        this.email = email;
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

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Returns readable details of the employee
    @Override
    public String toString() {
        return "Employee [ID=" + id + ", Name=" + name + ", Dept=" + department + ", Email=" + email + "]";
    }
}
