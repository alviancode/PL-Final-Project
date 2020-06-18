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


// CategoryController class which implements Initializable
public class CategoryController implements Initializable {

    Connection connect = new Connection();

    public TextField catField, idField;
    public Button addButton, delButton;

    ObservableList<ModelTableCat> oblist = FXCollections.observableArrayList();

    String ID = "";


    @FXML
    private TableView<ModelTableCat> catTable;


    // Function that return value of catField textfield
    public String getCatField() {
        return catField.getText();
    }

    // Function that return value of idField textfield
    public String getIdField() {
        return idField.getText();
    }


    // Function that add new category to database
    public void addCategory() {
        try {

            // Validate if all data is already filled by the user
            // If not it will pop up a message
            if (getCatField().isEmpty() || getIdField().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Warning");
                a.setContentText("All Data Must be Filled!");
                a.show();

            } else {

                // Validate if user input is appropriate by the length
                if (getCatField().length() <= 15 && getIdField().length() == 4) {

                    // Check if data is already inside database or not
                    // If true, it will pop up a message and not input the data to database
                    if (dataBaseCheck()) {
                        Alert alert1 = new Alert(Alert.AlertType.ERROR);
                        alert1.setTitle("ERROR");
                        alert1.setContentText("Check the input for duplicate value!");
                        alert1.setHeaderText("SOMETHING WRONG");
                        alert1.show();

                        // If data is all valid
                        // It will insert the data to database
                    } else {
                        PreparedStatement prepStat = connect.getPrepStat("INSERT INTO category VALUES(?, ?)");
                        prepStat.setString(1, getIdField());
                        prepStat.setString(2, getCatField());
                        prepStat.executeUpdate();

                        // Pop up a message that show the user the data is already inserted
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("Info");
                        alert1.setContentText("Success add new category!");
                        alert1.setHeaderText("SUCCESS");
                        alert1.show();
                    }
                    clearField();
                    catTable.getItems().clear();
                    showTable();

                    // If user not filled all data
                    // It will show a pop up message
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


    // dataBaseCheck Function
    // Function that return the value of boolean
    // If data is already inside database
    // It will return false
    public boolean dataBaseCheck() {
        PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM category WHERE ID='" + getIdField() + "'");
        PreparedStatement prepStat2 = connect.getPrepStat("SELECT * FROM category WHERE Category='" + getCatField() + "'");
        try {
            ResultSet rs = prepStat.executeQuery();
            ResultSet rs2 = prepStat2.executeQuery();
            return rs.next() || rs2.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }


    // Function that fill catTable
    public void showTable() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM category");
            ResultSet rs = prepStat.executeQuery();

            while (rs.next()) {
                oblist.add((new ModelTableCat(rs.getString("ID"), rs.getString("Category"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Function that get the data from the table if user click a specific row
    // And set idField and catField value from table
    public void getVal() {
        ModelTableCat category = catTable.getSelectionModel().getSelectedItem();
        ID = category.getId();
        idField.setText(ID);
        String Category = category.getCategory();
        catField.setText(Category);
    }


    // Function that check the data is being used in another database
    //If yes, it will return true
    public boolean isUsed() {
        PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM product WHERE Category'" + getCatField() + "'");
        try {
            ResultSet rs = prepStat.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    // deleteButton function
    public void deleteButton() {

        // If isUsed() function return true
        // It will pop up a message and not delete the data from database
        if (isUsed()) {
            Alert alert1 = new Alert(Alert.AlertType.WARNING);
            alert1.setTitle("WARNING");
            alert1.setContentText("The data that you want to delete is currently in use!");
            alert1.setHeaderText("CANNOT DELETE THIS DATA");
            alert1.show();

            // If isUsed() return false
            // It will delete the data from database
        } else {
            deleteCategory();
            clearField();
            catTable.getItems().clear();
            showTable();
        }
    }


    // Function that do the deletion of the data from database
    public void deleteCategory() {
        try {

            // It will pop up a confirmation
            Alert alert4 = new Alert(Alert.AlertType.CONFIRMATION);
            alert4.setTitle("Confirmation");
            alert4.setContentText("This will remove it permanently from the database.");
            alert4.setHeaderText("Are you sure want to delete this category?");
            Optional<ButtonType> result = alert4.showAndWait();

            // If user pressed "OK"
            if (result.isPresent() && result.get() == ButtonType.OK) {
                PreparedStatement prepStat = connect.getPrepStat("DELETE FROM category WHERE ID = ?");
                prepStat.setString(1, ID);
                prepStat.executeUpdate();

                //If user press cancel
            } else {
                alert4.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Function that clear all textfield value
    public void clearField() {
        catField.setText("");
        idField.setText("");
    }


    // Initialize function
    @Override
    public void initialize(URL location, ResourceBundle resource) {
        showTable();

        TableColumn idCol = new TableColumn("ID");
        idCol.setMinWidth(100);
        idCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCat, String>("id"));

        TableColumn catCol = new TableColumn("Category");
        catCol.setMinWidth(250);
        catCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCat, String>("category"));
        catTable.setItems(oblist);
        catTable.getColumns().addAll(idCol, catCol);
    }
}
