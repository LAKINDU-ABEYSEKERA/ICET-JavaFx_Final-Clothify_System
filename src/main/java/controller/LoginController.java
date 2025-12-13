package controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.io.IOException;

public class LoginController {

    @FXML
    private AnchorPane root;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPass;

    @FXML
    public void initialize() {

        // Safety check
        if (root == null) {
            System.out.println("ROOT IS NULL - fx:id missing!");
            return;
        }

        FadeTransition fade = new FadeTransition(Duration.seconds(1), root);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.8), root);
        scale.setFromX(0.9);
        scale.setFromY(0.9);
        scale.setToX(1);
        scale.setToY(1);

        fade.play();
        scale.play();
    }

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String email = txtEmail.getText();
        String password = txtPass.getText();

        if (authenticate(email, password)) {
            // 1. Show Success Message
            new Alert(Alert.AlertType.INFORMATION, "Login Successful!").showAndWait();

            // 2. NAVIGATE TO DASHBOARD
            try {
                // Get the current window (Stage)
                Stage stage = (Stage) txtEmail.getScene().getWindow();

                // Load the Dashboard View
                // MAKE SURE "/view/DashboardForm.fxml" matches your file name exactly!
                stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
                stage.setTitle("Clothify Store - Dashboard");
                stage.centerOnScreen();
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Could not load Dashboard. Check file path!").show();
            }

        } else {
            new Alert(Alert.AlertType.ERROR, "Invalid Email or Password").show();
        }
    }

    private boolean authenticate(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<User> query = session.createQuery("FROM User WHERE email = :e", User.class);
            query.setParameter("e", email);
            User user = query.uniqueResult();

            if (user != null && user.getPassword().equals(password)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}