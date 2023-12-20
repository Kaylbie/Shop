package com.kursinis.prif4kursinis.fxControllers.windowControllers;

import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayOrderWindowController implements Initializable {

    private EntityManagerFactory entityManagerFactory;
    private Cart cart;
    private User currentUser;
    @FXML
    private Label orderStatusLabel;
    @FXML
    private Label orderTotalPriceLabel;
    @FXML
    private Label orderManagerLabel;
    @FXML
    private Label orderTotalQuantityLabel;
    @FXML
    private ListView cartContentsListView;
    @FXML
    private ListView messageListView;
    @FXML
    private Label customerNameLabel;
    @FXML
    private Label customerAddressLabel;
    @FXML
    private Label customerCardNoLabel;
    @FXML
    private TextField messageTextField;
    private CustomHib customHib;
    private Map<String, Product> productMap = new HashMap<>();

    @Override
    public void initialize(java.net.URL arg0, java.util.ResourceBundle arg1) {
        entityManagerFactory= com.kursinis.prif4kursinis.StartGui.getEntityManagerFactory();
        customHib=new CustomHib(entityManagerFactory);
    }

    public void setProductData(Cart cart, User currentUser) {
        this.cart=cart;
        this.currentUser=currentUser;
        setViewed();
        loadCartData();
    }

    private void setViewed() {
        if(currentUser instanceof Manager){
            cart.setAttentionRequired(false);
            customHib.update(cart);
        }
    }

    private void loadCartItems() {
        Cart activeCart=customHib.findCartById(this.cart.getId());
        if (activeCart != null) {
            productMap.clear();
            for (CartItem cartItem : activeCart.getItems()) {
                Product product = cartItem.getProduct();
                String productDisplay = product.getTitle() + " - $" + product.getPrice() + " x " + cartItem.getQuantity();
                productMap.put(productDisplay, product);
                cartContentsListView.getItems().add(productDisplay);
            }
        }
    }
    private void loadCartData() {
        orderStatusLabel.setText("Status: " + cart.getStatus());
        orderTotalPriceLabel.setText("Total Price: $" + cart.getTotal());
        String managerName = cart.getResponsibleManager() != null ? cart.getResponsibleManager().getName() : "N/A";
        orderManagerLabel.setText("Accepted by: " + managerName);
        orderTotalQuantityLabel.setText("Total Quantity: " + cart.getItemCount());

        Customer customer = (Customer) cart.getUser();
        if (customer != null) {
            customerNameLabel.setText("Customer: " + customer.getName()+" "+customer.getSurname());
            customerAddressLabel.setText("Address: " + customer.getAddress());
            customerCardNoLabel.setText("Card No: " + customer.getCardNo());
        } else {
            customerNameLabel.setText("Customer: N/A");
            customerAddressLabel.setText("Address: N/A");
            customerCardNoLabel.setText("Card No: N/A");
        }
        loadCartItems();

        loadMessagesForCart();
    }

    private void loadMessagesForCart() {
        List<Message> messages = customHib.getMessagesForCart(cart.getId());
        messageListView.getItems().clear();
        for (Message message : messages) {
            String displayText = message.getDateCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) +
                    ": " + message.getSender().getName() + " - " + message.getMessageBody();
            messageListView.getItems().add(displayText);
        }
    }


    public void sendMessage(ActionEvent actionEvent) {
        String messageContent = messageTextField.getText();
        if (messageContent == null || messageContent.trim().isEmpty()) {
            showAlert("No Message", "Please type a message before sending.");
            return;
        }
        try {
            EntityManager em = entityManagerFactory.createEntityManager();
            em.getTransaction().begin();

            Message message = new Message();
            message.setMessageBody(messageContent);
            message.setDateCreated(LocalDateTime.now());
            message.setCart(cart);
            message.setSender(currentUser);

            em.persist(message);
            em.getTransaction().commit();
            em.close();
            showAlert("Sent", "Message sent.");
            if(currentUser instanceof Customer){
                cart.setAttentionRequired(true);
                customHib.update(cart);
            }
            messageTextField.clear();
            loadMessagesForCart();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Could not send the message.");
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
