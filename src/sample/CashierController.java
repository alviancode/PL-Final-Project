package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class CashierController implements Initializable {
    Connection connect = new Connection();
    public TextField barcodeField, prodField, priceField, qtyField, totalField, changeField, payField;
    public Button addButton, delButton;
    ObservableList<ModelTableCashier> oblist = FXCollections.observableArrayList();
    int ID;
    long subTotal;
    long change;

    @FXML
    private TableView<ModelTableCashier> cashierTable;

    public String getBarcodeField() {
        return barcodeField.getText();
    }

    public String getProdField() {
        return prodField.getText();
    }

    public String getPriceField() {
        return priceField.getText();
    }

    public String getQtyField() {
        return qtyField.getText();
    }

    public String getPayField() {
        return payField.getText();
    }


    public void productSearch() {
        PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM product WHERE Barcode='" + getBarcodeField() + "'");
        try {
            ResultSet rs = prepStat.executeQuery();
            if (!rs.next()) {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("ERROR");
                alert1.setContentText("Barcode Not Found!");
                alert1.setHeaderText("SOMETHING WRONG");
                alert1.show();
            } else {
                String productName = rs.getString("Product");
                String price = rs.getString("RetailPrice");
                prodField.setText(productName.trim());
                priceField.setText(price.trim());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void calculateSubTotal() {
        subTotal = 0;
        PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM cashier");
        try {
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                subTotal += rs.getInt("Total");
            }
            totalField.setText(String.valueOf(subTotal));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void payInvoiceButton() {
        if (Integer.parseInt(getPayField()) >= Integer.parseInt(totalField.getText())) {
            calculateChange();
            receipt();
            clearDB();
            totalField.setText("");
            payField.setText("");
            cashierTable.getItems().clear();
            showTable();
        } else {
            Alert alert1 = new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("ERROR");
            alert1.setContentText("Pay Amount cannot smaller than total");
            alert1.setHeaderText("Pay Amount Error!");
            alert1.show();
        }
    }

    public void receipt() {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("Receipt.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert root != null;
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
    }

    public void calculateChange() {
        change = 0;
        change = Integer.parseInt(getPayField()) - Integer.parseInt(totalField.getText());
        changeField.setText(String.valueOf(change));
    }

    public void updateCheckQty() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM product WHERE Barcode='" + getBarcodeField() + "'");
            PreparedStatement prepStat2 = connect.getPrepStat("UPDATE product SET Qty = ? WHERE Barcode = '" + getBarcodeField() + "'");
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                int availableQty = rs.getInt("Qty");
                if (Integer.parseInt(getQtyField()) > availableQty) {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("ERROR");
                    alert1.setContentText("Available Quantity = " + availableQty);
                    alert1.setHeaderText("QUANTITY IS NOT ENOUGH!");
                    alert1.show();
                } else {
                    prepStat2.setString(1, String.valueOf(availableQty - Integer.parseInt(getQtyField())));
                    prepStat2.executeUpdate();
                    addToDB();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addToDB() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("INSERT INTO cashier(Barcode, Product, Quantity, Total)" + "VALUES(?, ?, ?, ?)");
            prepStat.setString(1, getBarcodeField());
            prepStat.setString(2, getProdField());
            prepStat.setString(3, getQtyField());
            prepStat.setString(4, String.valueOf(Integer.parseInt(getPriceField()) * Integer.parseInt(getQtyField())));
            prepStat.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addButton() {
        if (getBarcodeField().isEmpty() || getQtyField().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Warning");
            a.setContentText("All Data Must be Filled!");
            a.show();
        } else {
            updateCheckQty();
            calculateSubTotal();
            cashierTable.getItems().clear();
            showTable();
            clearField();
        }
        delButton.setDisable(true);
    }

    public void clearField() {
        barcodeField.setText("");
        prodField.setText("");
        priceField.setText("");
        qtyField.setText("");
    }

    public void clearDB() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("TRUNCATE cashier");
            prepStat.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showTable() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM cashier");
            ResultSet rs = prepStat.executeQuery();

            while (rs.next()) {
                oblist.add((new ModelTableCashier(rs.getString("ID"), rs.getString("Barcode"), rs.getString("Product"), rs.getString("Quantity"), rs.getString("Total"))));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getVal() {
        try {
            addButton.setDisable(true);
            delButton.setDisable(false);
            ModelTableCashier cashier = cashierTable.getSelectionModel().getSelectedItem();
            ID = Integer.parseInt(cashier.getId());
            barcodeField.setText(cashier.getBarcode());
            priceField.setText("******");
            qtyField.setText(cashier.getQty());
        } catch (Exception e) {
            System.out.println("ERROR!");
        }
    }

    public void deleteButton() {
        try {
            addButton.setDisable(false);

            PreparedStatement prepStat = connect.getPrepStat("DELETE FROM cashier WHERE ID = ?");
            prepStat.setString(1, String.valueOf(ID));
            prepStat.executeUpdate();
            delButton.setDisable(true);
            calculateSubTotal();

            updateQtyCancel();
            cashierTable.getItems().clear();
            showTable();
            clearField();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateQtyCancel() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM product WHERE Barcode='" + getBarcodeField() + "'");
            PreparedStatement prepStat2 = connect.getPrepStat("UPDATE product SET Qty = ? WHERE Barcode = '" + getBarcodeField() + "'");
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                int availableQty = rs.getInt("Qty");
                prepStat2.setString(1, String.valueOf(availableQty + Integer.parseInt(getQtyField())));
                prepStat2.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resource) {
        calculateSubTotal();
        showTable();

        TableColumn idCol = new TableColumn("ID");
        idCol.setMinWidth(30);
        idCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCashier, String>("id"));
        TableColumn barcodeCol = new TableColumn("Barcode");
        barcodeCol.setMinWidth(50);
        barcodeCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCashier, String>("barcode"));
        TableColumn prodCol = new TableColumn("Product");
        prodCol.setMinWidth(200);
        prodCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCashier, String>("product"));
        TableColumn qtyCol = new TableColumn("Qty");
        qtyCol.setMinWidth(100);
        qtyCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCashier, String>("qty"));
        TableColumn totalCol = new TableColumn("Total");
        totalCol.setMinWidth(200);
        totalCol.setCellValueFactory(
                new PropertyValueFactory<ModelTableCashier, String>("total"));
        cashierTable.setItems(oblist);
        cashierTable.getColumns().addAll(idCol, barcodeCol, prodCol, qtyCol, totalCol);

    }
}
