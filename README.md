# Course Registration & Management System (CRS)

## Overview

This repository contains a Java Swing-based desktop application designed to handle university operations, student enrollments, academic progression, and performance recovery paths. The application implements an Object-Oriented Development with Java (OODJ) architecture, completely isolating responsibilities using a Data Access Object (DAO) pattern to handle CSV-based data updates.

---

## Architecture and Key Modules

### 1. Presentation Layer (`ui`)

The user interface uses Java Swing graphical components to provide interactive dashboards specialized by user roles:

* **Login & Registration Views:** Handles basic access authentication and registration for pending accounts.
* **Dashboards:** Main hub navigation tailored distinctively for Students, Academic Officers, and Course Administrators.
* **Management Sub-Panels:** Includes specialized screens such as account approval tables, enrollment choice configuration boards, student file summaries, and milestone tracking panels.

### 2. Business Logic & Domain Layer (`domain` & `academic`)

Manages business requirements, verification metrics, and system operations:

* **Role-Based Access Management:** Controls behavioral differences across structural roles (`Student`, `AcademicOfficer`, `CourseAdministrator`).
* **Course & Enrollment Progression:** Handles prerequisite evaluations, limits core registration windows, and handles workflow approval tracking.
* **Performance Recovery Plans:** A unique framework allowing the institution to step in with milestones and track progression markers for students requiring academic recovery attention.

### 3. Data Access Layer (`service` & `data_access`)

Employs standard text file persistence mechanisms:

* **DAO Patterns:** Data Access Objects such as `UserDAO`, `StudentDAO`, and `AcademicRecordDAO` read from and write updates to the local text flat files.
* **Exporter Utilities:** Contains structural logic to output performance portfolios and student reports as PDFs.

---

## File Structure & Storage Design

The system keeps tracks of structural university profiles across several local CSV databases:

* `data/student_information.csv`: Houses essential demographic fields, major designations, and emails mapped to individual `StudentID` markers.
* `data/user_credentials.csv`: Stores matching system access rows, cryptographic password hashes, status flags (`true`/`false`), and operational role identifiers (`Student`, `Academic Officer`, `Course Administrator`, `Pending`).
* `config.properties`: Retains desktop configuration data such as system time zones and the `last_email` identity context used during runtime sessions.

---

## Getting Started

### Prerequisites

* Java Development Kit (JDK) 8 or higher
* An IDE supporting Java Swing development (such as IntelliJ IDEA or Eclipse)

### How to Build and Run

1. Clone this repository locally.
2. Ensure that your workspace references the necessary graphical or text utility libraries if required (e.g., PDF generation components).
3. Execute the main compilation sequence via `Main.java`.

The application initializes by loading configuration elements and executing the primary window context on the Event Dispatch Thread:

```java
import ui.LoginView;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginView().setVisible(true);
        });
    }
}

```

---

## User Access Matrix

For validation testing during development, you can use the default credential profiles stored within the local filesystem layout:

| User ID | Role | Associated Email Address |
| --- | --- | --- |
| **AO001** | Academic Officer | officer@university.edu |
| **CA001** | Course Administrator | admin@university.edu |
| **S001** | Student | fiona.smith@university.edu |
