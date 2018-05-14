package gridlock.view;

import gridlock.model.Difficulty;
import gridlock.model.Mode;
import gridlock.model.SystemSettings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class PlaySettingsController {
    private SystemSettings settings;
    @FXML
    private ToggleGroup toggleDifficulty;
    @FXML
    private ToggleGroup toggleGameMode;

    public void initData(SystemSettings settings) {
        this.settings = settings;
    }

    @FXML
    private void navToMenu(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Menu.fxml"));
        Parent menuParent = loader.load();
        Scene menuScene = new Scene(menuParent);

        MenuController menuController = loader.getController();
        menuController.initData(this.settings);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(menuScene);
    }

    @FXML
    private void playSettingsControl(ActionEvent event) throws Exception {
        // Get Toggle Button Values
        try {
            Mode selectedMode = this.getMode();
            Difficulty selectedDifficulty = this.getDifficulty();

            if (selectedMode == Mode.CAMPAIGN) {
                navToLevelSelect(event, selectedMode, selectedDifficulty);
            } else {
                assert selectedMode == Mode.SANDBOX;
                navToGame(event, selectedMode, selectedDifficulty);
            }
        }

        catch (NullPointerException e) {
            System.out.println("Please select a mode and difficulty!");
        }
    }

    private void navToLevelSelect(ActionEvent event, Mode selectedMode, Difficulty selectedDifficulty) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("LevelSelect.fxml"));
        Parent levelSelectParent = loader.load();
        Scene levelSelectScene = new Scene(levelSelectParent);

        LevelSelectController levelSelectController = loader.getController();
        System.out.println(this.settings);
        levelSelectController.initData(this.settings, selectedMode, selectedDifficulty);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(levelSelectScene);
    }

    private void navToGame(ActionEvent event, Mode selectedMode, Difficulty selectedDifficulty) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Game.fxml"));
        Parent gameParent = loader.load();
        Scene gameScene = new Scene(gameParent);

        GameController gameController = loader.getController();
        gameController.initData(this.settings, selectedMode, selectedDifficulty, 1);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(gameScene);
    }

    private Mode getMode() {
        ToggleButton selectedMode = (ToggleButton) toggleGameMode.getSelectedToggle();
        return Mode.valueOf(selectedMode.getText().toUpperCase());
    }

    private Difficulty getDifficulty() {
        ToggleButton selectedDifficulty = (ToggleButton) toggleDifficulty.getSelectedToggle();
        return Difficulty.valueOf(selectedDifficulty.getText().toUpperCase());
    }

    @FXML
    private void playButtonPressSound() {
        this.settings.playButtonPressSound();
    }
}
