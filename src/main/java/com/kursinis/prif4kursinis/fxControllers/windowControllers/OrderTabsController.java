package com.kursinis.prif4kursinis.fxControllers.windowControllers;

import com.kursinis.prif4kursinis.StartGui;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.Cart;
import com.kursinis.prif4kursinis.model.Manager;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.SVGPath;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class OrderTabsController implements Initializable {
    private EntityManagerFactory entityManagerFactory;
    @FXML
    private Button openButton;
    @FXML private Label orderNumberLabel;
    @FXML private Label orderTotalPriceLabel;
    @FXML private Label dateCreatedLabel;
    @FXML private Label acceptedByManagerLabel;
    @FXML private Label orderStatusLabel;
    private User currentUser;
    @FXML
    private Button cancelButton;
    @FXML private Button closeButton;
    @FXML private SVGPath attentionRequiredStar;
    private Cart cart;
    private CustomHib customHib;

    @Override
    public void initialize(java.net.URL location, java.util.ResourceBundle resources) {
        entityManagerFactory= com.kursinis.prif4kursinis.StartGui.getEntityManagerFactory();
        customHib = new CustomHib(entityManagerFactory);
    }
    public void setCartData(Cart cart, User currentUser) {
        this.currentUser=currentUser;
        this.cart=cart;
        loadOrderData();
    }
    private void loadOrderData(){
        orderNumberLabel.setText("Order Nr: "+String.valueOf(cart.getId()));
        orderTotalPriceLabel.setText("Total price: $"+String.valueOf(cart.getTotal()));
        dateCreatedLabel.setText("Date created: "+cart.getDateCreated().toString());
        String managerName = cart.getResponsibleManager() != null ? cart.getResponsibleManager().getName() : "N/A";
        acceptedByManagerLabel.setText("Accepted by: "+managerName);
        orderStatusLabel.setText("Status: "+cart.getStatus());
        if(cart.getStatus().equals("Open")){
            orderOpened();
            orderStatusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14;");

        }
        else if(cart.getStatus().equals("Cancelled")){
            orderCancelled();
            orderStatusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 14;");
        }
        else if(cart.getStatus().equals("Closed")){
            orderClosed();
            orderStatusLabel.setStyle("-fx-text-fill: grey; -fx-font-size: 14;");
        }
        else if(cart.getStatus().equals("Pending")){
            orderPending();
            orderStatusLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 14;");
        }
        if(cart.isAttentionRequired()){
            attentionRequired(true);
        }
        else{
            attentionRequired(false);
        }
    }

    private void orderPending() {
        openButton.setVisible(true);
        closeButton.setVisible(false);
        cancelButton.setVisible(true);
    }

    private void orderClosed() {
        openButton.setVisible(false);
        closeButton.setVisible(false);
        cancelButton.setVisible(false);
    }

    private void orderCancelled() {
        openButton.setVisible(false);
        closeButton.setVisible(false);
        cancelButton.setVisible(false);
    }

    public void attentionRequired(Boolean required){
        attentionRequiredStar.setVisible(required);
    }
    private void orderOpened(){
        openButton.setVisible(false);
        closeButton.setVisible(true);
        closeButton.setVisible(true);
    }
    public void openOrderWindow(MouseEvent mouseEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("userWindow/displayOrderWindow.fxml"));
            Parent root = fxmlLoader.load();

            DisplayOrderWindowController controller = fxmlLoader.getController();
            controller.setProductData(cart, currentUser);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Order Nr. "+ cart.getId());
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleOpenActionButton(ActionEvent actionEvent) {
        cart.setResponsibleManager((currentUser instanceof Manager) ? (Manager) currentUser : null);
        cart.setStatus("Open");
        cart.setAttentionRequired(false);
        customHib.update(cart);
        loadOrderData();
    }

    public void handleCloseActionButton(ActionEvent actionEvent) {
        cart.setStatus("Closed");
        customHib.update(cart);
        loadOrderData();
    }

    public void handleCancelActionButton(ActionEvent actionEvent) {
        cart.setStatus("Cancelled");
        customHib.update(cart);
        loadOrderData();
    }
}
