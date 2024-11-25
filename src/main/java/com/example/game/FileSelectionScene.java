package com.example.game;

// Import necessary packages
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

public class FileSelectionScene {

    private int[] coinsArray; // Stores the extracted coin values

    public Scene createScene(Stage primaryStage) {
        Main mainApp = (Main) primaryStage.getUserData();

        // Load the background image
        Image bk = new Image(getClass().getResource("/CoinsDir/bk.png").toExternalForm());
        ImageView backgroundView = new ImageView(bk);
        backgroundView.setPreserveRatio(false); // Do not maintain aspect ratio

        // Root layout
        VBox layout = new VBox(20);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20px;");

        // Label to display the selected file path
        Label filePathLabel = new Label("No file selected.");
        filePathLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

        // File chooser button
        Button fileChooserButton = new Button("Select File");
        fileChooserButton.setStyle("-fx-background-color: LIGHTGREEN; -fx-text-fill: white;" +
                " -fx-font-size: 18px; -fx-font-weight: bold;" +
                " -fx-background-radius: 15px; -fx-padding: 10px 20px;");
        fileChooserButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Coin Data File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
            File selectedFile = fileChooser.showOpenDialog(primaryStage);

            if (selectedFile != null) {
                filePathLabel.setText("Selected File: " + selectedFile.getAbsolutePath());
                try {
                    // Read and process the file
                    String[] fileContents = Files.lines(selectedFile.toPath()).toArray(String[]::new);

                    if (fileContents.length < 2) {
                        showError("File Format Error", "The file must contain at least two lines.");
                        return;
                    }

                    // Parse the first line as the number of coins
                    int numberOfCoins = Integer.parseInt(fileContents[0].trim());

                    // Parse the second line as coin values
                    int[] coinValues = Arrays.stream(fileContents[1].trim().split("\\s+"))
                            .mapToInt(Integer::parseInt)
                            .toArray();

                    if (coinValues.length != numberOfCoins) {
                        showError("Data Error", "Number of coins does not match the number of values.");
                        return;
                    }

                    // Store and display the data
                    this.coinsArray = coinValues;
                } catch (NumberFormatException ex) {
                    showError("Parsing Error", "Ensure the file contains valid numbers.");
                } catch (IOException ex) {
                    showError("File Error", "An error occurred while reading the file.");
                }
            } else {
                filePathLabel.setText("No file selected.");
            }
        });

        // Back button
        Button backButton = createButton("Back");

        backButton.setOnAction(e -> {
            playClickSound("/MainDir/clickSound.wav");
            CoinsSelectionScene coinsSelectionScene = new CoinsSelectionScene();
            Scene scene = coinsSelectionScene.createScene(primaryStage);
            mainApp.switchToScene(scene);
        });

        // Next button to validate input
        Button nextButton = createButton("Next");
        nextButton.setOnAction(e -> {
            if (coinsArray == null || coinsArray.length == 0) {
                showError("No File Loaded", "Please load a valid file before proceeding.");
                return;
            }

            // Create custom buttons for the confirmation alert
            ButtonType confirmButton = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

            // Confirmation alert to verify the user's data
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Your Data");
            confirmationAlert.setHeaderText("Please confirm your coin data");
            confirmationAlert.setContentText("Is this your coin data?\n\n" +
                    "Coins number: " + coinsArray.length + "\n" +
                    "Coin values: " + Arrays.toString(coinsArray));

            confirmationAlert.getButtonTypes().setAll(confirmButton, cancelButton); // Set custom buttons

            // Show the alert and wait for the user's response
            confirmationAlert.showAndWait().ifPresent(response -> {
                if (response == confirmButton) {
                    // Save the coins array and proceed to the next scene
                    CharSelectionScene charSelectionScene = new CharSelectionScene();
                    Scene scene = charSelectionScene.createScene(primaryStage);
                    mainApp.switchToScene(scene);
                } else {
                    // If the user cancels, show feedback or do nothing
                    showError("Action Canceled", "Please review your data.");
                }
            });
        });

        // Horizontal layout for Back and Next buttons
        HBox buttonContainer = new HBox(20); // Space between buttons
        buttonContainer.setStyle("-fx-alignment: center;");
        buttonContainer.getChildren().addAll(backButton, nextButton);

        // Add all components to the layout
        layout.getChildren().addAll(fileChooserButton, filePathLabel, buttonContainer);

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

    // Method to create buttons with the desired style and effect
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
            playClickSound("/MainDir/clickSound.wav");
            translateTransition.playFromStart();
        });

        return button;
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

    // Helper method to display error messages
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Getter method for coinsArray
    public int[] getCoinsArray() {
        return coinsArray;
    }
}
