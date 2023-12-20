package com.kursinis.prif4kursinis.fxControllers.userWindowControllers;

import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.Cart;
import com.kursinis.prif4kursinis.model.CartItem;
import com.kursinis.prif4kursinis.model.Product;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class UserCartWindowController implements Initializable {
    @FXML
    private ListView<String> cartItemsListView;
    @FXML private Button checkoutButton;
    @FXML private Button removeItemButton;
    @FXML private Label totalPriceLabel;
    private Map<String, Product> productMap = new HashMap<>();
    private User currentUser;
    private CustomHib customHib;
    private EntityManagerFactory entityManagerFactory;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entityManagerFactory= com.kursinis.prif4kursinis.StartGui.getEntityManagerFactory();
        customHib = new CustomHib(entityManagerFactory);
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        loadCartItems();
    }
    private void loadCartItems() {
        Cart activeCart = customHib.findActiveCartByUserId(currentUser.getId());
        if (activeCart != null) {
            productMap.clear();
            for (CartItem cartItem : activeCart.getItems()) {
                Product product = cartItem.getProduct();
                String productDisplay = product.getTitle() + " - $" + product.getPrice() + " x " + cartItem.getQuantity();
                productMap.put(productDisplay, product);
                cartItemsListView.getItems().add(productDisplay);
            }
            updateTotalPrice(activeCart.getTotal());
        }
    }


    private void updateTotalPrice(double total) {
        totalPriceLabel.setText("Total: $" + total);
    }
    public void onRemoveItem(ActionEvent actionEvent) {
        String selectedProductDisplay = cartItemsListView.getSelectionModel().getSelectedItem();
        if (selectedProductDisplay == null) {
            showAlert("No product selected", "Please select a product to remove.");
            return;
        }
        Product selectedProduct = decodeStringToProduct(selectedProductDisplay);
        Cart activeCart = findActiveCartForUser(this.currentUser);
        if (activeCart != null && selectedProduct != null) {
            CartItem selectedCartItem = activeCart.getItems().stream()
                    .filter(cartItem -> cartItem.getProduct().equals(selectedProduct))
                    .findFirst()
                    .orElse(null);

            if (selectedCartItem == null) {
                System.out.println("Selected CartItem is null");
                return;
            }

            double priceReduction = selectedProduct.getPrice() * selectedCartItem.getQuantity();
            double newTotal = Math.max(activeCart.getTotal() - priceReduction, 0);
            activeCart.setTotal(newTotal);
            activeCart.setItemCount(Math.max(activeCart.getItemCount() - selectedCartItem.getQuantity(), 0));

            customHib.delete(CartItem.class, selectedCartItem.getId());
            activeCart.getItems().remove(selectedCartItem);
            customHib.update(activeCart);

            cartItemsListView.getItems().remove(selectedProductDisplay);
            totalPriceLabel.setText("Total: $" + activeCart.getTotal());
        } else {
            System.out.println("Active cart or selected product is null");
        }
    }
    private Cart findActiveCartForUser(User user) {
        return customHib.findActiveCartByUserId(user.getId());
    }


    private Product decodeStringToProduct(String productDisplay) {
        String productName = productDisplay.split(" - $")[0];
        return productMap.getOrDefault(productName, null);
    }
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void checkoutButton(ActionEvent actionEvent) {
        Cart activeCart = customHib.findActiveCartByUserId(currentUser.getId());
        if (activeCart == null && activeCart.getItems().isEmpty()) {
            showAlert("Checkout Failed", "No active cart found.");
            return;
        }
        activeCart.setStatus("Pending");

        try {
            customHib.update(activeCart);
            showAlert("Checkout Successful", "Your order has been placed successfully.");
        } catch (Exception e) {
            showAlert("Checkout Failed", "An error occurred during checkout.");
            e.printStackTrace(); // Log the exception for debugging purposes
        }
        loadCartItems();
    }
}
