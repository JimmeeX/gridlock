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
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Handles PlaySettings.fxml. Gets user input for Difficulty and Mode, and changes scenes accordingly.
 * Accessible through Menu -> Play
 * Added by James. Edited by Ian.
 */
public class PlaySettingsController {
    private SystemSettings settings;
    private Difficulty difficulty;
    private Mode mode;
    @FXML
    private ToggleGroup toggleDifficulty;
    @FXML
    private ToggleGroup toggleGameMode;

    @FXML
    private AnchorPane wrapper;

    /**
     * Initialises Settings (mainly for the sounds to work).
     * Set labels in scene
     * Used to pass information between controllers.
     * @param settings Settings for the App.
     */
    public void initData(SystemSettings settings) {
        this.settings = settings;
        ToggleButton selectedDifficulty = (ToggleButton) this.toggleDifficulty.getSelectedToggle();
        this.difficulty = Difficulty.valueOf(selectedDifficulty.getText().toUpperCase());

        ToggleButton selectedMode = (ToggleButton) this.toggleGameMode.getSelectedToggle();
        this.mode = Mode.valueOf(selectedMode.getText().toUpperCase());
    }

    /**
     * Generate a fade in transition when changing scenes.
     * Initialises any listeners
     */
    @FXML
    private void initialize() {
        this.wrapper.setOpacity(0);
        this.performFadeIn(this.wrapper);

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

    /**
     * Handles the Buttons which are responsible for changing scenes.
     * @param event Button Press Event
     */
    @FXML
    private void changeSceneControl(ActionEvent event) {
        FadeTransition ft = this.performFadeOut(this.wrapper);
        ft.setOnFinished (fadeEvent -> {
            try {
                Button button = (Button) event.getSource();
                switch (button.getText()) {
                    case "Play":
                        this.playSettingsControl(event);
                        break;
                    case "Back":
                        this.navToMenu(event);
                        break;
                }
            }
            catch (Exception e) {
                System.out.println(e);
                System.out.println("Scene Transition Failed");
            }
        });
    }

    /**
     * Return back to Menu
     * @param event Button Press Event
     * @throws Exception Any Exception
     */
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

    /**
     * Determines which scene to go. If "CAMPAIGN" go to level select screen. If "SANDBOX" go straight to a game.
     * @param event Start Button Press Event
     * @throws Exception Any Exception thrown during scene transition.
     */
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

    /**
     * Navigates to LevelSelect.fxml screen for CAMPAIGN.
     * @param event Start Button Press Event
     * @param selectedMode User Selected Mode
     * @param selectedDifficulty User Selected Difficulty
     * @throws Exception Any Exception thrown during scene transition.
     */
    private void navToLevelSelect(ActionEvent event, Mode selectedMode, Difficulty selectedDifficulty) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("LevelSelect.fxml"));
        Parent levelSelectParent = loader.load();
        Scene levelSelectScene = new Scene(levelSelectParent);

        LevelSelectController levelSelectController = loader.getController();
        levelSelectController.initData(this.settings, selectedMode, selectedDifficulty);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(levelSelectScene);
    }

    /**
     * Navigates to Game.fxml for SANDBOX.
     * @param event Start Button Press Event
     * @param selectedMode User Selected Mode
     * @param selectedDifficulty User Selected Difficulty
     * @throws Exception Any Exception thrown during scene transition.
     */
    private void navToGame(ActionEvent event, Mode selectedMode, Difficulty selectedDifficulty) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Game.fxml"));
        Parent gameParent = loader.load();
        Scene gameScene = new Scene(gameParent);

        GameController gameController = loader.getController();
        gameController.initData(this.settings, null, selectedMode, selectedDifficulty, 1);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(gameScene);
    }

    /**
     * Fade Out Animation (mostly used for Scene transitioning)
     * @param node The target node to perform Fade Out
     * @return Fade Transition Object
     */
    private FadeTransition performFadeOut(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
        return ft;
    }

    /**
     * Fade In Animation (mostly used for Scene transitioning)
     * @param node The target node to perform Fade In
     * @return Fade Transition Object
     */
    private FadeTransition performFadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        return ft;
    }

    /**
     * Triggered when Mouse enters a Node.
     * Used when mouse enters a button, which will increase the size of the button.
     * Used in conjunction with buttonExitAnimation
     * @param event Mouse Enter Event
     */
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

    /**
     * Triggered when Mouse exits a Node.
     * Used when mouse enters a button, which will increase the size of the button.
     * Used in conjunction with buttonEnterAnimation
     * @param event Mouse Exit Event
     */
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

    /**
     * Plays buttonSound audio when a button is pressed.
     */
    @FXML
    private void playButtonPressSound() {
        this.settings.playButtonPressSound();
    }
}
