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

// Login class which extends Connection class
public class LoginController extends Connection {

    public TextField usernameField;
    public PasswordField passwordField;
    public Button loginButton;
    public Label warningLabel;


    // Function that "loginButton" execute
    public void loginButton() {

        // Check if user input a correct credential
        if (usernameField.getText().equals(getUsername()) && passwordField.getText().equals(getPassword())) {
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

            // If not correct
            // It will show a message to the user
        } else {
            warningLabel.setVisible(true);
        }
    }
}