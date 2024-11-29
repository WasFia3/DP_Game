package com.example.game;

import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class GameModeScene {

    private Clip clip; // Clip instance to control playback
    private String mode = null;

    public Scene createScene(Stage primaryStage, int [] coins) {
        // Ensure Main instance is properly set
        Main mainApp = (Main) primaryStage.getUserData();

        // Play background music
        playBackgroundMusic("/GameModeDir/gameModeMusic.wav");

        // Root layout
        StackPane root = new StackPane();

        // Background image
        Image backgroundImg = loadImage("/GameModeDir/gameModeBK.jpg");
        ImageView backgroundView = new ImageView(backgroundImg);
        backgroundView.setPreserveRatio(false);
        backgroundView.fitWidthProperty().bind(root.widthProperty());
        backgroundView.fitHeightProperty().bind(root.heightProperty());

        // Main container
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(20));

        // "Pick the Game Mode" Label
        Label titleLabel = new Label("Pick the Game Mode");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Game mode buttons
        HBox gameModeContainer = new HBox(20);
        gameModeContainer.setAlignment(Pos.CENTER);

        // Buttons for game modes
        Button computerVsComputerButton = createGameModeButton("Computer vs. Computer", "/GameModeDir/computer.png");
        Button twoPlayerButton = createGameModeButton("2 Player", "/GameModeDir/knightPixled.png");

        computerVsComputerButton.setOnAction(e -> mode = "computer");
        twoPlayerButton.setOnAction(e -> mode = "2player");

        gameModeContainer.getChildren().addAll(computerVsComputerButton, twoPlayerButton);

        // Toggle music button
        ToggleButton toggleMusicButton = createMusicToggleButton();
        HBox toggleMusicContainer = new HBox();
        toggleMusicContainer.setAlignment(Pos.CENTER);
        toggleMusicContainer.getChildren().add(toggleMusicButton);

        // Game mode layout
        VBox gameModeLayout = new VBox(20);
        gameModeLayout.setAlignment(Pos.CENTER);
        gameModeLayout.getChildren().addAll(gameModeContainer, toggleMusicContainer);

        // Navigation buttons
        HBox navigationButtons = new HBox(20);
        navigationButtons.setAlignment(Pos.CENTER);

        Button backButton = createStyledButton("Back");
        backButton.setOnAction(e -> {
            stopBackgroundMusic();
            CoinsSelectionScene coinsSelectionScene = new CoinsSelectionScene();
            Scene scene = coinsSelectionScene.createScene(primaryStage);
            mainApp.switchToScene(scene);
        });

        Button startButton = createStyledButton("Start");
        startButton.setOnAction(e -> {
            if (mode == null) {
                // Show an error alert if no mode is selected
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Selection Error");
                alert.setHeaderText("No Mode Selected");
                alert.setContentText("You must pick a game mode before clicking Start.");
                alert.showAndWait();
                return;
            }

            stopBackgroundMusic(); // Stop current background music

            if (mode.equals("2player")) {

                CharSelectionScene charSelectionScene = new CharSelectionScene();
                Scene scene = charSelectionScene.createScene(primaryStage,mode, coins);
                mainApp.switchToScene(scene);
            } else if (mode.equals("computer")) {

                GameScene gameScene = new GameScene();
                Scene scene = gameScene.createScene(primaryStage, null, null, mode, coins);
                mainApp.switchToScene(scene);
            }
        });
//
        navigationButtons.getChildren().addAll(backButton, startButton);

        // Combine all components
        mainContainer.getChildren().addAll(titleLabel, gameModeLayout, navigationButtons);
        root.getChildren().addAll(backgroundView, mainContainer);

        return new Scene(root, 800, 600);
    }

    private ToggleButton createMusicToggleButton() {
        Image musicIconImg = loadImage("/MainDir/music.png");
        ImageView musicIconView = new ImageView(musicIconImg);
        musicIconView.setFitWidth(30);
        musicIconView.setFitHeight(30);

        Image noMusicIconImg = loadImage("/MainDir/noMusic.png");
        ImageView noMusicIconView = new ImageView(noMusicIconImg);
        noMusicIconView.setFitWidth(30);
        noMusicIconView.setFitHeight(30);

        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setGraphic(musicIconView);
        toggleButton.setSelected(true);

        toggleButton.setOnAction(event -> {
            if (toggleButton.isSelected()) {
                toggleButton.setGraphic(musicIconView);
                playBackgroundMusic("/GameModeDir/gameModeMusic.wav");
            } else {
                toggleButton.setGraphic(noMusicIconView);
                stopBackgroundMusic();
            }
        });

        return toggleButton;
    }

    public void playBackgroundMusic(String musicFilePath) {
        stopBackgroundMusic();
        try {
            URL resource = getClass().getResource(musicFilePath);
            if (resource == null) {
                throw new IOException("Music file not found: " + musicFilePath);
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(resource);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stopBackgroundMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }

    private Button createGameModeButton(String labelText, String imagePath) {
        VBox buttonContainer = new VBox(10);
        buttonContainer.setAlignment(Pos.CENTER);

        Image image = loadImage(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);

        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        Button button = new Button();
        button.setGraphic(imageView);
        button.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);

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

        buttonContainer.getChildren().addAll(button, label);

        Button wrappedButton = new Button();
        wrappedButton.setGraphic(buttonContainer);
        wrappedButton.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        return wrappedButton;
    }

    private Button createStyledButton(String labelText) {
        Button button = new Button(labelText);
        button.setStyle("-fx-background-color: MEDIUMPURPLE; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 15px; -fx-padding: 10px 20px;");
        button.setPrefWidth(150);

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

        return button;
    }

    private Image loadImage(String path) {
        URL resource = getClass().getResource(path);
        if (resource == null) {
            throw new IllegalArgumentException("Image not found: " + path);
        }
        return new Image(resource.toExternalForm());
    }
}
