package gridlock.view;

import gridlock.model.Difficulty;
import gridlock.model.Mode;
import gridlock.model.SystemSettings;
import javafx.animation.FadeTransition;
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
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class LevelSelectController {
    private SystemSettings settings;
    private Mode mode;
    private Difficulty difficulty;
    private Integer level;

    @FXML
    private AnchorPane wrapper;
    @FXML
    private GridPane levels;
    @FXML
    private ToggleGroup toggleLevel;
    @FXML
    private Label modeLabel;
    @FXML
    private Label difficultyLabel;
    @FXML
    private Label levelLabel;

    public void initData(SystemSettings settings, Mode mode, Difficulty difficulty) {
        this.settings = settings;
        this.mode = mode;
        this.difficulty = difficulty;

        this.modeLabel.setText(mode.toString());
        this.difficultyLabel.setText(difficulty.toString());

        ToggleButton selectedLevel = (ToggleButton) this.toggleLevel.getSelectedToggle();
        this.level = Integer.parseInt(selectedLevel.getText());
        this.levelLabel.setText(selectedLevel.getText());

        this.applyLevelComplete();
    }

    @FXML
    private void initialize() {
        this.wrapper.setOpacity(0);
        this.performFadeIn(this.wrapper);

        this.toggleLevel.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            @Override
            public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
                if (toggleLevel.getSelectedToggle() == null) {
                    oldValue.setSelected(true);
                }
                else {
                    ToggleButton selectedLevel = (ToggleButton) toggleLevel.getSelectedToggle();
                    level = Integer.parseInt(selectedLevel.getText());
                    levelLabel.setText(selectedLevel.getText());
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
                    levelButton.setStyle("-fx-background-color: linear-gradient(to bottom right, #e8a069 0%, #edbc91 22%, #f7f4f0 43%, #e07430); -fx-text-fill: black");
                    break;
                case 2:
                    levelButton.setStyle("-fx-background-color: linear-gradient(to bottom right, #bdbdc2 0%, #c8c8cc 22%, #fbfbfb 43%, #98979e); -fx-text-fill: black");
                    break;
                case 3:
                    levelButton.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffe263 0%, #ffefaf 22%, #fffcf1 43%, #fdb307); -fx-text-fill: black");
                    break;
            }
            this.levels.getChildren().set(i, levelButton);
        }
    }

    @FXML
    private void changeSceneControl(ActionEvent event) {
        FadeTransition ft = this.performFadeOut(this.wrapper);
        ft.setOnFinished (fadeEvent -> {
            try {
                Button button = (Button) event.getSource();
                switch (button.getText()) {
                    case "Start":
                        this.navToGame(event);
                        break;
                    case "Back":
                        this.navToPlaySettings(event);
                        break;
                }
            }
            catch (Exception e) {
                System.out.println(e);
                System.out.println("Scene Transition Failed");
            }
        });
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
        gameController.initData(this.settings, null, this.mode, this.difficulty, this.level);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(gameScene);
    }

    @FXML
    private void levelEnter(MouseEvent event) {
        Node node = (Node)event.getSource();
        node.setCursor(Cursor.HAND);
    }

    @FXML
    private void levelExit(MouseEvent event) {
        Node node = (Node)event.getSource();
        node.setCursor(Cursor.DEFAULT);
    }

    private FadeTransition performFadeOut(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
        return ft;
    }

    private FadeTransition performFadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        return ft;
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
