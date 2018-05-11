package gridlock.view;

import gridlock.model.Difficulty;
import gridlock.model.Mode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class LevelSelectController {
    private Mode mode;
    private Difficulty difficulty;

    @FXML
    private ToggleGroup toggleLevel;
    @FXML
    private Label modeLabel;
    @FXML
    private Label difficultyLabel;

    public void initData(Mode mode, Difficulty difficulty) {
        this.mode = mode;
        this.difficulty = difficulty;

        this.modeLabel.setText(mode.toString());
        this.difficultyLabel.setText(difficulty.toString());
    }

    @FXML
    private void navToPlaySettings(ActionEvent event) throws Exception {
        Parent playSettingsParent = FXMLLoader.load(getClass().getResource("PlaySettings.fxml"));
        Scene playSettingsScene = new Scene(playSettingsParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(playSettingsScene);
    }

    @FXML
    private void levelSelectControls(ActionEvent event) throws Exception {
//        try {
        Integer selectedLevel = this.getLevel();
        this.navToGame(event, selectedLevel);
//        }

//        catch (NullPointerException e) {
//            System.out.println("Please selected a level");
//        }
    }

    private void navToGame(ActionEvent event, Integer selectedLevel) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Game.fxml"));
        Parent gameParent = loader.load();
        Scene gameScene = new Scene(gameParent);

        GameController gameController = loader.getController();
        gameController.initData(this.mode, this.difficulty, selectedLevel);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(gameScene);
    }

    private Integer getLevel() {
        ToggleButton selectedLevel = (ToggleButton) toggleLevel.getSelectedToggle();
        return Integer.parseInt(selectedLevel.getText());
    }
}
