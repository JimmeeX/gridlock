package gridlock.view;

import gridlock.model.Board;
import gridlock.model.Difficulty;
import gridlock.model.Mode;
import gridlock.model.SystemSettings;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class GameWinController {
    private SystemSettings settings;
    private Mode mode;
    private Difficulty difficulty;
    private Integer level;

    // Temp
    @FXML
    private AnchorPane wrapper;
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
    private void initialize() {
        this.wrapper.setOpacity(0);
        this.performFadeIn(this.wrapper);
    }

    @FXML
    private void changeSceneControl(ActionEvent event) {
        FadeTransition ft = this.performFadeOut(this.wrapper);
        ft.setOnFinished (fadeEvent -> {
            try {
                Button button = (Button) event.getSource();
                switch (button.getText()) {
                    case "Restart":
                        this.restartLevel(event);
                        break;
                    case "Next Level":
                        this.navToNextLevel(event);
                        break;
                    case "Back":
                        this.navToMenu(event);
                        break;
                    case "Level Select":
                        this.navToLevelSelect(event);
                        break;
                }
            }
            catch (Exception e) {
                System.out.println("Scene Transition Failed");
            }
        });
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
