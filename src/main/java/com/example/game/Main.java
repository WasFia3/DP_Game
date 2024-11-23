package com.example.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {

        // Create game icon
        Image icon = new Image(getClass().getResource("/MainDir/gameIcon.png").toExternalForm());

        this.primaryStage = primaryStage;
        this.primaryStage.setUserData(this); // Make Main accessible globally

        // Start with StartScene
        StartScene startScene = new StartScene();
        Scene scene = startScene.createScene(primaryStage);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Coin Clash");
        primaryStage.getIcons().add(icon);
        primaryStage.show();
    }

    // Method to switch scenes
    public void switchToScene(Scene newScene) {

        primaryStage.setScene(newScene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
