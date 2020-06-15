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

public class CategoryController implements Initializable {
    String ID = "";
    ObservableList<ModelTableCat> oblist = FXCollections.observableArrayList();
    Connection connect = new Connection();
    public TextField catField, idField;
    public Button addButton, delButton;

    @FXML
    private TableView<ModelTableCat> catTable;

    public String getCatField() {
        return catField.getText();
    }

    public String getIdField() {
        return idField.getText();
    }

    public void addCategory() {
        try {
            if (getCatField().isEmpty() || getIdField().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Warning");
                a.setContentText("All Data Must be Filled!");
                a.show();

            } else {
                if (getCatField().length() <= 15 && getIdField().length() == 4) {
                    if (dataBaseCheck()) {
                        Alert alert1 = new Alert(Alert.AlertType.ERROR);
                        alert1.setTitle("ERROR");
                        alert1.setContentText("Check the input for duplicate value!");
                        alert1.setHeaderText("SOMETHING WRONG");
                        alert1.show();
                    } else {
                        PreparedStatement prepStat = connect.getPrepStat("INSERT INTO category VALUES(?, ?)");
                        prepStat.setString(1, getIdField());
                        prepStat.setString(2, getCatField());
                        prepStat.executeUpdate();
                        Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                        alert1.setTitle("Info");
                        alert1.setContentText("Success add new category!");
                        alert1.setHeaderText("SUCCESS");
                        alert1.show();
                    }
                    clearField();
                    catTable.getItems().clear();
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

    public void clearField() {
        catField.setText("");
        idField.setText("");
    }

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

    public void getVal() {
        ModelTableCat category = catTable.getSelectionModel().getSelectedItem();
        ID = category.getId();
        idField.setText(ID);
        String Category = category.getCategory();
        catField.setText(Category);
    }

    public boolean isUsed(){
        PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM product WHERE Category'" + getCatField() +"'");
        try {
            ResultSet rs = prepStat.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteButton(){
        if(isUsed()){
            Alert alert1 = new Alert(Alert.AlertType.WARNING);
            alert1.setTitle("WARNING");
            alert1.setContentText("The data that you want to delete is currently in use!");
            alert1.setHeaderText("CANNOT DELETE THIS DATA");
            alert1.show();
        } else{
            deleteCategory();
        }
    }

    public void deleteCategory() {
        try {
            Alert alert4 = new Alert(Alert.AlertType.CONFIRMATION);
            alert4.setTitle("Confirmation");
            alert4.setContentText("This will remove it permanently from the database.");
            alert4.setHeaderText("Are you sure want to delete this category?");
            Optional<ButtonType> result = alert4.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                PreparedStatement prepStat = connect.getPrepStat("DELETE FROM category WHERE ID = ?");
                prepStat.setString(1, ID);
                prepStat.executeUpdate();
                clearField();
                catTable.getItems().clear();
                showTable();
            } else {
                alert4.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
