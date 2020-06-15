package sample;

import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ReceiptController implements Initializable {
    public TextArea receiptText;
    String temp = "";
    Connection connect = new Connection();

    public void showTable() {
        try {
            PreparedStatement prepStat = connect.getPrepStat("SELECT * FROM cashier");
            ResultSet rs = prepStat.executeQuery();
            while (rs.next()) {
                temp += rs.getString("ID") + ". " + rs.getString("Product") + "\n" + "    Qty: " + rs.getString("Quantity") +
                        " ..................................................................... " + rs.getString("Total") + "\n";
            }
            receiptText.setText(
                    "-------------- WELCOME TO BINBIN STORE --------------\n" +
                            "                                 YOUR RECEIPT\n" + temp +
                            "\n##########################################" +
                            "\n----------------------- THANK YOU -----------------------"
            );

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        showTable();
    }
}
