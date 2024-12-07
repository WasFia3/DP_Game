package com.example.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.net.URL;

public class GameScene {

    private boolean isPlayer1Turn = true;  // Track whose turn it is
    private Clip clip;
    private int player1Coins = 0;
    private int player2Coins = 0;
    private int[] coinsArray;

    public Scene createScene(Stage primaryStage, String player1, String player2, String mode, int[] coins) {
        this.coinsArray = coins.clone(); // Store the coins for the game

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

        HBox coinDisplay1 = createCoinDisplay("Knight Coins: ", player1Coins, "/GameDir/coin.png");
        HBox coinDisplay2 = createCoinDisplay("Hornet Coins: ", player2Coins, "/GameDir/coin.png");

        topContainer.getChildren().addAll(coinDisplay1, coinDisplay2);
        BorderPane.setAlignment(topContainer, Pos.TOP_CENTER);
        root.setTop(topContainer);

        // Center: Player Images, Labels, and Coin Array
        HBox centerContainer = new HBox(200); // Space between players
        centerContainer.setAlignment(Pos.CENTER);

        VBox playerBox1 = createPlayerBox("Knight", "/GameDir/knightLeft.png");
        VBox playerBox2 = createPlayerBox("Hornet", "/GameDir/hornetRight.png");

        // Coin Array Display (Horizontal)
        HBox coinArrayDisplay = createHorizontalCoinArray();

        centerContainer.getChildren().addAll(playerBox1, coinArrayDisplay, playerBox2);
        root.setCenter(centerContainer);

        // Bottom: Buttons
        HBox bottomContainer = new HBox(20);
        bottomContainer.setAlignment(Pos.CENTER);

        Button backButton = createStyledButton("Back");
        Button replayButton = createStyledButton("Replay");

        backButton.setOnAction(e -> {
            stopBackgroundMusic();
            GameModeScene gameModeScene = new GameModeScene();
            Scene scene = gameModeScene.createScene(primaryStage, coins);
            mainApp.switchToScene(scene);
        });

        replayButton.setOnAction(e -> {
            // Reset the game variables
            player1Coins = 0;
            player2Coins = 0;
            updateCoinLabel(coinDisplay1, "Player 1 Coins: ", player1Coins);
            updateCoinLabel(coinDisplay2, "Player 2 Coins: ", player2Coins);
            coinsArray = coins.clone(); // Reset the coins array

            // Clear the winner label if it's displayed
            root.setCenter(centerContainer);  // Reset to the initial game state without the winner label

            // Update the coin array display
            updateCoinArrayDisplay(coinArrayDisplay);

            // Restart the game simulation
            startGameSimulation(coinArrayDisplay, coinDisplay1, coinDisplay2, root);

        });


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
                playBackgroundMusic("/GameDir/gameBKMusic.wav"); // Start music
            } else {
                System.out.println("Music is OFF");
                toggleButton.setGraphic(noMusicIconView);
                stopBackgroundMusic(); // Stop the music
            }
        });

        bottomContainer.getChildren().addAll(toggleButton, backButton, replayButton);
        root.setBottom(bottomContainer);

        // Start automatic game play (simulating two players automatically)
        startGameSimulation(coinArrayDisplay, coinDisplay1, coinDisplay2, root);

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

    private HBox createHorizontalCoinArray() {
        HBox coinArrayBox = new HBox(10); // Spacing between coins
        coinArrayBox.setAlignment(Pos.CENTER);

        for (int coin : coinsArray) {
            Label coinLabel = new Label(String.valueOf(coin));
            coinLabel.setStyle("-fx-background-color: CADETBLUE; -fx-padding: 10px; -fx-border-radius: 15px; -fx-font-size: 16px; -fx-text-fill: black;");
            coinArrayBox.getChildren().add(coinLabel);
        }

        return coinArrayBox;
    }

    private void updateCoinArrayDisplay(HBox coinArrayDisplay) {
        coinArrayDisplay.getChildren().clear(); // Clear current display
        for (int coin : coinsArray) {
            Label coinLabel = new Label(String.valueOf(coin));
            coinLabel.setStyle("-fx-background-color: CADETBLUE; -fx-padding: 10px; -fx-border-radius: 15px; -fx-font-size: 16px; -fx-text-fill: black;");
            coinArrayDisplay.getChildren().add(coinLabel);
        }
    }

    private Timeline gameTimeline;  // Add this at the class level to keep track of the timeline

    private void startGameSimulation(HBox coinArrayDisplay, HBox coinDisplay1, HBox coinDisplay2, BorderPane root) {
        // Stop any existing timeline before starting a new one
        if (gameTimeline != null) {
            gameTimeline.stop();
        }

        gameTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    if (coinsArray.length == 0) {
                        // Game over, determine winner
                        endGame(coinDisplay1, coinDisplay2, root);
                        return; // Stop further game actions
                    }

                    int pick;
                    if (isPlayer1Turn) {
                        pick = pickCoinForPlayer1();
                        player1Coins += coinsArray[pick];
                        updateCoinLabel(coinDisplay1, "Player 1 Coins: ", player1Coins);
                    } else {
                        pick = pickCoinForPlayer2();
                        player2Coins += coinsArray[pick];
                        updateCoinLabel(coinDisplay2, "Player 2 Coins: ", player2Coins);
                    }

                    // Remove picked coin and update display
                    coinsArray = removeElement(coinsArray, pick);
                    updateCoinArrayDisplay(coinArrayDisplay);
                    isPlayer1Turn = !isPlayer1Turn; // Switch turns
                })
        );
        gameTimeline.setCycleCount(Timeline.INDEFINITE); // Run until manually stopped
        gameTimeline.play();
    }


    private void endGame(HBox coinDisplay1, HBox coinDisplay2, BorderPane root) {
        // Determine the winner
        String winnerMessage;
        if (player1Coins > player2Coins) {
            winnerMessage = "Knight Wins!";
        } else if (player1Coins < player2Coins) {
            winnerMessage = "Hornet Wins!";
        } else {
            winnerMessage = "It's a Draw!";
        }

        // Display the winner message in the center
        Label winnerLabel = new Label(winnerMessage);
        // Styling game title label with the function styleLabel
        styleLabel(winnerLabel, "white", "MEDIUMPURPLE");
        winnerLabel.setAlignment(Pos.CENTER);

        VBox winnerBox = new VBox(winnerLabel);
        winnerBox.setAlignment(Pos.CENTER);
        root.setCenter(winnerBox);  // Show winner message in the center
    }

    private int pickCoinForPlayer1() {
        return 0; // Example: always pick the first coin
    }

    private int pickCoinForPlayer2() {
        return 0; // Example: always pick the first coin
    }

    private void updateCoinLabel(HBox coinBox, String text, int coins) {
        Label label = (Label) coinBox.getChildren().get(1); // Assuming the label is the second child
        label.setText(text + coins);
    }

    private void playBackgroundMusic(String musicPath) {
        try {
            URL url = getClass().getResource(musicPath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopBackgroundMusic() {
        if (clip != null) {
            clip.stop();
        }
    }

    private int[] removeElement(int[] array, int index) {
        if (index < 0 || index >= array.length) {
            return array; // If index is out of bounds, return the original array
        }

        int[] newArray = new int[array.length - 1];
        System.arraycopy(array, 0, newArray, 0, index);
        System.arraycopy(array, index + 1, newArray, index, array.length - index - 1);
        return newArray;
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);

        button.setStyle(
                "-fx-background-color: MEDIUMPURPLE; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 20px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 15px; " +
                        "-fx-padding: 10px 20px;"
        );

        button.setPrefWidth(200);

        DropShadow hoverEffect = new DropShadow();
        hoverEffect.setColor(Color.LIGHTBLUE);
        hoverEffect.setRadius(15);
        hoverEffect.setSpread(0.5);

        button.setOnMouseEntered(e -> button.setEffect(hoverEffect));
        button.setOnMouseExited(e -> button.setEffect(null));

        return button;
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


}
