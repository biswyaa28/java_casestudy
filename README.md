# Office Asset Allocation System

> **Case Study No. 135 — Java Programming (Semester III)**  
> Built using Plain Java (JDK 17) and Java Swing with zero external libraries or build tools.

---

## 📌 Project Overview
The **Office Asset Allocation System** is a standalone desktop application designed to track and manage organizational hardware and office assets (laptops, phones, furniture, and miscellaneous equipment). The system maintains an end-to-end audit trail for employee onboarding, asset conditions, assignments, transfers, returns, and maintenance history.

---

## ⬇️ Download & Run (No Java Required)

You do **not** need Java installed to run the native desktop packages. They come pre-bundled with an embedded Java 17 runtime via `jpackage`.

Go to the [Releases](https://github.com/biswyaa28/java_casestudy/releases) page and download the installer for your operating system:

| Operating System | Download File | How to Run |
| :--- | :--- | :--- |
| **macOS** | `AssetSystem-macOS.dmg` | Open the `.dmg`, drag the app to your `Applications` folder, and double-click to run. |
| **Windows** | `AssetSystem-Windows.exe` | Double-click the installer `.exe` and follow the setup prompt to install and run. |
| **Linux (Debian/Ubuntu)** | `AssetSystem-Linux.deb` | Install via terminal: `sudo dpkg -i AssetSystem-Linux.deb` |
| **Any OS (Universal)** | `AssetSystem.jar` | Double-click or run: `java -jar AssetSystem.jar` *(Requires Java 17+ installed)* |

> [!NOTE]  
> **Security Warnings on First Run (Unsigned App Notice):**
> - **macOS:** Because this open-source student app is not signed with an Apple Developer certificate, macOS Gatekeeper may show a warning saying *"the developer cannot be verified"*.  
>   👉 **Fix:** Right-click (or Control-click) `Asset Allocation System.app`, click **Open**, and then click **Open** again in the dialog box.
> - **Windows:** Windows Defender SmartScreen may show a warning saying *"Windows protected your PC"*.  
>   👉 **Fix:** Click **More info**, then click **Run anyway**.

---

## 📸 Screenshots

*(Run the app and paste screenshots of the 4 tabs here)*

```
+------------------------------------------------------------------------+
|  Employees  |  Assets  |  Assign / Transfer / Return  |  Reports       |
+------------------------------------------------------------------------+
|                                                                        |
|  [ Employee ID: _________ ]  [ Name: _________ ]  [ Dept: _________ ]  |
|                                                                        |
|  [ Add Employee ]  [ Clear Form ]                                      |
|                                                                        |
|  +--------------+---------------+---------------+--------------------+ |
|  | Employee ID  | Name          | Department    | Email              | |
|  +--------------+---------------+---------------+--------------------+ |
|  | EMP01        | Alice Smith   | IT            | alice@company.com  | |
|  | EMP02        | Bob Johnson   | HR            | bob@company.com    | |
|  +--------------+---------------+---------------+--------------------+ |
+------------------------------------------------------------------------+
```

---

## 🛠️ Java Concepts Applied

| Concept | Implementation in Code |
| :--- | :--- |
| **Object-Oriented Programming (OOP)** | Clean 3-layer architecture (`model`, `service`, `gui`). |
| **Abstraction** | Abstract parent class `Asset` with abstract method `getCategory()`. |
| **Inheritance** | Subclasses `Laptop`, `Phone`, `Furniture`, and `OtherAsset` extending `Asset`. |
| **Polymorphism** | Dynamic method overriding of `getCategory()` across specialized asset types. |
| **Encapsulation** | All fields are `private` and accessed via validated getters and setters. |
| **Java Collections (HashMap)** | `HashMap<String, Asset>` and `HashMap<String, Employee>` for instant $O(1)$ ID-based lookup. |
| **Java Collections (ArrayList)**| `ArrayList<Record>` for transaction audit history and `ArrayList<String>` for service history. |
| **Enums** | `AssetType`, `AssetCondition`, and `AssetStatus` for compile-time type safety. |
| **Custom Exceptions** | `AssetException` (checked exception) to enforce business rules cleanly. |
| **Input Validation** | Rejection of empty strings, duplicate IDs, and invalid asset conditions. |
| **Java Swing GUI** | Desktop interface using `JFrame`, `JTabbedPane`, `JTable`, `DefaultTableModel`, and `JOptionPane`. |
| **Event Handling** | Standard `ActionListener` anonymous inner classes on button clicks. |

---

## 📋 Business Rules Enforced

1. **Unique Identifiers**: Employee and Asset IDs must be strictly unique across the system.
2. **Mandatory Fields**: Blank or whitespace-only inputs are rejected via `AssetException`.
3. **No Double Assignment**: An asset that is currently `ASSIGNED` cannot be assigned again until returned.
4. **Condition Safety Guard**: Assets in `DAMAGED` or `UNDER_REPAIR` condition cannot be assigned to employees.
5. **Transfer Validation**: An asset can only be transferred if currently `ASSIGNED`, and cannot be transferred to the current holder.
6. **Return Validation**: Only currently `ASSIGNED` assets can be returned to company inventory (`AVAILABLE`).

---

## ⚠️ Known Limitations

1. **In-Memory Storage**: Data is maintained in memory (`HashMap` and `ArrayList`) during runtime. Records reset when the application is closed (no external database or file persistence).
2. **No Delete Feature**: Employees and assets cannot be deleted once added, preserving audit trail integrity as per case study requirements.
3. **No Employee Editing**: Employee details (name, email, department) cannot be edited after initial registration.

---

## 💻 How to Run from Source

If you prefer compiling and running directly from source:

### Prerequisites
- Java Development Kit (JDK 17 or higher)

### 1. Compile All Files
From the project root directory, run:
```bash
javac -d bin $(find src -name "*.java")
```

### 2. Launch the Desktop Swing GUI
```bash
java -cp bin Main
```

### 3. Run the Automated Unit Test Console
```bash
java -cp bin TestConsole
```

### 4. Package into a Runnable JAR
```bash
jar cfe AssetSystem.jar Main -C bin .
java -jar AssetSystem.jar
```

---

## 📜 License
This project is licensed under the [MIT License](LICENSE).
