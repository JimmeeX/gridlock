package gridlock.view;

import gridlock.model.Difficulty;
import gridlock.model.Mode;
import gridlock.model.SystemSettings;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LevelSelectController {
    private SystemSettings settings;
    private Mode mode;
    private Difficulty difficulty;
    private Integer level;

    @FXML
    private GridPane levels;
    @FXML
    private ToggleGroup toggleLevel;
    @FXML
    private Label modeLabel;
    @FXML
    private Label difficultyLabel;

    public void initData(SystemSettings settings, Mode mode, Difficulty difficulty) {
        this.settings = settings;
        this.mode = mode;
        this.difficulty = difficulty;

        this.modeLabel.setText(mode.toString());
        this.difficultyLabel.setText(difficulty.toString());

        ToggleButton selectedLevel = (ToggleButton) this.toggleLevel.getSelectedToggle();
        this.level = Integer.parseInt(selectedLevel.getText());

        this.applyLevelComplete();
    }

    @FXML
    private void initialize() {
        this.toggleLevel.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (toggleLevel.getSelectedToggle() == null) {
                    oldValue.setSelected(true);
                }
                else {
                    ToggleButton selectedLevel = (ToggleButton) toggleLevel.getSelectedToggle();
                    level = Integer.parseInt(selectedLevel.getText());
                }
            }
        });
    }

    private void applyLevelComplete() {
        Integer[] levelData = this.settings.getLevelComplete(this.difficulty);
        for (int i = 0; i < levelData.length; i++) {
            ToggleButton levelButton = (ToggleButton)this.levels.getChildren().get(i);
            // TODO: Maybe Make Style a bit prettier through css
            switch (levelData[i]) {
                case 0:
                    levelButton.setStyle("-fx-background-color: black");
                    // Paint Incomplete Level
                    break;
                case 1:
                    levelButton.setStyle("-fx-background-color: #D1A163");
                    break;
                case 2:
                    levelButton.setStyle("-fx-background-color: #D2D3D5");
                    break;
                case 3:
                    levelButton.setStyle("-fx-background-color: #F3BA2F");
                    break;
            }
            this.levels.getChildren().set(i, levelButton);
        }
    }

    @FXML
    private void navToPlaySettings(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("PlaySettings.fxml"));
        Parent playSettingsParent = loader.load();
        Scene playSettingsScene = new Scene(playSettingsParent);

        PlaySettingsController playSettingsController = loader.getController();
        playSettingsController.initData(this.settings);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(playSettingsScene);
    }

    @FXML
    private void navToGame(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Game.fxml"));
        Parent gameParent = loader.load();
        Scene gameScene = new Scene(gameParent);

        GameController gameController = loader.getController();
        gameController.initData(this.settings, this.mode, this.difficulty, this.level);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(gameScene);
    }

    @FXML
    private void buttonEnterAnimation(MouseEvent event) {
        Node node = (Node)event.getSource();

        // Increase the Size
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), node);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.1);
        scaleTransition.setToY(1.1);
        scaleTransition.playFromStart();

        node.setCursor(Cursor.HAND);
    }

    @FXML
    private void buttonExitAnimation(MouseEvent event) {
        Node node = (Node)event.getSource();

        // Decrease the Size
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(250), node);
        scaleTransition.setFromX(1.1);
        scaleTransition.setFromY(1.1);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.playFromStart();

        node.setCursor(Cursor.DEFAULT);
    }

    @FXML
    private void playButtonPressSound() {
        this.settings.playButtonPressSound();
    }
}
