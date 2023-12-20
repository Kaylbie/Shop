package com.kursinis.prif4kursinis.fxControllers.userWindowControllers;


import com.kursinis.prif4kursinis.StartGui;
import com.kursinis.prif4kursinis.fxControllers.createControllers.CreateProductController;
import com.kursinis.prif4kursinis.fxControllers.createControllers.EditUserWindowController;
import com.kursinis.prif4kursinis.fxControllers.windowControllers.ProductUpdateCallback;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.Cart;
import com.kursinis.prif4kursinis.model.CartItem;
import com.kursinis.prif4kursinis.model.Product;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;

public class UserProductNodeController implements Initializable {
    @FXML
    private Label productNameLabel;
    @FXML
    private Label productCodeLabel;
    @FXML
    private Label productPriceLabel;
    @FXML
    private ImageView productImageView;
    private ProductUpdateCallback updateCallback;
    private Product product;
    private EntityManagerFactory entityManagerFactory;
    private User currentUser;
    CustomHib customHib;
    public void initialize(URL url, ResourceBundle resourceBundle) {
        entityManagerFactory = StartGui.getEntityManagerFactory();
        customHib= new CustomHib(entityManagerFactory);
    }
    public void setUpdateCallback(ProductUpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }
    public void setProductData(Product product) {

        this.product=product;
        productNameLabel.setText(product.getTitle());
        productCodeLabel.setText(product.getCode()); // Replace getCode() with your actual method
        productPriceLabel.setText("Price: $" + product.getPrice()); // Replace getPrice() with your actual method
        String imagePath = "/com/kursinis/prif4kursinis/images/" + product.getPhotoName(); // Replace getImageName() with your actual method
        if (imagePath != null && !imagePath.isEmpty()) {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            productImageView.setImage(image);
        }
    }

    public void addToCart(ActionEvent actionEvent) {
        customHib.beginTransaction();
        try {
            Cart cart = findOrCreateActiveCartForUser(this.currentUser);

            // Proceed only if the cart is active
            if (cart != null && "Active".equals(cart.getStatus())) {
                Optional<CartItem> existingItem = cart.getItems().stream()
                        .filter(item -> item.getProduct().equals(product))
                        .findFirst();

                if (existingItem.isPresent()) {
                    // Increase quantity if the item already exists
                    CartItem item = existingItem.get();
                    item.setQuantity(item.getQuantity() + 1);
                    cart.setItemCount(cart.getItemCount()+1);
                } else {
                    // Add a new item if it doesn't exist
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setProduct(product);
                    newItem.setQuantity(1);
                    cart.setItemCount(cart.getItemCount()+1);
                    cart.getItems().add(newItem);
                }

                double newTotal = cart.getTotal() + product.getPrice();
                cart.setTotal(newTotal);

                customHib.update(cart);
                customHib.commitTransaction();
            } else {
                System.out.println("No active cart available.");
                customHib.rollbackTransaction();
            }
        } catch (Exception e) {
            customHib.rollbackTransaction();
            e.printStackTrace();
        }
    }

    private Cart findOrCreateActiveCartForUser(User user) {
        Cart activeCart = customHib.findActiveCartByUserId(user.getId());
        if (activeCart == null) {
            activeCart = new Cart();
            activeCart.setUser(user);
            activeCart.setDateCreated(LocalDate.now());
            activeCart.setStatus("Active");
            activeCart.setItems(new ArrayList<>());
            activeCart.setTotal(0.0);
            activeCart.setItemCount(0);
            customHib.create(activeCart);
        }
        return activeCart;
    }


    void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public void displayProductWindow(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("userWindow/displayCustomerProductWindow.fxml"));
            Parent root = fxmlLoader.load();

            DisplayCustomerProductWindowController controller = fxmlLoader.getController();
            controller.setProductData(product, currentUser);
            controller.setAdminButtonsVisible(false);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Product");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
