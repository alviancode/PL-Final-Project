package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigInteger;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ProductController implements Initializable {
    Connection connect = new Connection();
    public TextField prodField, priceField, qtyField, barcodeField;
    public TextArea descArea;
    public ComboBox catCombo, brandCombo;
    public Button addButton, delButton;
    ObservableList<ModelTableProd> oblist = FXCollections.observableArrayList();

    @FXML
    private TableView<ModelTableProd> prodTable;


    public String getCatCombo() {
        String catComboValue = (String) catCombo.getValue();
        return catComboValue;
    }

    public String getBrandCombo() {
        String brandComboValue = (String) brandCombo.getValue();
        return brandComboValue;
    }

    public String getProd() {
        return prodField.getText();
    }

    public String getDesc() {
        return descArea.getText();
    }

    public String getQty() {
        try {
            Integer.parseInt(qtyField.getText());
            return qtyField.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getRetailPrice() {
        try {
            Integer.parseInt(priceField.getText());
            return priceField.getText();
        } catch (Exception e) {
            return "";
        }
    }

    public String getBarcode() {
        return barcodeField.getText();
    }

    public void setDB() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("INSERT INTO product VALUES(?, ?, ?, ?, ?, ?,?)");
            prepStat.setString(1, getProd());
            prepStat.setString(2, getDesc());
            prepStat.setString(3, getCatCombo());
            prepStat.setString(4, getBrandCombo());
            prepStat.setString(5, getQty());
            prepStat.setString(6, getRetailPrice());
            prepStat.setString(7, getBarcode());

            prepStat.executeUpdate();
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Info");
            alert1.setContentText("Success add new category!");
            alert1.setHeaderText("SUCCESS");
            alert1.show();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resource) {
        showTable();

        TableColumn prodCol = new TableColumn("Product");
        prodCol.setMinWidth(100);
        prodCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableProd, String>("product"));

        TableColumn descCol = new TableColumn("Description");
        descCol.setMinWidth(180);
        descCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableProd, String>("desc"));

        TableColumn catCol = new TableColumn("Category");
        catCol.setMinWidth(100);
        catCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableProd, String>("category"));

        TableColumn brandCol = new TableColumn("Brand");
        brandCol.setMinWidth(100);
        brandCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableProd, String>("brand"));

        TableColumn priceCol = new TableColumn("Price");
        priceCol.setMinWidth(100);
        priceCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableProd, String>("price"));

        TableColumn qtyCol = new TableColumn("Qty");
        qtyCol.setMinWidth(50);
        qtyCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableProd, String>("qty"));

        TableColumn barcodeCol = new TableColumn("Barcode");
        barcodeCol.setMinWidth(100);
        barcodeCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableProd, String>("barcode"));

        prodTable.setItems(oblist);
        prodTable.getColumns().addAll(prodCol, descCol, catCol, brandCol, qtyCol, priceCol, barcodeCol);

    }


    public void showTable() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM product");
            ResultSet rs = prepStat.executeQuery();

            while (rs.next()) {
                oblist.add((new ModelTableProd(rs.getString("Product"), rs.getString("Description"), rs.getString("Category"), rs.getString("Brand"), rs.getString("Qty"), rs.getString("RetailPrice"), rs.getString("Barcode"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void editButton() {
        PreparedStatement prepStat = connect.getPrepStat("UPDATE product SET Product = ?, Description = ?, Category = ?, Brand = ?, Qty = ?, RetailPrice = ?, Barcode = ?  WHERE Barcode = '" + getBarcode() + "'");
        try {
            prepStat.setString(1, getProd());
            prepStat.setString(2, getDesc());
            prepStat.setString(3, getCatCombo());
            prepStat.setString(4, getBrandCombo());
            prepStat.setString(5, getQty());
            prepStat.setString(6, getRetailPrice());
            prepStat.setString(7, getBarcode());
            prepStat.executeUpdate();
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Info");
            alert1.setContentText("CHANGE SAVED");
            alert1.setHeaderText("Success updated product!");
            alert1.show();
            clearField();
            prodTable.getItems().clear();
            showTable();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshButton() {
        barcodeField.setEditable(true);
        clearField();
        prodTable.getItems().clear();
        showTable();
    }


    public void addButton() {
        if (getProd().isEmpty() || getCatCombo() == null || getBrandCombo() == null || getBarcode().isEmpty() ||
                getDesc().isEmpty() || getQty().isEmpty() || getRetailPrice().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Warning");
            a.setContentText("Check data input!");
            a.show();
        } else {
            getRetailPrice();
            if (getProd().length() <= 15 & getBarcode().length() == 4 && getDesc().length() <= 255 &&
                    getQty().length() <= 255 && getRetailPrice().length() <= 255) {
                if (dataBaseCheck()) {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("ERROR");
                    alert1.setContentText("Barcode cannot be repeated!");
                    alert1.setHeaderText("SOMETHING WRONG");
                    alert1.show();
                } else {
                    setDB();
                    clearField();
                    prodTable.getItems().clear();
                    showTable();
                }

            } else {
                Alert alert3 = new Alert(Alert.AlertType.WARNING);
                alert3.setTitle("Warning");
                alert3.setContentText("Check all inputs!");
                alert3.show();
            }
        }
    }

    public void catCombo() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM category");
            ResultSet rs = prepStat.executeQuery();
            catCombo.getItems().clear();
            while (rs.next()) {
                catCombo.getItems().add(rs.getString("Category"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void brandCombo() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM brand");
            ResultSet rs = prepStat.executeQuery();
            brandCombo.getItems().clear();
            while (rs.next()) {
                brandCombo.getItems().add(rs.getString("Brand"));
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void getVal() {
        barcodeField.setEditable(false);
        ModelTableProd product = prodTable.getSelectionModel().getSelectedItem();
        prodField.setText(product.getProduct());
        brandCombo.setValue(product.getBrand());
        catCombo.setValue(product.getCategory());
        qtyField.setText(product.getQty());
        priceField.setText(product.getPrice());
        descArea.setText(product.getDesc());
        barcodeField.setText(product.getBarcode());
    }


    public void deleteProduct() {
        try {
            Alert alert4 = new Alert(Alert.AlertType.CONFIRMATION);
            alert4.setTitle("Confirmation");
            alert4.setContentText("This will remove it permanently from the database.");
            alert4.setHeaderText("Are you sure want to delete this product?");
            Optional<ButtonType> result = alert4.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                PreparedStatement prepStat = connect.getPrepStat("DELETE FROM product WHERE Barcode = ?");
                prepStat.setString(1, getBarcode());
                prepStat.executeUpdate();
                clearField();
                prodTable.getItems().clear();
                showTable();
            } else {
                alert4.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean dataBaseCheck() {
        PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM product WHERE Barcode='" + getBarcode() + "'");
        try {
            ResultSet rs = prepStat.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    public void clearField() {
        prodField.setText("");
        priceField.setText("");
        qtyField.setText("");
        barcodeField.setText("");
        descArea.setText("");
        catCombo.setValue("");
        brandCombo.setValue("");
    }
}
