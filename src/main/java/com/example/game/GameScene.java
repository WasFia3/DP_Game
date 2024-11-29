package com.example.game;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.Scene;
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

public class GameScene {

    private Clip clip;
    private int player1Coins = 0;
    private int player2Coins = 0;

    public Scene createScene(Stage primaryStage, String player1, String player2, String mode, int [] coins) {

        // Ensure Main instance is properly set
        Main mainApp = (Main) primaryStage.getUserData();

        // Play background music
        playBackgroundMusic("/GameDir/gameBKMusic.wav");

        // Root layout
        BorderPane root = new BorderPane();

        // Background
        Image backgroundImg = new Image(getClass().getResource("/GameDir/bk.gif").toExternalForm());
        BackgroundImage bg = new BackgroundImage(backgroundImg, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER,
                new BackgroundSize(1.0, 1.0, true, true, false, false));
        root.setBackground(new Background(bg));

        // Top: Coin Displays
        HBox topContainer = new HBox(200); // Add spacing between elements
        topContainer.setAlignment(Pos.TOP_CENTER);

        HBox coinDisplay1 = createCoinDisplay("Player 1 Coins: ", player1Coins, "/GameDir/coin.png");
        HBox coinDisplay2 = createCoinDisplay("Player 2 Coins: ", player2Coins, "/GameDir/coin.png");

        topContainer.getChildren().addAll(coinDisplay1, coinDisplay2);
        BorderPane.setAlignment(topContainer, Pos.TOP_CENTER);
        root.setTop(topContainer);

        // Center: Player Images and Labels
        HBox centerContainer = new HBox(200); // Space between players
        centerContainer.setAlignment(Pos.CENTER);

        // Handle player and image assignment based on mode
        VBox playerBox1;
        VBox playerBox2;

        if (mode.equalsIgnoreCase("computer")) {
            // Computer mode: Knight on the left, Hornet on the right
            playerBox1 = createPlayerBox("Knight", "/GameDir/knightLeft.png");
            playerBox2 = createPlayerBox("Hornet", "/GameDir/hornetRight.png");
        } else if (mode.equalsIgnoreCase("2player")) {
            // 2-player mode
            if (player1.equalsIgnoreCase("Hornet")) {
                // Hornet is Player 1, Knight is Player 2
                playerBox1 = createPlayerBox("Hornet", "/GameDir/hornetLeft.png");
                playerBox2 = createPlayerBox("Knight", "/GameDir/knightRight.png");
            } else {
                // Knight is Player 1, Hornet is Player 2
                playerBox1 = createPlayerBox("Knight", "/GameDir/knightLeft.png");
                playerBox2 = createPlayerBox("Hornet", "/GameDir/hornetRight.png");
            }
        } else {
            // Fallback case (defaulting to Knight vs Hornet)
            playerBox1 = createPlayerBox("Knight", "/GameDir/knightLeft.png");
            playerBox2 = createPlayerBox("Hornet", "/GameDir/hornetRight.png");
        }

        centerContainer.getChildren().addAll(playerBox1, playerBox2);
        root.setCenter(centerContainer);

        // Bottom: Buttons
        HBox bottomContainer = new HBox(20);
        bottomContainer.setAlignment(Pos.CENTER);

        ToggleButton toggleMusicButton = createMusicToggleButton();
        Button backButton = createStyledButton("Back");
        Button replayButton = createStyledButton("Replay");

        backButton.setOnAction(e -> {
            stopBackgroundMusic();
            GameModeScene gameModeScene = new GameModeScene();
            Scene scene = gameModeScene.createScene(primaryStage,coins);
            mainApp.switchToScene(scene);

        });

        replayButton.setOnAction(e -> {
            player1Coins = 0;
            player2Coins = 0;
            updateCoinLabel(coinDisplay1, "Player 1 Coins: ", player1Coins);
            updateCoinLabel(coinDisplay2, "Player 2 Coins: ", player2Coins);
        });

        bottomContainer.getChildren().addAll(toggleMusicButton, backButton, replayButton);
        root.setBottom(bottomContainer);

        // Testing CHECKED
        for (int i = 0; i < coins.length; i++) {
            System.out.println("Element at index " + i + ": " + coins[i]);
        }
        return new Scene(root, 800, 600);
    }

    private HBox createCoinDisplay(String text, int coins, String coinImagePath) {
        HBox coinBox = new HBox(10); // Horizontal layout for coin and label
        coinBox.setAlignment(Pos.CENTER);

        Image coinImg = new Image(getClass().getResource(coinImagePath).toExternalForm());
        ImageView coinView = new ImageView(coinImg);
        coinView.setFitWidth(40);
        coinView.setFitHeight(40);

        Label label = new Label(text + coins);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        coinBox.getChildren().addAll(coinView, label);

        return coinBox;
    }

    private VBox createPlayerBox(String playerName, String playerImagePath) {
        VBox playerBox = new VBox(10);
        playerBox.setAlignment(Pos.CENTER);

        Image playerImg = new Image(getClass().getResource(playerImagePath).toExternalForm());
        ImageView playerView = new ImageView(playerImg);
        playerView.setPreserveRatio(true);
        playerView.setFitWidth(250); // Enlarged player image
        playerView.setFitHeight(250);

        Label playerLabel = new Label(playerName);
        playerLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");

        playerBox.getChildren().addAll(playerView, playerLabel);

        return playerBox;
    }

    private ToggleButton createMusicToggleButton() {
        Image musicOnImg = new Image(getClass().getResource("/MainDir/music.png").toExternalForm());
        ImageView musicOnView = new ImageView(musicOnImg);
        musicOnView.setFitWidth(30);
        musicOnView.setFitHeight(30);

        Image musicOffImg = new Image(getClass().getResource("/MainDir/noMusic.png").toExternalForm());
        ImageView musicOffView = new ImageView(musicOffImg);
        musicOffView.setFitWidth(30);
        musicOffView.setFitHeight(30);

        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setGraphic(musicOnView);
        toggleButton.setOnAction(e -> {
            if (toggleButton.isSelected()) {
                toggleButton.setGraphic(musicOffView);
                stopBackgroundMusic();
            } else {
                toggleButton.setGraphic(musicOnView);
                playBackgroundMusic("/GameDir/gameBKMusic.wav");
            }
        });

        return toggleButton;
    }


    // Method to create styled buttons with animations

    private Button createStyledButton(String labelText) {
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
                playClickSound("/MainDir/clickSound.wav");
                translateTransition.playFromStart();
            });

            return button;
        }

    private void updateCoinLabel(HBox coinDisplay, String text, int coins) {
        Label label = (Label) coinDisplay.getChildren().get(1);
        label.setText(text + coins);
    }

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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopBackgroundMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
            clip.close();
        }
    }
}
