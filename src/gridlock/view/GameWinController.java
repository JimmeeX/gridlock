package gridlock.view;

import gridlock.model.Difficulty;
import gridlock.model.GameBoard;
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
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Once a Puzzle has been solved, GameWin.fxml will be called, to show the Win Screen as a popup.
 * Added by James. Edited by Ian.
 */
public class GameWinController {
    private SystemSettings settings;
    private GameBoard prevBoard;
    private Mode mode;
    private Difficulty difficulty;
    private Integer level;

    @FXML
    private AnchorPane wrapper;
    @FXML
    private Canvas canvas;
    @FXML
    private Label minMovesLabel;
    @FXML
    private Label movesLabel;
    @FXML
    private Button nextLevelButton;
    @FXML
    private Button levelSelectButton;

    /**
     * Initialises Settings (mainly for the sounds to work). Used to pass information between controllers.
     * Set Labels
     * Draw Medals
     * @param settings Settings for the App
     * @param prevBoard Board of the solved level, in case the user wants to redo it.
     * @param mode CAMPAIGN or SANDBOX
     * @param difficulty EASY, MEDIUM, or HARD
     * @param level Level Completed
     * @param numMoves Number of Moves to solve the Tutorial puzzle
     * @param minMoves Minimum Number of Moves required to solve the tutorial puzzle
     * @param result Whether a gold, silver, or bronze medal will be rewarded.
     */
    public void initData(SystemSettings settings, GameBoard prevBoard, Mode mode, Difficulty difficulty, Integer level, Integer numMoves, Integer minMoves, Integer result) {
        this.settings = settings;
        this.prevBoard = prevBoard;
        this.mode = mode;
        this.difficulty = difficulty;
        this.level = level;

        this.minMovesLabel.setText("Goal: " + minMoves.toString());
        this.movesLabel.setText("Moves: " + numMoves.toString());

        this.drawMedals(result);

        if (mode.equals(Mode.SANDBOX)) {
            this.levelSelectButton.setDisable(true);
        }

        // If Level 20, then disable nextLevelButton
        if (this.level == 20 && this.mode.equals(Mode.CAMPAIGN)) {
            nextLevelButton.setDisable(true);
        }
    }

    /**
     * Generates a fade in transition
     */
    @FXML
    private void initialize() {
        this.wrapper.setOpacity(0);
        this.performFadeIn(this.wrapper);
    }

    /**
     * Draws Image Medals on a canvas to display in the winScreen. Will draw either Gold, Silver or Bronze medal
     * based on performance.
     * @param result 1: Bronze; 2: Silver 3: Gold
     */
    private void drawMedals(Integer result) {
        Image medalImage = new Image("file:src/gridlock/static/images/medals.png");
        switch (result) {
            case 1:
                medalImage = new Image("file:src/gridlock/static/images/medal_bronze.png");
                break;
            case 2:
                medalImage = new Image("file:src/gridlock/static/images/medal_silver.png");
                break;
            case 3:
                medalImage = new Image("file:src/gridlock/static/images/medal_gold.png");
                break;
        }
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        gc.drawImage(medalImage, 25, 75, 100, 200);
        gc.drawImage(medalImage, 500, 75, 100, 200);
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
                System.out.println(e);
                System.out.println("Scene Transition Failed");
            }
        });
    }

    /**
     * Go play the next level.
     * @param event Next Level Button Press Event
     * @throws Exception Any exception thrown when scene transition fails.
     */
    @FXML
    private void navToNextLevel(ActionEvent event) throws Exception {
        // Restart Level but for the next level
        // On owner stage, reload the same level
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Game.fxml"));
        Parent gameParent = loader.load();
        Scene gameScene = new Scene(gameParent);

        GameController gameController = loader.getController();
        gameController.initData(this.settings, null, this.mode, this.difficulty, this.level + 1);

        Stage popupWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage owner = (Stage)popupWindow.getOwner();
        owner.setScene(gameScene);

        // Close the popup window -> return to the game with a new board state
        popupWindow.close();

    }

    /**
     * Go Back to Level Select Screen (only for CAMPAIGN)
     * @param event Level Select Button Press Event
     * @throws Exception Any exception thrown when scene transition fails.
     */
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

    /**
     * Go through the level again.
     * @param event Restart Button Press Event
     * @throws Exception Any exception thrown when scene transition fails.
     */
    @FXML
    private void restartLevel(ActionEvent event) throws Exception {
        // On owner stage, reload the same level
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Game.fxml"));
        Parent gameParent = loader.load();
        Scene gameScene = new Scene(gameParent);

        GameController gameController = loader.getController();
        gameController.initData(this.settings, this.prevBoard, this.mode, this.difficulty, this.level);

        Stage popupWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage owner = (Stage)popupWindow.getOwner();
        owner.setScene(gameScene);

        // Close the popup window -> return to the game with a new board state
        popupWindow.close();
    }

    /**
     * Return back to Menu
     * @param event Back Button Press Event
     * @throws Exception Any exception thrown when scene transition fails.
     */
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
