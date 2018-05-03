package gridlock.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import javax.swing.*;

public class MenuController {
    private String difficulty;
    private String gameMode;
    private String level;
    @FXML
    private ToggleGroup toggleDifficulty;
    @FXML
    private ToggleGroup toggleGameMode;
    @FXML
    private ToggleGroup toggleLevel;

    @FXML
    private void navToMenu(ActionEvent event) throws Exception {
        Parent menuParent = FXMLLoader.load(getClass().getResource("../templates/Menu.fxml"));
        Scene menuScene = new Scene(menuParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(menuScene);
    }

    @FXML
    private void navToPlaySettings(ActionEvent event) throws Exception {
        Parent playSettingsParent = FXMLLoader.load(getClass().getResource("../templates/PlaySettings.fxml"));
        Scene playSettingsScene = new Scene(playSettingsParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(playSettingsScene);
    }

    @FXML
    private void playSettingsControl(ActionEvent event) throws Exception {
        // TODO: Modify ToggleButton so that something is always selected. (So try/catch is not needed)
        try {
//            String selectedDifficulty = getDifficulty();
//            String selectedGameMode = getGameMode();

            if (this.gameMode.equals("Campaign")) {
                // Load Game Menu
                navToLevelSelect(event, this.difficulty);
            }
            else {
                assert this.gameMode.equals("Sandbox");
                navToGame(event, this.difficulty);
            }
        }
        catch (NullPointerException e) {
            System.out.println("Please specify a difficulty and game mode.");
        }
    }

    // TODO: Handle Mode
    private void navToLevelSelect(ActionEvent event, String difficultyMode) throws Exception {
        Parent levelSelectParent = FXMLLoader.load(getClass().getResource("../templates/LevelSelect.fxml"));
        Scene levelSelectScene = new Scene(levelSelectParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(levelSelectScene);
    }

    // TODO: Handle Mode
    private void navToGame(ActionEvent event, String difficultyMode) throws Exception {
        Parent gameParent = FXMLLoader.load(getClass().getResource("../templates/Game.fxml"));
        Scene gameScene = new Scene(gameParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(gameScene);
    }

    // TODO: Handle Mode
    @FXML
    private void levelSelectControls(ActionEvent event) throws Exception{
        try {
//            String selectedLevel = getLevel();
//            String selectedDifficulty = getDifficulty();
            System.out.println(this.level);
            System.out.println(this.difficulty);
            navToGame(event, this.difficulty);
        }
        catch (NullPointerException e) {
            System.out.println("Please select a level");
        }

    }

    @FXML
    private void navToSettings(ActionEvent event) throws Exception {
        Parent settingsParent = FXMLLoader.load(getClass().getResource("../templates/Settings.fxml"));
        Scene settingsScene = new Scene(settingsParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(settingsScene);
    }

    @FXML
    private void navToHelp(ActionEvent event) throws Exception {
        Parent helpParent = FXMLLoader.load(getClass().getResource("../templates/Help.fxml"));
        Scene helpScene = new Scene(helpParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(helpScene);
    }

    @FXML
    private void navToAbout(ActionEvent event) throws Exception {
        Parent aboutParent = FXMLLoader.load(getClass().getResource("../templates/About.fxml"));
        Scene aboutScene = new Scene(aboutParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(aboutScene);
    }

    @FXML
    private void quitGame(ActionEvent event) {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
        System.out.println("You pressed Quit");
    }

    // Helpers
//    @FXML
    private void setDifficulty() {
        ToggleButton selectedDifficulty = (ToggleButton) toggleDifficulty.getSelectedToggle();
        this.difficulty = selectedDifficulty.getText();
        System.out.println(this.difficulty);
    }
//    @FXML
    private void setGameMode() {
        ToggleButton selectedGameMode = (ToggleButton) toggleGameMode.getSelectedToggle();
        this.gameMode = selectedGameMode.getText();
    }
//    @FXML
    private void setLevel() {
        ToggleButton selectedLevel = (ToggleButton) toggleLevel.getSelectedToggle();
        this.level = selectedLevel.getText();
        System.out.println(this.difficulty);
        System.out.println(this.level);
    }
}