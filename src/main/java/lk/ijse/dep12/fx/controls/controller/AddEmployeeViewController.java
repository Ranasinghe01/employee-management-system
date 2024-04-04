package lk.ijse.dep12.fx.controls.controller;

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
    public Button btnSaveOrUpdate;
    public Button btnDelete;
    public TableView<Employee> tblEmployee;
    public Label lblNIC;
    public AnchorPane root;
    ObservableList<Employee> employeeList;

    private boolean onceTriedToSave = false;

    public void initialize() throws IOException{
        mainGridPane.setDisable(true);
        btnNewEmployee.requestFocus();
        btnSaveOrUpdate.setDisable(true);
        btnDelete.setDisable(true);
        employeeList = tblEmployee.getItems();

        tblEmployee.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id")); // set a new property value factory to cell value factory
        tblEmployee.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("nic")); // set a new property value factory to cell value factory
        tblEmployee.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("fullName")); // set a new property value factory to cell value factory
        tblEmployee.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("address")); // set a new property value factory to cell value factory
        tblEmployee.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("gender"));

        File file = new File(".employee.db");

        if (!file.exists()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Data File Not Found! ");
            alert.show();
        }

        try(BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] empDetails = line.split("@");      //line string is divide by ($) and create String array by String parts  ($) can't be use for splitter
                Employee employee = new Employee(empDetails[0], empDetails[1], empDetails[2], empDetails[3], empDetails[4]);
                employeeList.add(employee);
            }
        }


        //set delete button to work on delete key press
        root.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE) {
                btnDelete.fire();
            }
        });

        //setting mnemoics
        for (Node node : mainGridPane.lookupAll(".label")) { // searching for the labels in grid pane
            Label lbl = (Label) node;
            lbl.setLabelFor(mainGridPane.lookup(lbl.getAccessibleText()));
        }

        //adding change listener to table
        tblEmployee.getSelectionModel().selectedItemProperty().addListener((observable, previous, current) -> {

            if (current != null) {

                btnDelete.setDisable(false);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want to Update or Delete the selected employee?", ButtonType.YES, ButtonType.NO);
                Optional<ButtonType> buttonType = alert.showAndWait();

                if (buttonType.get() == ButtonType.YES) {
//                    clearTheForm();
                    enableRequiredControls();
                    onceTriedToSave = true; // specify once tried to save
                    btnSaveOrUpdate.setText("Update");

                    //Employee employee = current;
                    lblEmployeeId.setText(current.getId());
                    txtNIC.setText(current.getNic());
                    txtName.setText(current.getFullName());
                    txtAddress.setText(current.getAddress());
                    if (current.getGender().equals("Male")) rdButtonMale.setSelected(true);
                    else rdButtonFemale.setSelected(true);


                } else {
                    btnDelete.setDisable(false);
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

    private boolean isAddressValid() {
        String address = txtAddress.getText();
        return address.isEmpty() || address.strip().length() >= 4;
    }

    private void enableRequiredControls() {
        mainGridPane.setDisable(false);
        txtNIC.requestFocus();
    }

    public void btnNewEmployeeOnAction(ActionEvent actionEvent) {

        btnSaveOrUpdate.setDisable(false);

        if (!lblEmployeeId.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you wish to add a new employee discarding currently unsaved data?"
                    , ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> buttonType = alert.showAndWait();

            if (buttonType.get() == ButtonType.YES) {
                btnSaveOrUpdate.setText("Save");
                clearTheForm();
                lblEmployeeId.setText(generateEmployeeId());
                enableRequiredControls();
            }
        } else {
            btnSaveOrUpdate.setText("Save");
            lblEmployeeId.setText(generateEmployeeId());
            enableRequiredControls();
        }
    }

    public void btnSaveOrUpdateOnAction(ActionEvent actionEvent) throws IOException {

        // to remove error class from all components in the main grid pane in the beginning
        for (Node node : mainGridPane.lookupAll(".error")) {
            node.getStyleClass().remove("error");
        }

        boolean validation = true;

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

        if (!validation) return;

        //if updating
        if (onceTriedToSave) {
            employeeList.remove(tblEmployee.getSelectionModel().getSelectedItem());
        }

        File file = new File(".employee.db");

        if (!file.exists()) file.createNewFile();

        Employee employee = new Employee(lblEmployeeId.getText(),
                txtNIC.getText().strip(),
                txtName.getText().strip(),
                txtAddress.getText().strip(),
                ((RadioButton) rdBtnGroupGender.getSelectedToggle()).getText()
        );

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {

            bw.write( employee.getId() + "@"
                    + employee.getNic() + "@"
                    + employee.getFullName() + "@"
                    + employee.getAddress() + "@"
                    + employee.getGender() + "\n");
        }
        employeeList.add(employee);

        onceTriedToSave = false;
        btnSaveOrUpdate.setText("Save");
        clearTheForm();
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {

        employeeList.remove(tblEmployee.getSelectionModel().getSelectedItem());
        tblEmployee.getSelectionModel().clearSelection();

        // generate employee id again if already was trying to enter a new employee while deleting existing one
        if (!lblEmployeeId.getText().isEmpty()) {
            lblEmployeeId.setText(generateEmployeeId());
        }
    }
}

