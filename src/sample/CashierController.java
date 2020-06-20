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


// CashierController class which implements interface Initializable
public class CashierController implements Initializable {

    Connection connect = new Connection();

    public TextField barcodeField, prodField, priceField, qtyField, totalField, changeField, payField;
    public Button addButton, delButton;

    ObservableList<ModelTableCashier> oblist = FXCollections.observableArrayList();

    public int ID;
    public long subTotal, change;


    // CashierController class associated with ModelTableCashier class
    @FXML
    private TableView<ModelTableCashier> cashierTable;


    // Function that return value of barcodeField textfield
    public String getBarcodeField() {
        return barcodeField.getText();
    }

    // Function that return value of prodField textfield
    public String getProdField() {
        return prodField.getText();
    }

    // Function that return value of priceField textfield
    public String getPriceField() {
        return priceField.getText();
    }

    // Function that return value of qtyField textfield
    public String getQtyField() {
        return qtyField.getText();
    }

    // Function that return value of payField textfield
    public String getPayField() {
        return payField.getText();
    }


    // Function that search the product from database
    // If product barcode not found, it will show a pop up message
    public void productSearch() {
        PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM product WHERE Barcode='" + getBarcodeField() + "'");
        try {
            ResultSet rs = prepStat.executeQuery();
            if (!rs.next()) {

                // Alert show product not found
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("ERROR");
                alert1.setContentText("Barcode Not Found!");
                alert1.setHeaderText("SOMETHING WRONG");
                alert1.show();
            } else {
                String productName = rs.getString("Product");
                String price = rs.getString("RetailPrice");

                // Set prodField and priceField value from database
                prodField.setText(productName.trim());
                priceField.setText(price.trim());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Function that calculate subTotal
    public void calculateSubTotal() {
        subTotal = 0;
        PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM cashier");
        try {
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {

                // Get data from database
                // Add it to subTotal until all product is summed
                subTotal += rs.getInt("Total");
            }

            // Set the totalField value to subTotal value
            totalField.setText(String.valueOf(subTotal));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // PayInvoiceButton function
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

            // If user input payment is smaller than total price amount
            // It will show pop up message
            Alert alert1 = new Alert(Alert.AlertType.ERROR);
            alert1.setTitle("ERROR");
            alert1.setContentText("Pay Amount cannot smaller than total");
            alert1.setHeaderText("Pay Amount Error!");
            alert1.show();
        }
    }


    // Function that call and show receipt window
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


    // Function to calculate change
    public void calculateChange() {
        change = 0;
        change = Integer.parseInt(getPayField()) - Integer.parseInt(totalField.getText());

        // Set changeField value to change value
        changeField.setText(String.valueOf(change));
    }


    // Function that update and check the value in inventory system database
    public void updateCheckQty() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM product WHERE Barcode='" + getBarcodeField() + "'");
            PreparedStatement prepStat2 = connect.getPrepStat("UPDATE product SET Qty = ? WHERE Barcode = '" + getBarcodeField() + "'");
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                int availableQty = rs.getInt("Qty");
                // If user request qty more than available qty
                // It will show pop up message
                if (Integer.parseInt(getQtyField()) > availableQty) {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("ERROR");
                    alert1.setContentText("Available Quantity = " + availableQty);
                    alert1.setHeaderText("QUANTITY IS NOT ENOUGH!");
                    alert1.show();
                } else {
                    // Subtract the value of qty in inventory system by requested qty
                    prepStat2.setString(1, String.valueOf(availableQty - Integer.parseInt(getQtyField())));
                    prepStat2.executeUpdate();
                    addToDB();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Function that add new "product" to cashier table in database
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


    // Function addButton
    public void addButton() {

        // Validate if user already input all the necesary data
        if (getBarcodeField().isEmpty() || getQtyField().isEmpty()) {

            // Show pop up message
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


    // Function that remove all textfield value
    public void clearField() {
        barcodeField.setText("");
        prodField.setText("");
        priceField.setText("");
        qtyField.setText("");
    }


    // Function that clear database table
    public void clearDB() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("TRUNCATE cashier");
            prepStat.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Function that fill cashierTable
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


    // Function that set value of textfield if user select one of the item inside the table
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


    // Function deleteButton
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


    // If user remove "product" from "cart"
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


    // Initalize Function
    @Override
    public void initialize(URL location, ResourceBundle resource) {
        calculateSubTotal();
        showTable();

        // Create necessary table column
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
