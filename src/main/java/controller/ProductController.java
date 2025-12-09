package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.entity.Product;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductController implements Initializable {

    @FXML private TextField txtId;
    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private TextField txtStock;

    @FXML private TableView<Product> tblProducts;
    @FXML private TableColumn<Product, String> colId;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Double> colPrice;
    @FXML private TableColumn<Product, Integer> colStock;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Link Table Columns to Product Class attributes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // 2. Load Data
        loadTable();

        // 3. Add listener to fill text fields when row clicked
        tblProducts.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtId.setText(newSelection.getId());
                txtName.setText(newSelection.getName());
                txtPrice.setText(String.valueOf(newSelection.getPrice()));
                txtStock.setText(String.valueOf(newSelection.getStock()));
            }
        });
    }

    private void loadTable() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Product> list = session.createQuery("FROM Product", Product.class).list();
            ObservableList<Product> observableList = FXCollections.observableArrayList(list);
            tblProducts.setItems(observableList);
        }
    }

    @FXML
    void btnAddOnAction(ActionEvent event) {
        Product product = new Product(
                txtId.getText(),
                txtName.getText(),
                "General", // Default Category
                "M",       // Default Size (Speed Run shortcut)
                Double.parseDouble(txtPrice.getText()),
                Integer.parseInt(txtStock.getText()),
                null       // No Supplier for now
        );

        executeTransaction(session -> session.persist(product));
        loadTable();
        clearFields();
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Product product = new Product(
                txtId.getText(),
                txtName.getText(),
                "General",
                "M",
                Double.parseDouble(txtPrice.getText()),
                Integer.parseInt(txtStock.getText()),
                null
        );

        executeTransaction(session -> session.merge(product));
        loadTable();
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        Product selected = tblProducts.getSelectionModel().getSelectedItem();
        if (selected != null) {
            executeTransaction(session -> session.remove(selected));
            loadTable();
            clearFields();
        }
    }

    // Helper to run Hibernate code safely
    private void executeTransaction(java.util.function.Consumer<Session> action) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            action.accept(session);
            tx.commit();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Database Error: " + e.getMessage()).show();
        }
    }

    private void clearFields() {
        txtId.clear(); txtName.clear(); txtPrice.clear(); txtStock.clear();
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        Stage stage = (Stage) txtId.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
    }
}