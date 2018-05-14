package gridlock.view;

import gridlock.model.Board;
import gridlock.model.Difficulty;
import gridlock.model.Mode;
import gridlock.model.SystemSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.swing.*;

public class GameWinController {
    private SystemSettings settings;
    private Mode mode;
    private Difficulty difficulty;
    private Integer level;

    // Temp
    @FXML
    private Label starsLabel;
    @FXML
    private Label movesLabel;
    @FXML
    private Button nextLevelButton;

    public void initData(SystemSettings settings, Mode mode, Difficulty difficulty, Integer level, Integer numMoves) {
        this.settings = settings;
        this.mode = mode;
        this.difficulty = difficulty;
        this.level = level;

        this.starsLabel.setText("3 Stars");
        this.movesLabel.setText("Moves: " + numMoves.toString());

        // Save Information

        // If Level 20, then disable nextLevelButton
        if (this.level == 20) {
            nextLevelButton.setDisable(true);
        }
    }

    @FXML
    private void navToNextLevel(ActionEvent event) throws Exception {
        // Restart Level but for the next level
        // On owner stage, reload the same level
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Game.fxml"));
        Parent gameParent = loader.load();
        Scene gameScene = new Scene(gameParent);

        GameController gameController = loader.getController();
        gameController.initData(this.settings, this.mode, this.difficulty, this.level + 1);

        Stage popupWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage owner = (Stage)popupWindow.getOwner();
        owner.setScene(gameScene);

        // Close the popup window -> return to the game with a new board state
        popupWindow.close();

    }

    @FXML
    private void navToLevelSelect(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("LevelSelect.fxml"));
        Parent levelSelectParent = loader.load();
        Scene levelSelectScene = new Scene(levelSelectParent);

        LevelSelectController levelSelectController = loader.getController();
        levelSelectController.initData(this.settings, this.mode, this.difficulty);

        Stage popupWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage owner = (Stage)popupWindow.getOwner();
        owner.setScene(levelSelectScene);

        popupWindow.close();
    }

    @FXML
    private void restartLevel(ActionEvent event) throws Exception {
        // On owner stage, reload the same level
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Game.fxml"));
        Parent gameParent = loader.load();
        Scene gameScene = new Scene(gameParent);

        GameController gameController = loader.getController();
        gameController.initData(this.settings, this.mode, this.difficulty, this.level);

        Stage popupWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage owner = (Stage)popupWindow.getOwner();
        owner.setScene(gameScene);

        // Close the popup window -> return to the game with a new board state
        popupWindow.close();
    }

    @FXML
    private void navToMenu(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Menu.fxml"));
        Parent menuParent = loader.load();
        Scene menuScene = new Scene(menuParent);

        MenuController menuController = loader.getController();
        menuController.initData(this.settings);

        Stage popupWindow = (Stage)((Node)event.getSource()).getScene().getWindow();
        Stage owner = (Stage)popupWindow.getOwner();
        owner.setScene(menuScene);

        // Close popup
        popupWindow.close();
    }

    @FXML
    private void playButtonPressSound() {
        this.settings.playButtonPressSound();
    }
}
