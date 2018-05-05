package gridlock.view;

import com.sun.org.apache.xpath.internal.operations.Mod;
import gridlock.model.Difficulty;
import gridlock.model.Mode;
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
    @FXML
    private ToggleGroup toggleDifficulty;
    @FXML
    private ToggleGroup toggleGameMode;
    @FXML
    private void navToMenu(ActionEvent event) throws Exception {
        Parent menuParent = FXMLLoader.load(getClass().getResource("Menu.fxml"));
        Scene menuScene = new Scene(menuParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(menuScene);
    }
    @FXML
    private void playSettingsControl(ActionEvent event) throws Exception {
        // Get Toggle Button Values
        try {
            Mode selectedMode = this.getMode();
            Difficulty selectedDifficulty = this.getDifficulty();
//            ToggleButton selectedGameMode = (ToggleButton) toggleGameMode.getSelectedToggle();
//            String selectedGameModeValue = selectedGameMode.getText();
//            ToggleButton selectedDifficulty = (ToggleButton) toggleDifficulty.getSelectedToggle();
//            String selectedDifficultyValue = selectedDifficulty.getText();

            if (selectedMode == Mode.CAMPAIGN) {
                navToLevelSelect(event, selectedMode, selectedDifficulty);
            } else {
                assert selectedMode == Mode.SANDBOX;
                navToGame(event, selectedMode, selectedDifficulty);
            }
        }

        catch (NullPointerException e) {
            //TODO: Or perhaps an alert
            System.out.println("Please select a mode and difficulty!");
        }
    }

    private void navToLevelSelect(ActionEvent event, Mode selectedMode, Difficulty selectedDifficulty) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("LevelSelect.fxml"));
        Parent levelSelectParent = loader.load();
        Scene levelSelectScene = new Scene(levelSelectParent);

        LevelSelectController levelSelectController = loader.getController();
        levelSelectController.initData(selectedMode, selectedDifficulty);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(levelSelectScene);
    }

    private void navToGame(ActionEvent event, Mode selectedMode, Difficulty selectedDifficulty) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Game.fxml"));
        Parent gameParent = loader.load();
        Scene gameScene = new Scene(gameParent);

        GameController gameController = loader.getController();
        gameController.initData(selectedMode, selectedDifficulty, -1);

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

//        this.game.setDifficulty(selectedDifficulty.getText());
//        // TODO: Modify ToggleButton so that something is always selected. (So try/catch is not needed)
//        try {
//            this.setGameMode();
//            this.setDifficulty();
//            System.out.println(this.game);
//            if (this.game.getMode() == Mode.CAMPAIGN) {
//                // Load Game Menu
//                navToLevelSelect(event);
//            }
//            else {
//                assert this.game.getMode() == Mode.SANDBOX;
//                navToGame(event);
//            }
//        }
//        catch (NullPointerException e) {
//            System.out.println("Please specify a difficulty and game mode.");
//        }
//    }

}
