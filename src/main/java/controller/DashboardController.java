package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {

    // Logic for "Manage Products" button
    @FXML
    void btnProductsOnAction(ActionEvent event) {
        try {
            // 1. Get the current window
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            // 2. Load the Product Form
            stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/ProductForm.fxml"))));
            stage.setTitle("Product Management");
            stage.centerOnScreen();

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Cannot load Product Screen!").show();
        }
    }

    // Logic for "Place Order" button
    @FXML
    void btnOrderOnAction(ActionEvent event) {
        // We will enable this button once we build the Order Screen
        new Alert(Alert.AlertType.INFORMATION, "Order Screen Coming Soon!").show();
    }
}