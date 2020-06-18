package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class BrandController implements Initializable {

    Connection connect = new Connection();

    public TextField brandField, idField;
    public Button addButton, delButton;

    ObservableList<ModelTableBrand> oblist = FXCollections.observableArrayList();

    String ID = "";


    @FXML
    private TableView<ModelTableBrand> brandTable;


    // Function that return value of brandField textfield
    public String getBrandField() {
        return brandField.getText();
    }

    // Function that return value of idField textfield
    public String getIdField() {
        return idField.getText();
    }


    // addButton function
    public void addButton() {
        try {

            // Check if all input is already filled
            // If not, it will show a pop up message
            if (getBrandField().isEmpty() || getIdField().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Warning");
                a.setContentText("All Data Must be Filled!");
                a.show();
            } else {

                // Validate user input
                if (getBrandField().length() <= 15 && getIdField().length() == 4) {
                    if (dataBaseCheck()) {
                        Alert alert1 = new Alert(Alert.AlertType.ERROR);
                        alert1.setTitle("ERROR");
                        alert1.setContentText("Check the input for duplicate value!");
                        alert1.setHeaderText("SOMETHING WRONG");
                        alert1.show();
                    } else {
                        PreparedStatement prepStat = connect.getPrepStat("INSERT INTO brand VALUES(?, ?)");
                        prepStat.setString(1, getIdField());
                        prepStat.setString(2, getBrandField());
                        prepStat.executeUpdate();
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("Info");
                        alert1.setContentText("Success add new category!");
                        alert1.setHeaderText("SUCCESS");
                        alert1.show();
                        clearField();
                    }
                    brandTable.getItems().clear();
                    showTable();
                } else {
                    Alert alert3 = new Alert(Alert.AlertType.WARNING);
                    alert3.setTitle("Warning");
                    alert3.setContentText("Check all inputs!");
                    alert3.show();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Function that clear value
    public void clearField() {
        brandField.setText("");
        idField.setText("");
    }


    // Function to check the database if data is already inside
    // If yes, it will return true
    public boolean dataBaseCheck() {
        PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM brand WHERE ID='" + getIdField() + "'");
        PreparedStatement prepStat2 = connect.getPrepStat("SELECT * FROM brand WHERE Brand='" + getBrandField() + "'");
        try {
            ResultSet rs = prepStat.executeQuery();
            ResultSet rs2 = prepStat2.executeQuery();
            return rs.next() || rs2.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }


    // Function that filled the table
    public void showTable() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM brand");
            ResultSet rs = prepStat.executeQuery();

            while (rs.next()) {
                oblist.add((new ModelTableBrand(rs.getString("ID"), rs.getString("Brand"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Function that get value from the table
    // from the selected row
    public void getVal() {
        ModelTableBrand brand = brandTable.getSelectionModel().getSelectedItem();
        ID = brand.getId();
        idField.setText(ID);
        String Brand = brand.getBrand();
        brandField.setText(Brand);
    }


    // Function that check if data being in used
    // If yes, it will return true
    public boolean isUsed() {
        PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM product WHERE Brand='" + getBrandField() + "'");
        try {
            ResultSet rs = prepStat.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // Function to delete data
    public void deleteButton() {

        // If isUsed() function return true
        // it will show a pop up message and not delete the data from database
        if (isUsed()) {
            Alert alert1 = new Alert(Alert.AlertType.WARNING);
            alert1.setTitle("WARNING");
            alert1.setContentText("The data that you want to delete is currently in use!");
            alert1.setHeaderText("CANNOT DELETE THIS DATA");
            alert1.show();
        } else {
            deleteBrand();
        }

        clearField();
        brandTable.getItems().clear();
        showTable();
    }


    // Function that delete brand from database
    public void deleteBrand() {
        try {
            Alert alert4 = new Alert(Alert.AlertType.CONFIRMATION);
            alert4.setTitle("Confirmation");
            alert4.setContentText("This will remove it permanently from the database.");
            alert4.setHeaderText("Are you sure want to delete this category?");
            Optional<ButtonType> result = alert4.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                PreparedStatement prepStat = connect.getPrepStat("DELETE FROM brand WHERE ID = ?");
                prepStat.setString(1, ID);
                prepStat.executeUpdate();
            } else {
                alert4.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Initialize function
    @Override
    public void initialize(URL location, ResourceBundle resource) {
        showTable();

        TableColumn idCol = new TableColumn("First Name");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableBrand, String>("id"));

        TableColumn catCol = new TableColumn("Brand");
        catCol.setMinWidth(250);
        catCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableBrand, String>("brand"));
        brandTable.setItems(oblist);
        brandTable.getColumns().addAll(idCol, catCol);

    }
}
