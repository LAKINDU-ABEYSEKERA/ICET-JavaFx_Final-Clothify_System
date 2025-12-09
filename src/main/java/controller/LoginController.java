package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import model.entity.User;
import org.hibernate.Session;
import org.hibernate.query.Query;
import util.HibernateUtil;

public class LoginController {

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPassword;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        if (authenticate(email, password)) {
            new Alert(Alert.AlertType.INFORMATION, "Login Successful!").show();

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