package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class tabPaneController implements Initializable {

    @FXML
    Tab tab1;

    @FXML
    Tab tab2;

    @FXML
    Tab tab3;

    @FXML
    Tab tab4;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            AnchorPane anch1 = FXMLLoader.load(getClass().getResource("Cashier.fxml"));
            tab1.setContent(anch1);
        } catch (IOException ex) {
            System.out.println("File not found!");
        }

        try {
            AnchorPane anch2 = FXMLLoader.load(getClass().getResource("Product.fxml"));
            tab2.setContent(anch2);
        } catch (IOException ex) {
            System.out.println("File not found!");
        }

        try {

            AnchorPane anch3 = FXMLLoader.load(getClass().getResource("Brand.fxml"));
            tab3.setContent(anch3);
        } catch (IOException ex) {
            System.out.println("File not found!");
        }

        try {

            AnchorPane anch4 = FXMLLoader.load(getClass().getResource("Category.fxml"));
            tab4.setContent(anch4);
        } catch (IOException ex) {
            System.out.println("File not found!");
        }
    }
}
