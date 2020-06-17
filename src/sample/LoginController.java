package sample;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    public TextField usernameField;
    public PasswordField passwordField;
    public Button loginButton;
    public Label warningLabel;


    public void loginButton() {

        if (usernameField.getText().equals("admin") && passwordField.getText().equals("Admin")) {
            Parent root = null;
            try {
                root = FXMLLoader.load(getClass().getResource("tabPane.fxml"));
            } catch (IOException e) {
                System.out.println("File not found!");
            }
            assert root != null;
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.show();
            Stage closeWindow = (Stage) loginButton.getScene().getWindow();
            closeWindow.close();
            warningLabel.setVisible(false);
        } else {
            warningLabel.setVisible(true);
        }
    }
}