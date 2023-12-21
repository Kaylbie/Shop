package com.kursinis.prif4kursinis.fxControllers.regLog;
import com.kursinis.prif4kursinis.fxControllers.JavaFxCustomUtils;
import org.mindrot.jbcrypt.BCrypt;

import com.kursinis.prif4kursinis.StartGui;
import com.kursinis.prif4kursinis.hibernateControllers.UserHib;
import com.kursinis.prif4kursinis.model.Customer;
import com.kursinis.prif4kursinis.model.Manager;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationController implements Initializable {
    @FXML
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public PasswordField repeatPasswordField;
    @FXML
    public TextField nameField;
    @FXML
    public TextField surnameField;
    @FXML
    public TextField addressField;
    @FXML
    public TextField cardNumberField;

    private EntityManagerFactory entityManagerFactory;
    private UserHib userHib;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entityManagerFactory = StartGui.getEntityManagerFactory();
    }
    public void createUser() {
        userHib = new UserHib(entityManagerFactory);

        if (loginField.getText().isEmpty() || passwordField.getText().isEmpty() ||
                nameField.getText().isEmpty() || surnameField.getText().isEmpty() ||
                addressField.getText().isEmpty() || cardNumberField.getText().isEmpty()) {

            showAlert("Registration Error", "All fields are required. Please fill in all fields to register.");
            return;
        }

        // Check if login name already exists
        if (userHib.getUserByLogin(loginField.getText()) != null) {
            showAlert("Registration Error", "Login name already exists. Please choose a different login name.");
            return;
        }

        // Check name and surname for invalid characters (only letters allowed)
        if (!nameField.getText().matches("[a-zA-Z]+") || !surnameField.getText().matches("[a-zA-Z]+")) {
            showAlert("Registration Error", "Name and surname should only contain letters.");
            return;
        }

        // Check address format (allowing letters, numbers and spaces)
        if (!addressField.getText().matches("[a-zA-Z0-9 ]+")) {
            showAlert("Registration Error", "Address can only contain letters, numbers, and spaces.");
            return;
        }

        // Check card number format (only numbers allowed)
        if (!cardNumberField.getText().matches("\\d+")) {
            showAlert("Registration Error", "Card number should only contain numbers.");
            return;
        }

        // Proceed with hashing the password and creating the user
        String hashedPassword = BCrypt.hashpw(passwordField.getText(), BCrypt.gensalt());

        User user = new Customer(
                loginField.getText(),
                hashedPassword,
                nameField.getText(),
                surnameField.getText(),
                addressField.getText(),
                cardNumberField.getText()
        );

        userHib.createUser(user);
        goBackToLogin();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private void goBackToLogin() {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("login.fxml"));
        try{
            Parent parent = fxmlLoader.load();
            Scene scene = new Scene(parent);
            Stage stage = (Stage) loginField.getScene().getWindow();
            stage.setTitle("Shop");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e){
            e.printStackTrace();
        }

    }
    public void returnToLogin(ActionEvent actionEvent) {
        goBackToLogin();
    }
}
