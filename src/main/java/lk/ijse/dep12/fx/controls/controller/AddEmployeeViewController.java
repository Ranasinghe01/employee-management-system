package lk.ijse.dep12.fx.controls.controller;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import lk.ijse.dep12.fx.controls.Employee;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class AddEmployeeViewController {
    public Label lblEmployeeId;
    public TextField txtName;
    public TextField txtAddress;
    public RadioButton rdButtonMale;
    public ToggleGroup rdBtnGroupGender;
    public RadioButton rdButtonFemale;
    public Label lblName;
    public Label lblAddress;
    public Label lblGender;
    public Label lblId;
    public GridPane mainGridPane;
    public Button btnNewEmployee;
    public TextField txtNIC;
    public Button btnSave;
    public Button btnDelete;
    public TableView<Employee> tblEmployee;
    public Label lblNIC;
    public AnchorPane root;
    ObservableList<Employee> employeeList;
    private final File DB_FILE = new File(".employee.db");


    public void initialize() throws IOException {

        mainGridPane.setDisable(true);
        btnNewEmployee.requestFocus();
        btnSave.setDisable(true);
        btnDelete.setDisable(true);
        employeeList = tblEmployee.getItems();

        tblEmployee.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id")); // set a new property value factory to cell value factory
        tblEmployee.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("nic")); // set a new property value factory to cell value factory
        tblEmployee.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("fullName")); // set a new property value factory to cell value factory
        tblEmployee.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("address")); // set a new property value factory to cell value factory
        tblEmployee.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("gender"));

        loadEmployeeDetails();

        //setting mnemoics
        for (Node node : mainGridPane.lookupAll(".label")) { // searching for the labels in grid pane
            Label lbl = (Label) node;
            lbl.setLabelFor(mainGridPane.lookup(lbl.getAccessibleText()));
        }

        //adding change listener to table
        tblEmployee.getSelectionModel().selectedItemProperty().addListener((observable, previous, current) -> {

            if (current != null) {

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to Delete the selected employee?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> buttonType = alert.showAndWait();

                if (buttonType.get() == ButtonType.YES) {
                    btnDelete.setDisable(false);

                } else {
                    btnDelete.setDisable(true);
                }
            }
            btnDelete.setDisable(current == null);
        });
    }

    private void clearTheForm() {
        lblEmployeeId.setText("");
        txtNIC.clear();
        txtName.clear();
        txtAddress.clear();
        rdBtnGroupGender.selectToggle(null);
        mainGridPane.setDisable(true);
        btnNewEmployee.requestFocus();
        btnDelete.setDisable(true);
    }

    private String generateEmployeeId() {
        if (employeeList.isEmpty()) return "IJSE-0001";
        else {
            int nextIdNum = Integer.parseInt(employeeList.getLast().getId().substring(5)) + 1;
            return "IJSE-%04d".formatted(nextIdNum);
        }
    }

    private void enableRequiredControls() {
        mainGridPane.setDisable(false);
        txtNIC.requestFocus();
    }

    public void btnNewEmployeeOnAction(ActionEvent actionEvent) {

        btnSave.setDisable(false);

        if (!lblEmployeeId.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to add a new employee discarding currently unsaved data?"
                    , ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.get() == ButtonType.YES) {
                clearTheForm();
                lblEmployeeId.setText(generateEmployeeId());
                enableRequiredControls();
            }
        } else {
            lblEmployeeId.setText(generateEmployeeId());
            enableRequiredControls();
        }
    }

    private boolean isAddressValid() {
        String address = txtAddress.getText();
        return address.isEmpty() || address.strip().length() >= 4;
    }

    private boolean isNameValid() {
        String name = txtName.getText().strip();
        if (name.length() < 3) return false;
        //check characters as an array
        for (char c : name.toCharArray()) {
            if (!(Character.isLetter(c) || Character.isSpaceChar(c))) {
                return false;
            }
        }
        return true;
    }

    private boolean isNICValid() {
        String nic = txtNIC.getText().strip();
        if (nic.length() != 10) return false;
        if (!(nic.endsWith("V") || nic.endsWith("v"))) return false;
        for (int i = 0; i < nic.length() - 1; i++) {
            if (!Character.isDigit(nic.charAt(i))) return false;
        }
        return true;
    }

    private boolean existsNic(String nic) {
        for (Employee employee : employeeList) {
            if (employee.getNic().equals(nic)) return true;
        }
        return false;
    }

    private boolean validateData() {

        boolean validation = true;

        // to remove error class from all components in the main grid pane in the beginning
        mainGridPane.lookupAll(".error").forEach(node -> node.getStyleClass().remove("error"));

        if (rdBtnGroupGender.getSelectedToggle() == null) {
            rdButtonFemale.getStyleClass().add("error");
            rdButtonMale.getStyleClass().add("error");
            lblGender.getStyleClass().add("error");
            rdButtonMale.requestFocus();
            validation = false;
        }

        if (!isAddressValid()) {
            txtAddress.getStyleClass().add("error");
            lblAddress.getStyleClass().add("error");
            validation = false;
            txtAddress.requestFocus();
        }

        if (!isNameValid()) {
            txtName.getStyleClass().add("error");
            lblName.getStyleClass().add("error");
            validation = false;
            txtName.requestFocus();
        }

        if (!isNICValid() || existsNic(txtNIC.getText().strip())) {
            txtNIC.getStyleClass().add("error");
            lblNIC.getStyleClass().add("error");
            validation = false;
            txtNIC.requestFocus();
        }

        return validation;
    }

    public void btnSaveOnAction(ActionEvent actionEvent) throws IOException {

        /* Data Validation */
        if (!validateData()) {
            return;
        }

        String id = lblEmployeeId.getText().strip();
        String nic = txtNIC.getText().strip();
        String name = txtName.getText().strip();
        String address = txtAddress.getText().strip();
        String gender = ((RadioButton) (rdBtnGroupGender.getSelectedToggle())).getText();

        /* Business Validation */
        for (Employee employee : employeeList) {
            if (employee.getNic().equals(nic)) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "NIC is already associated with another employee");
                alert.setHeaderText("Duplicate NIC Error");
                alert.showAndWait();
                txtNIC.getStyleClass().add("error");
                txtNIC.requestFocus();
                return;
            }
        }
        Employee employee = new Employee(id, nic, name, address, gender);
        if (saveEmployee(employee)) {
            employeeList.add(employee);
            btnNewEmployee.fire();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to save the employee, something went wrong, try again.");
            alert.setHeaderText("Save Failed");
            alert.show();
        }
    }

    private boolean saveEmployee(Employee employee) throws IOException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DB_FILE, true))) {

//            bw.write( employee.getId() + "@"
//                    + employee.getNic() + "@"
//                    + employee.getFullName() + "@"
//                    + employee.getAddress() + "@"
//                    + employee.getGender() + "\n");

            bw.write(employee.getId() + "\n"
                    + employee.getNic() + "\n"
                    + employee.getFullName() + "\n"
                    + employee.getAddress() + "\n"
                    + employee.getGender() + "\n\n");

            clearTheForm();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidNic(String nic) {
        if (nic.length() != 10 || !nic.toUpperCase().endsWith("V")) return false;
        for (char c : nic.substring(0, 9).toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    private boolean isValidName(String name) {
        for (char c : name.toCharArray()) {
            if (!Character.isLetter(c) || !Character.isSpaceChar(c)) return false;
        }
        return true;
    }

    private void loadEmployeeDetails() {

        try {
            if (!DB_FILE.exists()) {
                DB_FILE.createNewFile();
                return;
            }

            try (BufferedReader br = new BufferedReader(new FileReader(DB_FILE))) {
                //01.
                while (true) {
                    String id = br.readLine();
                    if (id == null) break;
                    String nic = br.readLine();
                    String name = br.readLine();
                    String address = br.readLine();
                    String gender = br.readLine();
                    String newLine = br.readLine();

                    if (name == null || address == null || gender == null || newLine == null ||
                            !isValidNic(nic) || !isValidName(name) ||
                            address.length() < 4 || !(gender.equals("Male") || gender.equals("Female")) ||
                            !newLine.equals("")) {

                        Alert alert = new Alert(Alert.AlertType.ERROR, "Corrupted database found. Do you want to reinitialize the database?",
                                ButtonType.YES, ButtonType.NO);
                        alert.setHeaderText("Error");
                        Optional<ButtonType> buttonType = alert.showAndWait();
                        if (buttonType.get() == ButtonType.YES) {
                            DB_FILE.delete();
                            DB_FILE.createNewFile();
                            return;
                        } else {
                            Platform.exit();
                            return;
                        }
                    } else {
                        employeeList = tblEmployee.getItems();
                        employeeList.add(new Employee(id, nic, name, address, gender));
                    }
                }
                //02.
//            String line;
//            while ((line = br.readLine()) != null) {
//                    String[] empDetails = line.split("@");      //line string is divide by ($) and create String array by String parts  ($) can't be use for splitter
//                    Employee employee = new Employee(empDetails[0], empDetails[1], empDetails[2], empDetails[3], empDetails[4]);
//                    employeeList.add(employee);
//                }
            }

        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong. Try again. If the problem persists, contact Developer");
            alert.setHeaderText("Loading Error");
            alert.showAndWait();
            e.printStackTrace();
            Platform.exit();       //Stop Running JVM
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) throws IOException {

        ObservableList<Employee> employeeList = tblEmployee.getItems();

        if (deleteEmployee(tblEmployee.getSelectionModel().getSelectedItem())){
            employeeList.remove(tblEmployee.getSelectionModel().getSelectedItem());
            if (employeeList.isEmpty()) btnNewEmployee.fire();

        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to delete the employee, try again!");
            alert.setHeaderText("Delete Failed");
            alert.show();
        }

        //set delete button to work on delete key press
        root.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE) {
                btnDelete.fire();
            }
        });
    }

    private boolean deleteEmployee(Employee deleteEmployee) throws IOException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DB_FILE))) {
            ObservableList<Employee> employeeList = tblEmployee.getItems();
            for (Employee employee : employeeList) {
                if (deleteEmployee == employee) continue;

                bw.write(employee.getId() + "\n");
                bw.write(employee.getNic() + "\n");
                bw.write(employee.getFullName() + "\n");
                bw.write(employee.getAddress() + "\n");
                bw.write(employee.getGender() + "\n\n");

            }
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
