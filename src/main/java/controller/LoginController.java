package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import util.HibernateUtil;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPass;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String email = txtEmail.getText();
        String password = txtPass.getText();

        if (authenticate(email, password)) {
            new Alert(Alert.AlertType.INFORMATION, "Login Successful!").showAndWait();
            // We will add the Dashboard navigation here in the next step
        } else {
            new Alert(Alert.AlertType.ERROR, "Invalid Email or Password").show();
        }
    }

    private boolean authenticate(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Check DB for user with matching email
            Query<User> query = session.createQuery("FROM User WHERE email = :e", User.class);
            query.setParameter("e", email);
            User user = query.uniqueResult();

            // Check if password matches
            if (user != null && user.getPassword().equals(password)) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}