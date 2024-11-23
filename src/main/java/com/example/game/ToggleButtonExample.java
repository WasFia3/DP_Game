package com.example.game;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class ToggleButtonExample extends Application {
    private Clip clip; // Clip instance to control playback

    @Override
    public void start(Stage primaryStage) {
        // Create a ToggleButton and set it to selected by default (ON state)
        ToggleButton toggleButton = new ToggleButton("Toggle Music");
        toggleButton.setSelected(true); // Set the toggle button to "ON" by default

        // Create a Label to display the toggle state
        Label statusLabel = new Label("Music is ON");

        // Start the background music initially
        playBackgroundMusic("/MainDir/introMusic.wav");

        // Set an action for when the ToggleButton's state changes
        toggleButton.setOnAction(event -> {
            if (toggleButton.isSelected()) {
                statusLabel.setText("Music is ON");
                playBackgroundMusic("/MainDir/introMusic.wav"); // Start music
            } else {
                statusLabel.setText("Music is OFF");
                stopBackgroundMusic(); // Stop the music
            }
        });

        // Set up the layout
        VBox root = new VBox(10, toggleButton, statusLabel);
        root.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Set up the Scene and Stage
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("JavaFX ToggleButton Example");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    // Play background music from the beginning each time
    public void playBackgroundMusic(String musicFilePath) {
        stopBackgroundMusic(); // Ensure any existing clip is stopped before starting a new one
        try {
            // Load the sound file from resources
            File musicFile = new File(getClass().getResource(musicFilePath).getFile());
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);

            // Set to loop indefinitely
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
            clip.close(); // Release resources to reset state
        }
    }
}
