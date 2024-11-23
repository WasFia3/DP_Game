package com.example.game;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class GameScene {

    // Method to create the game scene
    public Scene createScene(Stage primaryStage, String playerCharacter, String computerCharacter) {

        // Title of the game
        Label gameTitle = new Label("Battle Arena");
        gameTitle.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Player character display
        //Image playerImg = new Image(getClass().getResource("/GameDir/" + playerCharacter + ".png").toExternalForm());
        //ImageView playerView = new ImageView(playerImg);
        //playerView.setFitWidth(200);
        //playerView.setFitHeight(300);
       // playerView.setPreserveRatio(true);

        Label playerLabel = new Label("Player: " + playerCharacter);
        playerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox playerBox = new VBox(10, playerLabel);
        playerBox.setAlignment(Pos.CENTER);

        // Computer character display
        //Image computerImg = new Image(getClass().getResource("/GameDir/" + computerCharacter + ".png").toExternalForm());
        //ImageView computerView = new ImageView(computerImg);
        //computerView.setFitWidth(200);
        //computerView.setFitHeight(300);
        //computerView.setPreserveRatio(true);

        Label computerLabel = new Label("Computer: " + computerCharacter);
        computerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        VBox computerBox = new VBox(10, computerLabel);
        computerBox.setAlignment(Pos.CENTER);

        // Create HBox to position player and computer visuals
        HBox battleBox = new HBox(100, playerBox, computerBox);
        battleBox.setAlignment(Pos.CENTER);
        battleBox.setPadding(new Insets(50));

        // Message label (game instructions or status)
        Label messageLabel = new Label("Get ready for the battle!");
        messageLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");

        // VBox for title and message
        VBox topBox = new VBox(20, gameTitle, messageLabel);
        topBox.setAlignment(Pos.CENTER);

        // BorderPane layout
        BorderPane border = new BorderPane();
        border.setTop(topBox);
        border.setCenter(battleBox);
       // border.setStyle("-fx-background-image: url('" + getClass().getResource("/GameDir/gameBackground.jpg").toExternalForm() + "');"
             //   + "-fx-background-size: 100% 100%;");

        // Create and return the scene
        Scene gameScene = new Scene(border, 800, 600);
        return gameScene;
    }
}
