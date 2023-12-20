package com.kursinis.prif4kursinis.fxControllers.windowControllers;

import com.kursinis.prif4kursinis.StartGui;
import com.kursinis.prif4kursinis.fxControllers.createControllers.CreateProductController;
import com.kursinis.prif4kursinis.fxControllers.userWindowControllers.DisplayCustomerProductWindowController;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ProductNodeController implements Initializable {
    @FXML
    private Label productNameLabel;
    @FXML
    private Label productCodeLabel;
    @FXML
    private Label productPriceLabel;
    @FXML
    private ImageView productImageView;
    @FXML
    private Button deleteButton;
    @FXML
    private Button visibilityButton;
    private ProductUpdateCallback updateCallback;
    private Product product;
    private EntityManagerFactory entityManagerFactory;
    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        entityManagerFactory = StartGui.getEntityManagerFactory();
    }
    public void setUpdateCallback(ProductUpdateCallback updateCallback) {
        this.updateCallback = updateCallback;
    }
    public void setProductData(Product product, User currentUser) {

        this.product=product;
        this.currentUser= currentUser;
        productNameLabel.setText(product.getTitle());
        productCodeLabel.setText(product.getCode());
        productPriceLabel.setText("Price: $" + product.getPrice());
        String imagePath = "/com/kursinis/prif4kursinis/images/" + product.getPhotoName();
        if (imagePath != null && !imagePath.isEmpty()) {
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            productImageView.setImage(image);
        }
        if(product.isVisible()){
            visibilityButton.setText("Visible");
        }
        else{
            visibilityButton.setText("Invisible");
        }
    }

    public void handleDeleteAction(ActionEvent actionEvent) {
        CustomHib customHib = new CustomHib(StartGui.getEntityManagerFactory());
        customHib.delete(Product.class, product.getId());
        if (updateCallback != null) {
            updateCallback.onProductUpdated();
        }
    }
    public void handleEditAction(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("create/createProduct.fxml"));
            Parent root = fxmlLoader.load();

            CreateProductController createProductController = fxmlLoader.getController();
            createProductController.setData(entityManagerFactory, product);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Edit Product");
            Scene scene = new Scene(root);
            stage.setScene(scene);

            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handleVisibilityAction(ActionEvent actionEvent) {
        CustomHib customHib = new CustomHib(StartGui.getEntityManagerFactory());
        if(product.isVisible()){
            product.setVisible(false);
            visibilityButton.setText("Invisible");
            customHib.update(product);
        }
        else{
            product.setVisible(true);
            visibilityButton.setText("Visible");
            customHib.update(product);
        }
    }


    public void handleProductView(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("userWindow/displayCustomerProductWindow.fxml"));
            Parent root = fxmlLoader.load();

            DisplayCustomerProductWindowController controller = fxmlLoader.getController();
            controller.setProductData(product, currentUser);
            controller.setAdminButtonsVisible(true);
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
