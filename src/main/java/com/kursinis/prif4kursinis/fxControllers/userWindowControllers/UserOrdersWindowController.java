package com.kursinis.prif4kursinis.fxControllers.userWindowControllers;

import com.kursinis.prif4kursinis.StartGui;
import com.kursinis.prif4kursinis.fxControllers.createControllers.EditUserWindowController;
import com.kursinis.prif4kursinis.fxControllers.windowControllers.DisplayOrderWindowController;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.Cart;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class UserOrdersWindowController implements Initializable {
    @FXML
    private ListView<Cart> ordersListView;
    List<String> statuses = Arrays.asList("Pending", "Open", "Closed", "Cancelled");
    private CustomHib customHib;
    private User currentUser;

    @FXML
    private TableColumn<OrderViewModel, Void> cancelOrder;

    @FXML
    ComboBox statusFilterComboBox;
    private EntityManagerFactory entityManagerFactory;
    @FXML
    private TableView<OrderViewModel> ordersTableView;
    @FXML
    private TableColumn<OrderViewModel, String> totalPriceColumn;
    @FXML
    private TableColumn<OrderViewModel, Number> quantityColumn;
    @FXML
    private TableColumn<OrderViewModel, Number> idColumn;
    @FXML
    private TableColumn<OrderViewModel, String> managerNameColumn;
    @FXML
    private TableColumn<OrderViewModel, String> statusColumn;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entityManagerFactory = StartGui.getEntityManagerFactory();
        customHib = new CustomHib(entityManagerFactory);
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        managerNameColumn.setCellValueFactory(new PropertyValueFactory<>("managerName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        ordersTableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Detect double-click
                openOrderDetailsWindow();
            }
        });
    }
    private void loadOrders() {
        List<Cart> orders = customHib.findCartsByUserIdAndStatuses(currentUser.getId(), statuses); // Implement this method in CustomHib
        ObservableList<OrderViewModel> orderViewModels = FXCollections.observableArrayList();

        for (Cart cart : orders) {
            String managerName = cart.getResponsibleManager() != null ? cart.getResponsibleManager().getName() : "N/A";
            orderViewModels.add(new OrderViewModel(cart.getId(), cart.getTotal(), cart.getItemCount(), managerName, cart.getStatus()));
        }

        ordersTableView.setItems(orderViewModels);
        setupCancelOrderButton();
    }


    public void setCurrentUser(User currentUser) {
        this.currentUser=currentUser;
        loadOrders();
    }
    private void setupCancelOrderButton() {
        cancelOrder.setCellFactory(param -> new TableCell<OrderViewModel, Void>() {
            private final Button cancelBtn = new Button("Cancel Order");

            {
                cancelBtn.setOnAction(event -> {
                    OrderViewModel orderViewModel = getTableView().getItems().get(getIndex());
                    Cart cart = customHib.findCartById(orderViewModel.getId()); // Assuming OrderViewModel has a getCartId method

                    if (cart != null) {
                        cart.setStatus("Cancelled");
                        customHib.update(cart);
                        showAlert("Order Cancelled", "Order has been cancelled.");
                        loadOrders();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(cancelBtn);
                }
            }
        });

    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public void onFilter(ActionEvent actionEvent) {
        Object selectedStatusObject = statusFilterComboBox.getValue();
        //System.out.println(selectedStatusObject);
        String selectedStatus = null;

        if (selectedStatusObject instanceof String) {
            selectedStatus = (String) selectedStatusObject;

        }

        if (selectedStatus != null && !selectedStatus.isEmpty()) {
            List<Cart> filteredOrders = customHib.findCartsByUserIdAndStatus(currentUser.getId(), selectedStatus);
            ObservableList<OrderViewModel> orderViewModels = FXCollections.observableArrayList();
            for (Cart cart : filteredOrders) {
                String managerName = cart.getResponsibleManager() != null ? cart.getResponsibleManager().getName() : "N/A";
                orderViewModels.add(new OrderViewModel(cart.getId(), cart.getTotal(), cart.getItemCount(), managerName, cart.getStatus()));
            }
            ordersTableView.getItems().clear();
            ordersTableView.setItems(orderViewModels);
        } else {
        }
        setupCancelOrderButton();
    }

    public void onReset(ActionEvent actionEvent) {
        loadOrders();
    }

    public void openOrderDetailsWindow() {
        OrderViewModel selectedOrderViewModel = ordersTableView.getSelectionModel().getSelectedItem();
        if (selectedOrderViewModel != null) {
            Cart selectedCart = customHib.findCartById(selectedOrderViewModel.getId());

            if (selectedCart != null) {
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("userWindow/displayOrderWindow.fxml"));
                    Parent root = fxmlLoader.load();
                    DisplayOrderWindowController controller = fxmlLoader.getController();
                    controller.setProductData(selectedCart, currentUser); // Assuming setCartData method exists in your controller
                    Stage stage = new Stage();
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setTitle("Order Nr. " + selectedCart.getId());
                    stage.setScene(new Scene(root));
                    stage.showAndWait();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
