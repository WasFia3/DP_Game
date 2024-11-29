package com.example.game;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.IOException;

public class CoinsSelectionScene {
    private Clip clip; // Clip instance to control playback

    // Method to create and return the scene
    public Scene createScene(Stage primaryStage) {
        Main mainApp = (Main) primaryStage.getUserData();

        // Load the background image
        Image bk = new Image(getClass().getResource("/CoinsDir/bk.png").toExternalForm());
        ImageView backgroundView = new ImageView(bk);
        backgroundView.setPreserveRatio(false); // Do not maintain aspect ratio

        // VBox layout to hold buttons and toggle button
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20px;");

        // Play initial background music
        playBackgroundMusic("/SelectionDir/selectBKMusic.wav");

        // Audio and Sound options
        Image musicIconImg = new Image(getClass().getResource("/MainDir/music.png").toExternalForm());
        ImageView musicIconView = new ImageView(musicIconImg);
        musicIconView.setFitWidth(30);
        musicIconView.setFitHeight(30);

        Image noMusicIconImg = new Image(getClass().getResource("/MainDir/noMusic.png").toExternalForm());
        ImageView noMusicIconView = new ImageView(noMusicIconImg);
        noMusicIconView.setFitWidth(30);
        noMusicIconView.setFitHeight(30);

        // Create a ToggleButton and set it to selected by default (ON state)
        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setGraphic(musicIconView);
        toggleButton.setSelected(true); // Set the toggle button to "ON" by default

        // Toggle button action for music control
        toggleButton.setOnAction(event -> {
            if (toggleButton.isSelected()) {
                System.out.println("Music is ON");
                toggleButton.setGraphic(musicIconView);
                playBackgroundMusic("/SelectionDir/selectBKMusic.wav"); // Start music
            } else {
                System.out.println("Music is OFF");
                toggleButton.setGraphic(noMusicIconView);
                stopBackgroundMusic(); // Stop the music
            }
        });

        Label label = new Label("Determine the number of your coins, and each coin value");
        label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Add buttons
        Button randomBtn = createButton("Random Coins Selection");
        Button manualBtn = createButton("Manual Coins Selection");
        randomBtn.setStyle("-fx-background-color: LIGHTGREEN; -fx-text-fill: white;" +
                " -fx-font-size: 18px; -fx-font-weight: bold;" +
                " -fx-background-radius: 15px; -fx-padding: 10px 20px;");

        manualBtn.setStyle("-fx-background-color: LIGHTGREEN; -fx-text-fill: white;" +
                " -fx-font-size: 18px; -fx-font-weight: bold;" +
                " -fx-background-radius: 15px; -fx-padding: 10px 20px;");

        // File chooser button
        Button fileBtn = createButton("Load Coins from File");
        fileBtn.setStyle("-fx-background-color: LIGHTGREEN; -fx-text-fill: white;" +
                " -fx-font-size: 18px; -fx-font-weight: bold;" +
                " -fx-background-radius: 15px; -fx-padding: 10px 20px;");



        // Create Next and Back buttons
        Button backButton = createButton("Back");
        backButton.setPrefWidth(200);

        // Back button logic
        backButton.setOnAction(e -> {
            playClickSound("/MainDir/clickSound.wav");

            StartScene startScene = new StartScene();
            Scene scene = startScene.createScene(primaryStage);
            mainApp.switchToScene(scene);

            stopBackgroundMusic();
            startScene.playBackgroundMusic("/MainDir/introMusic.wav");
        });



        manualBtn.setOnAction(e -> {
            playClickSound("/MainDir/clickSound.wav");

            ManualSelectionScene manualSelectionScene = new ManualSelectionScene();
            Scene scene = manualSelectionScene.createScene(primaryStage);
            mainApp.switchToScene(scene);
            stopBackgroundMusic(); // Stop the music


        });

        fileBtn.setOnAction(e -> {
            System.out.println("File Button Clicked");
            playClickSound("/MainDir/clickSound.wav");

            FileSelectionScene fileSelectionScene = new FileSelectionScene();
            Scene scene = fileSelectionScene.createScene(primaryStage);
            if (scene != null) {
                mainApp.switchToScene(scene);
                System.out.println("Scene switched successfully.");
            } else {
                System.err.println("Failed to create FileSelectionScene.");
            }
            stopBackgroundMusic(); // Stop the music

        });

        randomBtn.setOnAction(e -> {
            System.out.println("File Button Clicked");
            playClickSound("/MainDir/clickSound.wav");

            RandomSelectionScene randomSelectionScene = new RandomSelectionScene();
            Scene scene = randomSelectionScene.createScene(primaryStage);
            if (scene != null) {
                mainApp.switchToScene(scene);
                System.out.println("Scene switched successfully.");
            } else {
                System.err.println("Failed to create FileSelectionScene.");
            }
            stopBackgroundMusic(); // Stop the music

        });



        // Create a container for the toggle button, back button, and next button
        HBox hBox = new HBox(20); // Horizontal box with spacing of 20
        hBox.setStyle("-fx-alignment: center;");
        hBox.getChildren().addAll(toggleButton, backButton);

        // Add components to layout
        layout.getChildren().addAll(label, randomBtn, manualBtn, fileBtn, hBox);

        // Use StackPane to set background
        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundView, layout);

        // Create the scene
        Scene scene = new Scene(root, 800, 600);

        // Bind the background size to the scene size
        backgroundView.fitWidthProperty().bind(scene.widthProperty());
        backgroundView.fitHeightProperty().bind(scene.heightProperty());

        return scene;
    }

    // Method to create styled buttons with animations
    public Button createButton(String labelText) {
        Button button = new Button(labelText);
        button.setStyle("-fx-background-color: MEDIUMPURPLE; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15px; -fx-padding: 10px 20px;");
        button.setPrefWidth(400);

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);

        ScaleTransition resetScaleTransition = new ScaleTransition(Duration.millis(200), button);
        resetScaleTransition.setToX(1.0);
        resetScaleTransition.setToY(1.0);

        DropShadow hoverGlow = new DropShadow();
        hoverGlow.setColor(Color.LIGHTBLUE);
        hoverGlow.setRadius(15);
        hoverGlow.setSpread(0.5);

        button.setOnMouseEntered(e -> {
            scaleTransition.playFromStart();
            button.setEffect(hoverGlow);
        });

        button.setOnMouseExited(e -> {
            resetScaleTransition.playFromStart();
            button.setEffect(null);
        });

        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(100), button);
        translateTransition.setByY(-10);
        translateTransition.setAutoReverse(true);
        translateTransition.setCycleCount(2);

        button.setOnAction(e -> {
            playClickSound("/MainDir/clickSound.wav");
            translateTransition.playFromStart();
        });

        return button;
    }

    // Play sound effect for button clicks
    public void playClickSound(String fileName) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(fileName));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Play background music from the beginning each time
    public void playBackgroundMusic(String musicFilePath) {
        stopBackgroundMusic();
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(musicFilePath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    // Stop and release the Clip
    public void stopBackgroundMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}
