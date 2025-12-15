package controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class DashboardController {

    @FXML
    private AnchorPane root;

    @FXML
    public void initialize() {

        FadeTransition fade = new FadeTransition(Duration.seconds(0.8), root);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(0.6), root);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1);
        scale.setToY(1);

        fade.play();
        scale.play();
    }

    @FXML
    void btnProductsOnAction(ActionEvent event) {
        try {

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();


            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/ProductForm.fxml"))));
            stage.setTitle("Product Management");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Cannot load Product Screen!").show();
        }
    }


    @FXML
    void btnOrderOnAction(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/OrderForm.fxml"))));
            stage.setTitle("POS System");
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}