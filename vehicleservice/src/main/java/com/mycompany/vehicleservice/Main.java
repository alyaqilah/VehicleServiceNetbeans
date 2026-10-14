package com.mycompany.vehicleservice;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("VehicleServiceView.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 900, 640);
        
        // Link external CSS stylesheet
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        
        stage.setTitle("Vehicle Service Cost Calculator");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}