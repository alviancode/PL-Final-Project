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
    String ID = "";
    Connection connect = new Connection();
    public TextField brandField, idField;
    public Button addButton, delButton;
    ObservableList<ModelTableBrand> oblist = FXCollections.observableArrayList();

    @FXML
    private TableView<ModelTableBrand> brandTable;


    public String getBrandField() {
        return brandField.getText();
    }

    public String getIdField() {
        return idField.getText();
    }

    public void addButton() {
        try {
            if (getBrandField().isEmpty() || getIdField().isEmpty()) {
                Alert a = new Alert(Alert.AlertType.WARNING);
                a.setTitle("Warning");
                a.setContentText("All Data Must be Filled!");
                a.show();

            } else {

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

    public void clearField() {
        brandField.setText("");
        idField.setText("");
    }

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

    public void getVal() {
        ModelTableBrand brand = brandTable.getSelectionModel().getSelectedItem();
        ID = brand.getId();
        idField.setText(ID);
        String Brand = brand.getBrand();
        brandField.setText(Brand);
    }

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

    public void deleteButton() {
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
