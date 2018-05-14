package gridlock.view;

import gridlock.model.Difficulty;
import gridlock.model.Mode;
import gridlock.model.SystemSettings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class PlaySettingsController {
    private SystemSettings settings;
    private Difficulty difficulty;
    private Mode mode;
    @FXML
    private ToggleGroup toggleDifficulty;
    @FXML
    private ToggleGroup toggleGameMode;

    public void initData(SystemSettings settings) {
        this.settings = settings;

        ToggleButton selectedDifficulty = (ToggleButton) this.toggleDifficulty.getSelectedToggle();
        this.difficulty = Difficulty.valueOf(selectedDifficulty.getText().toUpperCase());

        ToggleButton selectedMode = (ToggleButton) this.toggleGameMode.getSelectedToggle();
        this.mode = Mode.valueOf(selectedMode.getText().toUpperCase());
    }

    @FXML
    private void initialize() {
        this.toggleDifficulty.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (toggleDifficulty.getSelectedToggle() == null) {
                    oldValue.setSelected(true);
                }
                else {
                    ToggleButton selectedDifficulty = (ToggleButton)toggleDifficulty.getSelectedToggle();
                    difficulty = Difficulty.valueOf(selectedDifficulty.getText().toUpperCase());
                }
            }
        });

        this.toggleGameMode.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (toggleGameMode.getSelectedToggle() == null) {
                    oldValue.setSelected(true);
                }
                else {
                    ToggleButton selectedMode = (ToggleButton) toggleGameMode.getSelectedToggle();
                    mode = Mode.valueOf(selectedMode.getText().toUpperCase());
                }
            }
        });
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
        if (this.mode == Mode.CAMPAIGN) {
            navToLevelSelect(event, this.mode, this.difficulty);
        } else {
            assert this.mode == Mode.SANDBOX;
            navToGame(event, this.mode, this.difficulty);
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

    @FXML
    private void playButtonPressSound() {
        this.settings.playButtonPressSound();
    }
}
