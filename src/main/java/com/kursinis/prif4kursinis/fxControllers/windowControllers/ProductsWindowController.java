package com.kursinis.prif4kursinis.fxControllers.windowControllers;
import com.kursinis.prif4kursinis.StartGui;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.hibernateControllers.GenericHib;
import com.kursinis.prif4kursinis.model.Product;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProductsWindowController implements Initializable, ProductUpdateCallback, ProductSearchListener {

    @FXML
    public ScrollPane productsScrollPane;
    @FXML
    public VBox productsVBox;
    private User currentUser;

    private EntityManagerFactory entityManagerFactory;
    private CustomHib customHib;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entityManagerFactory = StartGui.getEntityManagerFactory();
    }
    @Override
    public void onProductUpdated() {
        refreshNodes();
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
        refreshNodes(null);
    }
    public void updateProductTabs(MouseEvent mouseEvent) {
        refreshNodes(null);
    }

    @Override
    public void onSearchQueryChanged(String query) {
        refreshNodes(query);
    }
    @Override
    public void onVisibilityChanged(String visibility) {
        refreshNodesWithVisibility(visibility);
    }
    private void refreshNodesWithVisibility(String visibility) {
        productsVBox.getChildren().clear();
        GenericHib productHib = new GenericHib(entityManagerFactory);
        List<Product> productList = productHib.getAllRecords(Product.class);

        for (Product product : productList) {
            if (visibility.equals("All") ||
                    (visibility.equals("Visible") && product.isVisible()) ||
                    (visibility.equals("Invisible") && !product.isVisible())) {
                try {
                    FXMLLoader loader = new FXMLLoader(StartGui.class.getResource("nodes/productNode.fxml"));
                    Node node = loader.load();
                    ProductNodeController controller = loader.getController();
                    controller.setProductData(product, currentUser);
                    controller.setUpdateCallback(this);
                    productsVBox.getChildren().add(node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void refreshNodes(String query) {
        productsVBox.getChildren().clear();
        GenericHib productHib = new GenericHib(entityManagerFactory);
        List<Product> productList = productHib.getAllRecords(Product.class);

        for (Product product : productList) {
            if (query == null || product.getTitle().toLowerCase().contains(query.toLowerCase())) {
                try {
                    FXMLLoader loader = new FXMLLoader(StartGui.class.getResource("nodes/productNode.fxml"));
                    Node node = loader.load();
                    ProductNodeController controller = loader.getController();
                    controller.setProductData(product, currentUser);
                    controller.setUpdateCallback(this);
                    productsVBox.getChildren().add(node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void refreshNodes() {
        refreshNodes(null);
    }
}
