package com.kursinis.prif4kursinis.fxControllers;

import com.kursinis.prif4kursinis.StartGui;
import com.kursinis.prif4kursinis.fxControllers.userWindowControllers.UserCartWindowController;
import com.kursinis.prif4kursinis.fxControllers.userWindowControllers.UserCatalogueWindowController;
import com.kursinis.prif4kursinis.fxControllers.userWindowControllers.UserOrdersWindowController;
import com.kursinis.prif4kursinis.fxControllers.windowControllers.OrderWindowController;
import com.kursinis.prif4kursinis.fxControllers.windowControllers.ProductsWindowController;
import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import com.kursinis.prif4kursinis.model.Customer;
import com.kursinis.prif4kursinis.model.Manager;
import com.kursinis.prif4kursinis.model.User;
import jakarta.persistence.EntityManagerFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class MainWindowController implements Initializable {

    @FXML
    public Pane adminDashboardPane;
    @FXML
    public Pane orderPageButtons;
    @FXML
    public VBox mainWindowButtons;
    @FXML public VBox userWindowButtons;
    @FXML private VBox managerDashboardButtons;
    @FXML
    public VBox productsPageButtons;
    @FXML
    public Label uidLabel;
    @FXML
    public Label nameSurnameLabel;
    private User currentUser;
    private EntityManagerFactory entityManagerFactory;
    private CustomHib customHib;
    private ProductsWindowController productsWindowController;
    private OrderWindowController orderWindowController;
    @FXML
    private TextField productSearchField;
    private LocalDate currentStartDate;
    private LocalDate currentEndDate;
    private String currentStatus;
    private String currentQuery=null;
    @FXML private ChoiceBox<String> visibilityChoiceBox;
    @FXML
    private Button logoutButton;

    @FXML
    private ChoiceBox<String> statusChoiceBox;
    @FXML
    private DatePicker selectDateFrom;
    @FXML
    private DatePicker selectDateTo;
    @FXML private TextField orderNrSearch;
    @FXML private Button logoutButton1;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        entityManagerFactory = StartGui.getEntityManagerFactory();
        loadEmpty();
    }

    private void loadEmpty() {
        mainWindowButtons.setVisible(false);
        orderPageButtons.setVisible(false);
        productsPageButtons.setVisible(false);
        userWindowButtons.setVisible(false);
        managerDashboardButtons.setVisible(false);
    }

    private void loadPane(String paneName){
        adminDashboardPane.getChildren().clear();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("window/" + paneName + ".fxml"));
        try {
            adminDashboardPane.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void loadOrderPane(ActionEvent actionEvent){
        adminDashboardPane.getChildren().clear();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("window/orderWindow.fxml"));

        try {
            adminDashboardPane.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        OrderWindowController controller = fxmlLoader.getController();
        controller.setOrdersData(currentUser);
        setOrderWindowController(controller);
        orderPageButtons.setVisible(true);
        mainWindowButtons.setVisible(false);
        productsPageButtons.setVisible(false);
        userWindowButtons.setVisible(false);
        statusChoiceBox.getItems().addAll("Pending", "Open", "Closed", "All"); // Add all statuses you support
        statusChoiceBox.getSelectionModel().select("All");
    }
    public void loadDashboardPane(ActionEvent actionEvent) {
        loadPane("dashboardWindow");
        if(currentUser instanceof Manager && ((Manager) currentUser).isAdmin()){
            mainWindowButtons.setVisible(true);
            managerDashboardButtons.setVisible(false);
        }
        else{
            mainWindowButtons.setVisible(false);
            managerDashboardButtons.setVisible(true);
        }
        orderPageButtons.setVisible(false);
        productsPageButtons.setVisible(false);
        userWindowButtons.setVisible(false);


    }
    private void loadDashboardPane(){
        loadPane("dashboardWindow");
        if(currentUser instanceof Manager && ((Manager) currentUser).isAdmin()) {
            mainWindowButtons.setVisible(true);
            managerDashboardButtons.setVisible(false);
        }
        else{
            mainWindowButtons.setVisible(false);
            managerDashboardButtons.setVisible(true);
        }
        orderPageButtons.setVisible(false);
        productsPageButtons.setVisible(false);
        userWindowButtons.setVisible(false);
    }

    public void loadProductCataloguePane(ActionEvent actionEvent) {
        adminDashboardPane.getChildren().clear();
        FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("window/productsWindow.fxml"));

        try {
            adminDashboardPane.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        productsPageButtons.setVisible(true);
        mainWindowButtons.setVisible(false);
        orderPageButtons.setVisible(false);
        ProductsWindowController controller = fxmlLoader.getController();
        controller.setCurrentUser(currentUser);
        setProductsWindowController(controller);
    }
    public void loadUsersPane(ActionEvent actionEvent) {
        loadPane("usersWindow");
        productsPageButtons.setVisible(false);
        mainWindowButtons.setVisible(true);
        orderPageButtons.setVisible(false);
        userWindowButtons.setVisible(false);
    }
    public void orderMenuBackButton(MouseEvent mouseEvent) {
        loadDashboardPane();
    }

    public void loadMessagesPane(ActionEvent actionEvent) {
        loadPane("messagesWindow");
    }

    public void loadSettingsPane(ActionEvent actionEvent) {
        loadPane("settingsWindow");
    }

    public void logOut(ActionEvent actionEvent) {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
        openLoginWindow();
    }

    public void createNewProduct(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("create/createProduct.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Create New Product");

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setData(User currentUser) {
        this.currentUser = currentUser;
        limitAccess();
        loadData();
    }
    private void loadCustomerWindows(){
        userWindowButtons.setVisible(true);
    }
    private void loadManagerWindows(){
        loadDashboardPane();
    }

    private void limitAccess() {
        if(currentUser instanceof Customer){
            loadCustomerWindows();
        }
        else if(currentUser instanceof Manager && ((Manager) currentUser).isAdmin()){
            loadManagerWindows();
        }
        else{
            loadManagerWindows();
        }
    }

    private void loadData(){
        nameSurnameLabel.setText(currentUser.getName()+" "+currentUser.getSurname());
        uidLabel.setText("UID: "+currentUser.getId());
    }
    public void setProductsWindowController(ProductsWindowController controller) {
        this.productsWindowController = controller;
    }

    public void findProductOnKeyPressed(KeyEvent keyEvent) {
        if (productsWindowController != null) {
            String query = ((TextField) keyEvent.getSource()).getText();
            productsWindowController.onSearchQueryChanged(query);
        }
    }
    public void findOrderOnKeyPressed(KeyEvent keyEvent) {
        if (orderWindowController != null) {
            String query = ((TextField) keyEvent.getSource()).getText();
            currentQuery = query;
            orderWindowController.refreshCartNodes(currentQuery, currentStatus, currentStartDate, currentEndDate);

        }
    }
    public void setOrderWindowController(OrderWindowController controller) {
        this.orderWindowController = controller;
    }
    public void onVisibilityChanged(ActionEvent actionEvent) {
        if (productsWindowController != null) {
            String visibility = visibilityChoiceBox.getValue();
            productsWindowController.onVisibilityChanged(visibility);
        }
    }
    private void openLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(StartGui.class.getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCustomerCart(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("userWindow/userCartWindow.fxml"));
        try {
            adminDashboardPane.getChildren().clear();
            adminDashboardPane.getChildren().add(fxmlLoader.load());
            UserCartWindowController cartController = fxmlLoader.getController();
            cartController.setCurrentUser(currentUser);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCustomerCatalogue(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("userWindow/userCatalogueWindow.fxml"));
        try {
            adminDashboardPane.getChildren().clear();
            adminDashboardPane.getChildren().add(fxmlLoader.load());
            UserCatalogueWindowController catalogueController = fxmlLoader.getController();
            catalogueController.setCurrentUser(currentUser);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadCustomerOrder(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("userWindow/userOrdersWindow.fxml"));
        try {
            adminDashboardPane.getChildren().clear();
            adminDashboardPane.getChildren().add(fxmlLoader.load());
            UserOrdersWindowController ordersController = fxmlLoader.getController();
            ordersController.setCurrentUser(currentUser);

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadCustomerSettings(ActionEvent actionEvent) {
    }
    public void onFilterChanged(ActionEvent actionEvent) {
        if (orderWindowController != null) {
            LocalDate startDate = selectDateFrom.getValue();
            currentStartDate=startDate;
            LocalDate endDate = selectDateTo.getValue();
            currentEndDate=endDate;
            String status = statusChoiceBox.getValue();
            currentStatus=status;
            orderWindowController.refreshCartNodes(currentQuery, status, startDate, endDate);
        }
    }
    public void clearFiltersActionButton(MouseEvent mouseEvent) {
        selectDateFrom.setValue(null);
        selectDateTo.setValue(null);
        statusChoiceBox.getSelectionModel().select("All");
        orderNrSearch.setText("");
        currentStartDate=null;
        currentEndDate=null;
        currentStatus=null;
        currentQuery=null;
        orderWindowController.refreshCartNodes(currentQuery, currentStatus, currentStartDate, currentEndDate);
    }
}
