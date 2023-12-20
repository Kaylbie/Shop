package com.kursinis.prif4kursinis.fxControllers.windowControllers;

import com.kursinis.prif4kursinis.hibernateControllers.CustomHib;
import jakarta.persistence.EntityManagerFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.util.*;

public class DashboardWindowController implements Initializable {
    private EntityManagerFactory entityManagerFactory;
    @FXML
    private Label totalSalesLabel;
    @FXML private Label totalRevenueLabel;
    @FXML private Label numberOfOrdersLabel;
    @FXML
    private BarChart<String, Number> salesBarChart;
    private CustomHib customHib;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        entityManagerFactory= com.kursinis.prif4kursinis.StartGui.getEntityManagerFactory();
        customHib=new CustomHib(entityManagerFactory);
        updateTextStatistics();
        updateSalesChart();
    }
    private void updateTextStatistics() {
        int totalSales = customHib.getTotalSalesCount();
        double totalRevenue = customHib.getTotalRevenue();
        int numberOfOrders = customHib.getNumberOfOrders();

        totalSalesLabel.setText("Total Sales: " + totalSales);
        totalRevenueLabel.setText("Total Revenue: $" + totalRevenue);
        numberOfOrdersLabel.setText("Number of Orders: " + numberOfOrders);
    }
    private void updateSalesChart() {
        Map<LocalDate, Double> salesData = customHib.getTotalSalesForEachDayLast30Days();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        for (Map.Entry<LocalDate, Double> entry : salesData.entrySet()) {
            String date = entry.getKey().toString();
            Double totalSales = entry.getValue();
            series.getData().add(new XYChart.Data<>(date, totalSales));
        }

        salesBarChart.getData().clear();
        salesBarChart.getData().add(series);
    }



}
