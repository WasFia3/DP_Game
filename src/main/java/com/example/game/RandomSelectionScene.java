package com.example.game;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Random;
import java.util.stream.IntStream;

public class RandomSelectionScene {

    private int[] randomCoinsArray;

    public Scene createScene(Stage primaryStage) {
        Main mainApp = (Main) primaryStage.getUserData();

        // Load background image
        Image backgroundImg = new Image(getClass().getResource("/CoinsDir/bk.png").toExternalForm());
        ImageView backgroundView = new ImageView(backgroundImg);
        backgroundView.setPreserveRatio(false);

        // VBox layout for the form
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20px;");

        // Label instructions
        Label instructionsLabel = new Label("Enter the range and number of coins:");
        instructionsLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Text field for number of coins
        TextField numCoinsField = new TextField();
        numCoinsField.setPromptText("Enter the number of coins");
        numCoinsField.setMaxWidth(300);

        // Ensure only digits are allowed
        numCoinsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                numCoinsField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Text field for start range
        TextField startRangeField = new TextField();
        startRangeField.setPromptText("Enter start range");
        startRangeField.setMaxWidth(300);

        // Ensure only digits are allowed
        startRangeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                startRangeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Text field for end range
        TextField endRangeField = new TextField();
        endRangeField.setPromptText("Enter end range");
        endRangeField.setMaxWidth(300);

        // Ensure only digits are allowed
        endRangeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                endRangeField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Result label for feedback
        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        // Back button
        Button backButton = createButton("Back");
        backButton.setOnAction(e -> {
            playClickSound("/MainDir/clickSound.wav");
            CoinsSelectionScene coinsSelectionScene = new CoinsSelectionScene();
            Scene scene = coinsSelectionScene.createScene(primaryStage);
            mainApp.switchToScene(scene);
        });

        // Start button
        Button startButton = createButton("Start");
        startButton.setOnAction(e -> {
            playClickSound("/MainDir/clickSound.wav");
            String numCoinsText = numCoinsField.getText();
            String startRangeText = startRangeField.getText();
            String endRangeText = endRangeField.getText();

            if (numCoinsText.isEmpty() || startRangeText.isEmpty() || endRangeText.isEmpty()) {
                resultLabel.setText("Please fill out all fields.");
                return;
            }

            int numCoins = Integer.parseInt(numCoinsText);
            int startRange = Integer.parseInt(startRangeText);
            int endRange = Integer.parseInt(endRangeText);

            if (startRange >= endRange) {
                resultLabel.setText("Start range must be less than end range.");
                return;
            }

            if (numCoins <= 0) {
                resultLabel.setText("Number of coins must be greater than 0.");
                return;
            }

            // Generate random coin values
            Random random = new Random();
            randomCoinsArray = IntStream.range(0, numCoins)
                    .map(i -> random.nextInt(endRange - startRange + 1) + startRange)
                    .toArray();

            // Confirmation alert to display the generated random values
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Random Coins Generated");
            confirmationAlert.setHeaderText("Here are your random coins:");
            confirmationAlert.setContentText("Random coin values: " + arrayToString(randomCoinsArray));

            ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
            confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton);

            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == confirmButton) {
                    // Proceed to the next scene
                    GameModeScene gameModeScene = new GameModeScene();
                    Scene scene = gameModeScene.createScene(primaryStage,randomCoinsArray);
                    mainApp.switchToScene(scene);
                } else {
                    resultLabel.setText("Action canceled. Adjust ranges if needed.");
                }
            });
        });

        // VBox layout for the form (instructions and text fields)
        VBox formContainer = new VBox(20);
        formContainer.getChildren().addAll(instructionsLabel, numCoinsField, startRangeField, endRangeField, resultLabel);
        formContainer.setStyle("-fx-alignment: center;");

        // HBox layout for the buttons
        HBox buttonContainer = new HBox(20);
        buttonContainer.getChildren().addAll(backButton, startButton);
        buttonContainer.setStyle("-fx-alignment: center;");

        // Combine the form and buttons into a single VBox
        VBox mainContainer = new VBox(40); // Increased spacing between form and buttons
        mainContainer.getChildren().addAll(formContainer, buttonContainer);
        mainContainer.setStyle("-fx-alignment: center;");

        // Root container
        StackPane root = new StackPane();
        root.getChildren().addAll(backgroundView, mainContainer);

        // Set the alignment for the mainContainer to center it vertically
        StackPane.setAlignment(mainContainer, javafx.geometry.Pos.CENTER);
        StackPane.setMargin(mainContainer, new Insets(0, 0, 80, 0)); // Adjust to move everything up/down

        // Set up background and scene
        Scene scene = new Scene(root, 800, 600);
        backgroundView.fitWidthProperty().bind(scene.widthProperty());
        backgroundView.fitHeightProperty().bind(scene.heightProperty());

        return scene;
    }

    // Helper method to convert array to a readable string
    private String arrayToString(int[] array) {
        StringBuilder sb = new StringBuilder();
        for (int num : array) {
            sb.append(num).append(" ");
        }
        return sb.toString().trim();
    }

    // Method to create styled buttons (same as in your previous class)
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

        button.setOnAction(e -> translateTransition.playFromStart());

        return button;
    }

    public int[] getRandomCoinsArray() {
        return randomCoinsArray;
    }

    // Play click sound
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
}
