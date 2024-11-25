package com.example.game;

import javax.sound.sampled.Clip;

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

import java.util.Arrays;

public class ManualSelectionScene {

    private int[] coinsArray;

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
        Label instructionsLabel = new Label("Enter the number of coins and their values:");
        instructionsLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Text field for number of coins
        TextField numberOfCoinsField = new TextField();
        numberOfCoinsField.setPromptText("Enter the number of coins (even number)");
        numberOfCoinsField.setMaxWidth(300);

                    // Add listener for when the user leaves the field (focus is lost)
        numberOfCoinsField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Remove spaces or non-digit characters immediately as the user types
            if (!newValue.matches("\\d*")) {
                numberOfCoinsField.setText(newValue.replaceAll("[^\\d]", "")); // Replace non-digits with an empty string
            }
        });

        numberOfCoinsField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Remove non-digit characters immediately
            if (!newValue.matches("\\d*")) {
                numberOfCoinsField.setText(newValue.replaceAll("[^\\d]", "")); // Replace non-digits
            } else if (!newValue.isEmpty()) {
                try {
                    int value = Integer.parseInt(newValue);
                    if (value > 1000) {
                        numberOfCoinsField.setText(oldValue); // Revert if the number exceeds 1000
                    }
                } catch (NumberFormatException e) {
                    numberOfCoinsField.setText(oldValue); // Handle parsing errors (shouldn't happen here)
                }
            }
        });

        numberOfCoinsField.focusedProperty().addListener((observable, wasFocused, isFocused) -> {
            // Check if the field lost focus (user finished typing and clicked elsewhere)
            if (!isFocused) {
                String newValue = numberOfCoinsField.getText();  // Get the current text

                // Make sure the text is not empty and is a valid number
                if (!newValue.isEmpty()) {
                    try {
                        int value = Integer.parseInt(newValue);  // Try parsing the text into an integer
                        if (!isEven(value)) {  // If the number is not even
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Invalid Input");
                            alert.setHeaderText("Insert Error");
                            alert.setContentText("The number of coins must be even! Click OK and enter again");
                            alert.showAndWait();
                        }
                    } catch (NumberFormatException e) {
                        // Handle case where the text is not a valid number
                        // Should not occur since invalid characters are removed
                    }
                }
            }
        });





        // Text field for coin values
        TextField coinValuesField = new TextField();
        coinValuesField.setPromptText("Enter coin values (space-separated)");
        coinValuesField.setMaxWidth(300);
        coinValuesField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[\\d\\s]*")) {
                coinValuesField.setText(oldValue); // Restrict input to numbers and spaces only
            }
        });

        // Result label for feedback
        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px;");

        // Back button
        Button backButton = createButton("Back");
        backButton.setOnAction(e -> {
            CoinsSelectionScene coinsSelectionScene = new CoinsSelectionScene();
            Scene scene = coinsSelectionScene.createScene(primaryStage);
            mainApp.switchToScene(scene);
        });

        // Next button to validate input
        Button nextButton = createButton("Next");
        nextButton.setOnAction(e -> {
            String numCoinsText = numberOfCoinsField.getText();
            String coinValuesText = coinValuesField.getText();

            if (numCoinsText.isEmpty() || coinValuesText.isEmpty()) {
                resultLabel.setText("Please enter both fields.");
                return;
            }

            int numCoins = Integer.parseInt(numCoinsText);
            int[] coins = Arrays.stream(coinValuesText.trim().split("\\s+"))
                    .mapToInt(Integer::parseInt)
                    .toArray();

            if (coins.length != numCoins) {
                resultLabel.setText("Number of coins does not match the values entered.");
                return;
            }

            // Create custom buttons for the confirmation alert
            ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("NO", ButtonBar.ButtonData.CANCEL_CLOSE);

            // Confirmation alert to verify the user's data
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Your Data");
            confirmationAlert.setHeaderText("Please confirm your coin data");
            confirmationAlert.setContentText("Is this your coin data?\n\n"+"Number of coins: " + numCoins + "\nCoin values: " + Arrays.toString(coins));
            confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton); // Set custom buttons

            // Show the alert and wait for the user's response
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == confirmButton) {
                    // Save the coins array and proceed to the next scene
                    this.coinsArray = coins;
                    CharSelectionScene charSelectionScene = new CharSelectionScene();
                    Scene scene = charSelectionScene.createScene(primaryStage);
                    mainApp.switchToScene(scene);
                } else {
                    // If the user cancels, show feedback or do nothing
                    resultLabel.setText("Action canceled. Please review your data.");
                }
            });
        });


        // VBox layout for the form (instructions and text fields)
        VBox formContainer = new VBox(20);
        formContainer.getChildren().addAll(instructionsLabel, numberOfCoinsField, coinValuesField, resultLabel);
        formContainer.setStyle("-fx-alignment: center;");

        // HBox layout for the buttons
        HBox buttonContainer = new HBox(20);
        buttonContainer.getChildren().addAll(backButton, nextButton);
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

    // checks if the number is even, return true if yes
    public boolean isEven(int number) {
        return number % 2 == 0;
    }


    // Method to create styled buttons
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

    public int[] getCoinsArray() {
        return coinsArray;
    }
}
