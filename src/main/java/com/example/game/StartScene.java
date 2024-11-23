package com.example.game;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.sound.sampled.*;
import java.io.IOException;

public class StartScene {

    private Clip clip; // Clip instance to control playback

    public Scene createScene(Stage primaryStage) {
        Main mainApp = (Main) primaryStage.getUserData();   //This allows you to access
                                                            // Main methods in this class we are in,
                                                            // methods such as switching to the next scene.


        // Play initial background music
        playBackgroundMusic("/MainDir/introMusic.wav");

        // Set the dimensions of the window عشان الشاشة ما تطلع قد راسي
        int width = 800;
        int height = 600;

        // Create a Label
        Label gameLbl = new Label("Coin Clash");

        // Styling game title label with the function styleLabel
        styleLabel(gameLbl, "white", "MEDIUMPURPLE");

        // Create Background
        Image startBKImg = new Image(getClass().getResource("/MainDir/startBackground.jpg").toExternalForm());
        ImageView startBKView = new ImageView(startBKImg);
       // startBKView.setPreserveRatio(false); اتوكع ماله فايدة بس خليه بلاش تتدمر الدنيا ويطلع السبب


        // Create Audio and Sound options Images:
        Image musicIconImg = new Image(getClass().getResource("/MainDir/music.png").toExternalForm());
        ImageView musicIconView = new ImageView(musicIconImg);
        musicIconView.setFitWidth(30);
        musicIconView.setFitHeight(30);

        Image noMusicIconImg = new Image(getClass().getResource("/MainDir/noMusic.png").toExternalForm());
        ImageView noMusicIconView = new ImageView(noMusicIconImg);
        noMusicIconView.setFitWidth(30);
        noMusicIconView.setFitHeight(30);

        // Create a ToggleButton and set it to selected by default (ON state)
        ToggleButton toggleButton = new ToggleButton("");
        toggleButton.setGraphic(musicIconView);
        toggleButton.setSelected(true); // Set the toggle button to "ON" by default

        // Toggle button action for music control
        toggleButton.setOnAction(event -> {
            if (toggleButton.isSelected()) {
                System.out.println("Music is ON");
                toggleButton.setGraphic(musicIconView);
                playBackgroundMusic("/MainDir/introMusic.wav"); // Start music
            } else {
                System.out.println("Music is OFF");
                toggleButton.setGraphic(noMusicIconView);
                stopBackgroundMusic(); // Stop the music
            }
        });

        // Creating Buttons:
        Button startButton = createButton("Start");
        Button quitButton = createButton("Quit");

        // Quit button action
        quitButton.setOnAction(e -> quitGame()); // Used function I created

        // Set action for Start button
        startButton.setOnAction(event -> {
            // When Start is clicked, switch to CharSelection scene
            playClickSound("/MainDir/clickSound.wav");
            CoinsSelectionScene coinsSelectionScene = new CoinsSelectionScene();
            Scene scene = coinsSelectionScene.createScene(primaryStage); // Create the game scene
            mainApp.switchToScene(scene);  // Switch to the new scene using Main's method
            stopBackgroundMusic();
        });

        // Create a GridPane for layout
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER_LEFT); // Align gridPane to the left
        gridPane.setHgap(10); // Horizontal spacing between buttons
        gridPane.setVgap(10); // Vertical spacing between buttons
        gridPane.setPadding(new Insets(0, 0, 0, 50)); // Add left padding for spacing from the edge

        // Add buttons and label to specific positions in the GridPane
        gridPane.add(gameLbl, 0, 0);
        gridPane.add(startButton, 0, 2);
        gridPane.add(quitButton, 0, 3);
        gridPane.add(toggleButton, 0, 4);

        // Define the StackPane for your root layout
        StackPane root = new StackPane();
        root.getChildren().addAll(startBKView, gridPane);

        // Create a scene with the specified width and height
        Scene scene = new Scene(root, width, height);

        // Bind ImageView width and height to the Scene width and height
        startBKView.fitWidthProperty().bind(scene.widthProperty());
        startBKView.fitHeightProperty().bind(scene.heightProperty());

        return scene;
    }










    // Styling method for the label
    public static void styleLabel(Label label, String textColor, String strokeColor) {
        label.setFont(Font.font("Courier New bold", 48));
        label.setStyle("-fx-text-fill: " + textColor + "; -fx-font-weight: bold;");

        DropShadow dropShadow = new DropShadow();
        dropShadow.setColor(Color.valueOf(strokeColor));
        dropShadow.setRadius(10);
        dropShadow.setSpread(0.6);

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setColor(Color.WHITE);
        innerShadow.setRadius(5);
        innerShadow.setChoke(0.7);

        dropShadow.setInput(innerShadow);
        label.setEffect(dropShadow);
    }

    // Method that creates a styled button with animation and click sound
    public Button createButton(String labelText) {
        Button button = new Button(labelText);
        button.setStyle("-fx-background-color: MEDIUMPURPLE; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15px; -fx-padding: 10px 20px;");
        button.setPrefWidth(200);

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
            /* playClickSound("/MainDir/clickSound.wav");
            useless. doesn't work. u have to add it in btn action. */
            translateTransition.playFromStart();
        });

        return button;
    }

    // Method to play click button sound
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

    // Quit game method
    public void quitGame() {
        playClickSound("/MainDir/clickSound.wav");
        System.out.println("Quitting the game...");
        System.exit(0);
    }
}
