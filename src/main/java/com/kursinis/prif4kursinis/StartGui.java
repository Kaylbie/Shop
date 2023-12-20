package com.kursinis.prif4kursinis;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartGui extends Application {
    private static EntityManagerFactory entityManagerFactory;

    @Override
    public void start(Stage stage) throws IOException {
        entityManagerFactory = Persistence.createEntityManagerFactory("coursework-shop");
        //FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("login.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(StartGui.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Shop");
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            entityManagerFactory.close(); // Close EntityManagerFactory when the application exits
        });
    }

    public static void main(String[] args) {
        launch();
    }

    public static EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }
}