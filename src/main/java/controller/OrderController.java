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
import model.CartItem;
import model.entity.Order;
import model.entity.OrderDetail;
import model.entity.Product;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;

public class OrderController implements Initializable {

    // LEFT SIDE: Product List
    @FXML private TableView<Product> tblProducts;
    @FXML private TableColumn<Product, String> colProdId;
    @FXML private TableColumn<Product, String> colProdName;
    @FXML private TableColumn<Product, Double> colProdPrice;
    @FXML private TableColumn<Product, Integer> colProdStock;

    // RIGHT SIDE: Cart
    @FXML private TableView<CartItem> tblCart;
    @FXML private TableColumn<CartItem, String> colCartProdId;
    @FXML private TableColumn<CartItem, String> colCartName;
    @FXML private TableColumn<CartItem, Integer> colCartQty;
    @FXML private TableColumn<CartItem, Double> colCartTotal;

    @FXML private TextField txtQty;
    @FXML private Label lblNetTotal;

    // Data Storage
    private ObservableList<CartItem> cartList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 1. Setup Columns (Left)
        colProdId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colProdName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colProdPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colProdStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // 2. Setup Columns (Right)
        colCartProdId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colCartName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCartQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colCartTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tblCart.setItems(cartList);

        // 3. Load Products from DB
        loadProducts();
    }

    private void loadProducts() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Product> list = session.createQuery("FROM Product", Product.class).list();
            tblProducts.setItems(FXCollections.observableArrayList(list));
        }
    }

    @FXML
    void btnAddToCartOnAction(ActionEvent event) {
        Product selectedProduct = tblProducts.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a product first!").show();
            return;
        }

        int qty = Integer.parseInt(txtQty.getText());
        if (qty > selectedProduct.getStock()) {
            new Alert(Alert.AlertType.WARNING, "Not enough stock!").show();
            return;
        }

        // Add to Cart List
        double total = qty * selectedProduct.getPrice();
        CartItem item = new CartItem(
                selectedProduct.getId(),
                selectedProduct.getName(),
                qty,
                selectedProduct.getPrice(),
                total
        );

        cartList.add(item);
        calculateNetTotal();
    }

    private void calculateNetTotal() {
        double netTotal = 0;
        for (CartItem item : cartList) {
            netTotal += item.getTotal();
        }
        lblNetTotal.setText(String.valueOf(netTotal));
    }

    @FXML
    void btnPlaceOrderOnAction(ActionEvent event) {
        String orderId = UUID.randomUUID().toString().substring(0, 8); // Generate random Order ID

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            // 1. Save Order
            Order order = new Order();
            order.setOrderId(orderId);
            order.setDate(LocalDate.now());
            order.setTotal(Double.parseDouble(lblNetTotal.getText()));
            session.persist(order);

            // 2. Save Details & Update Stock
            for (CartItem item : cartList) {
                // Save Order Detail
                OrderDetail detail = new OrderDetail();
                detail.setOrderId(orderId);
                detail.setProductId(item.getProductId());
                detail.setQty(item.getQty());
                detail.setUnitPrice(item.getUnitPrice());
                session.persist(detail);

                // Update Stock
                Product product = session.get(Product.class, item.getProductId());
                product.setStock(product.getStock() - item.getQty());
                session.merge(product);
            }

            tx.commit();
            new Alert(Alert.AlertType.INFORMATION, "Order Placed Successfully!").show();
            cartList.clear();
            loadProducts(); // Refresh stock table
            lblNetTotal.setText("0.00");

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Order Failed: " + e.getMessage()).show();
        }
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        Stage stage = (Stage) tblProducts.getScene().getWindow();
        stage.setScene(new Scene(FXMLLoader.load(getClass().getResource("/view/DashboardForm.fxml"))));
    }
}