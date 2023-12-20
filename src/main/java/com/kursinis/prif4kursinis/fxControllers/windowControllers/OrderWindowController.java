package com.kursinis.prif4kursinis.fxControllers.windowControllers;

import com.kursinis.prif4kursinis.StartGui;
import com.kursinis.prif4kursinis.fxControllers.MainWindowController;
import com.kursinis.prif4kursinis.hibernateControllers.GenericHib;
import com.kursinis.prif4kursinis.model.Cart;
import com.kursinis.prif4kursinis.model.Product;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class OrderWindowController implements Initializable  {



    @FXML
    public VBox ordersVbox;
    @FXML
    public ScrollPane ordersScrollPane;
    @FXML
    private Label pendingCountLabel;
    @FXML
    private Label openCountLabel;
    @FXML
    private Label closedCountLabel;
    private User currentUser;
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        entityManagerFactory = StartGui.getEntityManagerFactory();
    }
    public void setOrdersData(User currentUser) {
        this.currentUser = currentUser;
        refreshCartNodes(null, null, null, null);
        setStatistics();
    }
    private void setStatistics(){
        GenericHib cartHib = new GenericHib(entityManagerFactory);
        List<Cart> cartList = cartHib.getAllRecords(Cart.class);
        CartStatistics statistics = new CartStatistics(cartList);
        pendingCountLabel.setText(String.valueOf(statistics.getPendingCount()));
        openCountLabel.setText(String.valueOf(statistics.getOpenCount()));
        closedCountLabel.setText(String.valueOf(statistics.getClosedCount()));
    }
    public void refreshCartNodes(String query, String status, LocalDate startDate, LocalDate endDate) {
        ordersVbox.getChildren().clear();
        GenericHib cartHib = new GenericHib(entityManagerFactory);
        List<Cart> cartList = cartHib.getAllRecords(Cart.class);
        sortOrdersWithAttention(cartList);
        for (Cart cart : cartList) {
            if ((query == null || isCartMatchingQuery(cart, query)) &&
                    (status == null || isCartMatchingStatus(cart, status)) &&
                    isCartMatchingDateRange(cart, startDate, endDate)) {
                try {
                    FXMLLoader loader = new FXMLLoader(StartGui.class.getResource("nodes/orderTabs.fxml"));
                    Node node = loader.load();
                    OrderTabsController controller = loader.getController();
                    controller.setCartData(cart, currentUser);
                    ordersVbox.getChildren().add(node);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean isCartMatchingDateRange(Cart cart, LocalDate startDate, LocalDate endDate) {
        LocalDate cartDate = cart.getDateCreated();
        if (startDate == null && endDate == null) {
            return true; // No date filter applied
        }
        if (startDate != null && cartDate.isBefore(startDate)) {
            return false; // Cart date is before the start date
        }
        if (endDate != null && cartDate.isAfter(endDate)) {
            return false; // Cart date is after the end date
        }
        return true;
    }
    private boolean isCartMatchingQuery(Cart cart, String query) {
        return String.valueOf(cart.getId()).startsWith(query);
    }

    private boolean isCartMatchingStatus(Cart cart, String status) {
        if ("All".equalsIgnoreCase(status)) {
            return true;
        }
        return cart.getStatus().equalsIgnoreCase(status);
    }

    public void updateOrderTabs(MouseEvent mouseEvent) {
        refreshCartNodes(null, null, null, null);
        setStatistics();
    }
    public void sortOrdersWithAttention(List<Cart> orders) {
        Comparator<Cart> comparator = (o1, o2) -> {
            boolean isO1Pending = o1.getStatus().equals("Pending");
            boolean isO2Pending = o2.getStatus().equals("Pending");
            boolean isO1Open = o1.getStatus().equals("Open");
            boolean isO2Open = o2.getStatus().equals("Open");
            boolean isO1OlderThanDay = isOrderOlderThanOneDay(o1);
            boolean isO2OlderThanDay = isOrderOlderThanOneDay(o2);
            boolean isO1AttentionRequired = o1.isAttentionRequired();
            boolean isO2AttentionRequired = o2.isAttentionRequired();

            // Prioritize orders with attention required
            if (isO1AttentionRequired && !isO2AttentionRequired) {
                return -1;
            }
            if (!isO1AttentionRequired && isO2AttentionRequired) {
                return 1;
            }

            // Then prioritize 'Pending' orders, especially those older than a day
            if (isO1Pending && isO1OlderThanDay) {
                o1.setAttentionRequired(true);
                return -1;
            }
            if (isO2Pending && isO2OlderThanDay) {
                o2.setAttentionRequired(true);
                return 1;
            }

            // Next, prioritize other 'Pending' orders
            if (isO1Pending && !isO2Pending) {
                return -1;
            }
            if (!isO1Pending && isO2Pending) {
                return 1;
            }

            // Then 'Open' orders
            if (isO1Open && !isO2Open) {
                return -1;
            }
            if (!isO1Open && isO2Open) {
                return 1;
            }
            return o1.getStatus().compareTo(o2.getStatus());
        };

        Collections.sort(orders, comparator);
    }
    private boolean isOrderOlderThanOneDay(Cart cart) {
        LocalDate oneDayAgo = LocalDate.now().minusDays(1);
        return cart.getDateCreated().isBefore(oneDayAgo);
    }

}
