package com.example.game;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.*;
import java.io.IOException;

public class CharSelectionScene {

    private Clip clip; // Clip instance to control playback

    // Selected Characters variables:

    private String plyer1Character = null;    // Tracks the player's selected character
    private String plyer2Character = null;  // Tracks the computer's assigned character

    // Method to create the selection scene
    public Scene createScene(Stage primaryStage, String mode, int [] coins) {

        Main mainApp = (Main) primaryStage.getUserData(); //This allows you to access
                                                     // Main methods in this class we are in,
                                                    // methods such as switching to the next scene.



        // Play initial background music
        playBackgroundMusic("/SelectionDir/selectBKMusic.wav");


        // Audio and Sound options:
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
                playBackgroundMusic("/SelectionDir/SelectBKMusic.wav"); // Start music
            } else {
                System.out.println("Music is OFF");
                toggleButton.setGraphic(noMusicIconView);
                stopBackgroundMusic(); // Stop the music
            }
        });





        // Load character images
        Image hornetImg = new Image(getClass().getResource("/SelectionDir/Hornet.png").toExternalForm());
        Image knightImg = new Image(getClass().getResource("/SelectionDir/Knight.png").toExternalForm());

        // Create ImageViews with specified sizes
        ImageView hornetView = new ImageView(hornetImg);
        hornetView.setFitWidth(200);
        hornetView.setFitHeight(300);
        hornetView.setPreserveRatio(true);

        ImageView knightView = new ImageView(knightImg);
        knightView.setFitWidth(200);
        knightView.setFitHeight(300);
        knightView.setPreserveRatio(true);

        // Create labels for characters
        Label hornetLabel = new Label("Hornet");
        Label knightLabel = new Label("Knight");

        // Adding effects to the labels
        styleLabel(hornetLabel, "white", "MEDIUMPURPLE");
        styleLabel(knightLabel, "white", "MEDIUMPURPLE");

        // Adding more details to labels font SIZE and WEIGHT
        hornetLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");
        knightLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        // Create Title label
        Label title = new Label("Select a character");

        // Adding effects to the title
        styleLabel(title, "white", "MEDIUMPURPLE");

        // Adding more details to title font SIZE and WEIGHT
        title.setStyle("-fx-font-size: 40px; -fx-font-weight: bold;");

        // Create character selection buttons and set images as button graphics
        Button hornetButton = new Button();
        hornetButton.setGraphic(hornetView);
        hornetButton.setStyle("-fx-background-color: transparent;");
        addButtonAnimations(hornetButton);  // Add animation and sound effects to the hornet button

        Button knightButton = new Button();
        knightButton.setGraphic(knightView);
        knightButton.setStyle("-fx-background-color: transparent;");
        addButtonAnimations(knightButton);  // Add animation and sound effects to the knight button

        // VBox for each character to stack image and label vertically
        VBox hornetBox = new VBox(10, hornetButton, hornetLabel);
        hornetBox.setAlignment(Pos.CENTER);

        VBox knightBox = new VBox(10, knightButton, knightLabel);
        knightBox.setAlignment(Pos.CENTER);

        // HBox to hold character VBoxes
        HBox charBox = new HBox(40, hornetBox, knightBox);
        charBox.setAlignment(Pos.CENTER);

        // Creating buttons with style
        Button startButton = createButton("start");
        Button backButton = createButton("Back");

        backButton.setOnAction(e -> {
            // Play the click sound
            playClickSound("/MainDir/clickSound.wav");

            // Create and switch to the StartScene
            CoinsSelectionScene coinsSelectionScene = new CoinsSelectionScene();
            Scene scene = coinsSelectionScene.createScene(primaryStage); // Create the StartScene
            mainApp.switchToScene(scene);  // Use Main's method to switch the scene

            // Stop current background music if applicable
            stopBackgroundMusic();

            // Optionally, restart background music for StartScene
           // coinsSelectionScene.playBackgroundMusic("/MainDir/introMusic.wav");
        });

        startButton.setOnAction(e -> {
            if (plyer1Character != null && isSelected(plyer1Character)) {
                // Play the click sound
                playClickSound("/MainDir/clickSound.wav");

                // Create and switch to the GameScene
                GameScene gameScene = new GameScene();

                // Check if the mode is "2Player", if so, assign both players
                if ("2Player".equals(mode)) {
                    // Use the playerCharacter for player1 and computerCharacter for player2
                    Scene scene = gameScene.createScene(primaryStage, plyer1Character, plyer2Character, mode, coins);
                    mainApp.switchToScene(scene);  // Switch to the new scene
                } else {
                    // If it's not 2Player mode, keep the previous logic
                    Scene scene = gameScene.createScene(primaryStage, plyer1Character, plyer2Character, mode, coins);
                    mainApp.switchToScene(scene);  // Switch to the new scene
                }

                // Stop current background music if applicable
                stopBackgroundMusic();

            } else {
                // Either playerCharacter is null or not selected
                System.out.println("User didn't pick a character before clicking on start");

                // Create a new Alert of type INFORMATION
                Alert alert = new Alert(Alert.AlertType.INFORMATION);

                // Set the title, header text, and content message
                alert.setTitle("Character Selection");
                alert.setHeaderText(null); // No header text
                alert.setContentText("You didn't pick a character!");

                // Show the alert and wait for the user to close it
                alert.showAndWait();
            }
        });






        // Event handlers for selection
        hornetButton.setOnAction(e -> selectCharacter("Hornet", "Knight", primaryStage));
        knightButton.setOnAction(e -> selectCharacter("Knight", "Hornet", primaryStage));




        // Bottom HBox for Back and Start buttons
        HBox bottomBox = new HBox(20, toggleButton,backButton, startButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));

        // Load the background image
        Image backgroundImg = new Image(getClass().getResource("/SelectionDir/charSelectionBK.jpg").toExternalForm());
        ImageView backgroundView = new ImageView(backgroundImg);
        backgroundView.setFitWidth(800);  // Set width of the background to the scene width
        backgroundView.setFitHeight(600);  // Set height of the background to the scene height
        backgroundView.setPreserveRatio(false);

        // Create and configure the BorderPane layout
        BorderPane border = new BorderPane();
        border.setCenter(charBox);     // Center for character selection
        border.setBottom(bottomBox);   // Bottom for Back and Start buttons

        // Add the title at the top center of the BorderPane
        BorderPane.setAlignment(title, Pos.CENTER);  // Ensure the title is centered
        border.setTop(title);

        // Add the background image by placing it in the center of the BorderPane
        border.setStyle("-fx-background-image: url('" + getClass().getResource("/SelectionDir/charSelectionBK.jpg").toExternalForm() + "');"
                + "-fx-background-size: 100% 100%;"); // This ensures the background covers the entire scene

        // Create and return the scene
        Scene selectionScene = new Scene(border, 800, 600);
        return selectionScene;
    }





    // Logic for selecting a character
    private void selectCharacter(String chosenCharacter, String remainingCharacter, Stage primaryStage) {
        plyer1Character = chosenCharacter;
        plyer2Character = remainingCharacter;

        System.out.println("Player selected: " + plyer1Character);
        System.out.println("Computer assigned: " + plyer2Character);

        playClickSound("/MainDir/clickSound.wav");

        // Proceed to the game scene
      //  startGame(primaryStage);
    }



    // Method to add button animations and sound effects
    // THIS METHOD IS SPECIAL FOR THE CHARACTERS BUTTON!!
    // not the original create button!!
    private void addButtonAnimations(Button button) {
        // Scale transition for enlarging the button on hover
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);

        // Scale transition to reset the button size when the hover ends
        ScaleTransition resetScaleTransition = new ScaleTransition(Duration.millis(200), button);
        resetScaleTransition.setToX(1.0);
        resetScaleTransition.setToY(1.0);

        // Drop shadow effect for hover
        DropShadow hoverGlow = new DropShadow();
        hoverGlow.setColor(Color.LIGHTBLUE);
        hoverGlow.setRadius(15);
        hoverGlow.setSpread(0.5);

        // Set mouse enter and exit events for scaling and drop shadow
        button.setOnMouseEntered(e -> {
            scaleTransition.playFromStart();  // Enlarge the button
            button.setEffect(hoverGlow);      // Add glow effect
            playSelectSound("/SelectionDir/selectSound.wav"); // Play sound when touched
        });

        button.setOnMouseExited(e -> {
            resetScaleTransition.playFromStart();  // Reset size
            button.setEffect(null);                // Remove glow effect
        });
    }

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


    // Method to check if a character is selected or not
    public boolean isSelected(String character){
        if(plyer1Character.equals(null))
        return false;
        else
            return true;
    }

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

    // Play sound method using javax.sound.sampled
    public void playSelectSound(String fileName) {
        try {
            // Load the audio file
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(getClass().getResource(fileName));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream); // Open the audio clip
            clip.start();           // Play the sound
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
