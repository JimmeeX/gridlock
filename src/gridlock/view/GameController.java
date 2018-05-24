package gridlock.view;

import gridlock.model.*;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;

/**
 * The actual Game Interface. Contains a board, draggable blocks, undo, redo, hint, reset, and buttons to go back.
 * Accessible through Menu -> Play -> (Choose Mode/Difficulty/Level)
 */
public class GameController {
    private SystemSettings settings;
    private GameBoard board;
    private Integer minMoves;
    private Integer result;
    private Mode mode;
    private Difficulty difficulty;
    private Integer level;
    private ArrayList<Node> recNodeList;
    private ArrayList<MouseGestures> mgList;

    private Service<Void> solverThread;
    private Block solverBlock;

    @FXML
    private AnchorPane wrapper;
    @FXML
    private Label modeLabel;
    @FXML
    private Label difficultyLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label movesLabel;
    @FXML
    private Label minMovesLabel;
    @FXML
    private AnchorPane primaryField;
    @FXML
    private Pane boardField;
    @FXML
    private Button nextButton;
    @FXML
    private Polygon goalArrow;
    @FXML
    private Button undoButton;
    @FXML
    private Button redoButton;
    @FXML
    private Button hintButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button levelSelectButton;
    @FXML
    private Button quitButton;

    /**
     * Initialises Settings (mainly for the sounds to work). Used to pass information between controllers.
     * Initialises GameBoard
     * Initialise Solver Thread
     * Draws Blocks and Places them on Board.
     * Adds Mouse Gestures to the Blocks
     * Initialises any animations
     * @param settings Settings for the App.
     * @param oldBoard Board of the most recent completed level. (if user wants to restart the level after completion)
     * @param mode CAMPAIGN; SANDBOX
     * @param difficulty EASY; MEDIUM; HARD
     * @param level Level Number
     */
    public void initData(SystemSettings settings, GameBoard oldBoard, Mode mode, Difficulty difficulty, Integer level) {
        // Initialise Variables
        this.settings = settings;
        this.mode = mode;
        this.difficulty = difficulty;
        this.level = level;

        this.modeLabel.setText(this.mode.toString());
        this.difficultyLabel.setText(this.difficulty.toString());
        this.levelLabel.setText("Level " + this.level.toString());
        this.movesLabel.setText("Moves: 0");

        if (this.mode.equals(Mode.SANDBOX)) {
            this.levelSelectButton.setDisable(true);
        }

        // Initialise Board
        this.initBoard(oldBoard);

        this.board.setMinMoves();
        this.minMoves = this.board.getMinMoves();
        System.out.println(minMoves);
        this.minMovesLabel.setText("Goal: " + this.minMoves);

        // Initialise Board Solver Thread
        this.initSolver();

        // Draw the Rectangles and add it to the Board
        this.initNodeList();

        // Add Drag/Drop Functionality to the Rectangles
        this.initMouseGestures();

        // Add Animations
        this.initAnimations();
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
     * Generates a board with added listeners.
     * CAMPAIGN will choose predefined board.
     * SANDBOX will auto-generate a board.
     */
    private void initBoard(GameBoard oldBoard) {
        if (oldBoard != null) {
            oldBoard.restart();
            this.board = oldBoard.duplicateGridandBlocks();
        }

        else {
            this.board = new GameBoard();
            if (mode.equals(Mode.CAMPAIGN)) {
                // Read Board from File
                String levelName = "src/gridlock/resources/" + this.difficulty.toString().toLowerCase() + "/" + this.level.toString() + ".txt";
                this.board.process(levelName);
            }
            else {
                if (this.difficulty.equals(Difficulty.EASY)) this.board = this.settings.getEasy();
                else if (this.difficulty.equals(Difficulty.MEDIUM)) this.board = this.settings.getMedium();
                else this.board = this.settings.getHard();
            }
        }

        // Add Listener for Win Game Condition
        this.board.gameStateProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    disableGameButtons();
                    nextButton.setDisable(false);
                    handleWin();
                    animateWinSequence();
                }
                else {
                    nextButton.setDisable(true);
                }
            }
        });

    }

    /**
     * Initialises a thread to check for hints.
     */
    private void initSolver() {
        // Initialise Solver in the background
        this.solverThread = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        solverBlock = board.getHint(false);
                        return null;
                    }
                };
            }
        };

        // Add Listener for Board Moves
        this.board.numMovesProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                movesLabel.setText("Moves: " + newValue.toString());
                solverThread.cancel();
                solverThread.restart();
            }
        });

        // Run the solver for the first time with the Initial Board state
        this.solverThread.restart();
    }

    /**
     * Draws Rectangles using the board attribute and adds them to the Pane.
     */
    private void initNodeList() {
        this.recNodeList = new ArrayList<>();
        // Draw Rectangles and add to Pane (so Pane is its Parent).
        for (Block block: this.board.getBlocks()) {
            Rectangle rec = new Rectangle(0, 0);
            rec.setUserData(block.getID());
            if (block.getID().equals("z")) {
                rec.setId("player");
            } else {
                rec.setId("obstacles");
            }
            setBlocks(block, rec);
            this.recNodeList.add(rec);
        }
    }

    /**
     * Adds Drag/Drop Functionality to the Rectangles.
     */
    private void initMouseGestures() {
        ArrayList<Block> blockL = this.board.getBlocks();
        this.mgList = new ArrayList<>();
        for(int i = 0; i < blockL.size(); i++) {
            Node currNode = this.recNodeList.get(i);
            if (blockL.get(i).isHorizontal()) {
                MouseGestures hmg = new MouseGestures(this.settings, blockL.get(i).getID(), this.board, this.boardField, this.board.getGridSize(), this.board.getGridSize(), true, currNode, this.recNodeList);
                hmg.makeDraggable(recNodeList.get(i));
                this.mgList.add(hmg);
            } else {
                MouseGestures vmg = new MouseGestures(this.settings, blockL.get(i).getID(), this.board, this.boardField, this.board.getGridSize(), this.board.getGridSize(), false, currNode, this.recNodeList);
                vmg.makeDraggable(recNodeList.get(i));
                this.mgList.add(vmg);
            }
            this.boardField.getChildren().addAll(this.recNodeList.get(i));
        }
    }

    /**
     * Add any on-going animations for the game.
     */
    private void initAnimations() {
        this.pulse(this.goalArrow);
    }

    /**
     * Updates the board when a non-user move is made (ie, undo, redo, reset).
     */
    private void updateBoard() {
        this.disableGameButtons();
        ArrayList<Block> blockList = this.board.getBlocks();
        for (int i = 0; i < blockList.size(); i++) {
            Block block = blockList.get(i);

            // Retrieve the Rectangle and Update it with new position
            MouseGestures mg = this.mgList.get(i);

            // Pane Size
            int widthFactor = 450 / this.board.getGridSize();
            int heightFactor = 450 / this.board.getGridSize();

            TranslateTransition tt;
            if (block.isHorizontal()) {
                double startCol = block.getCol()*widthFactor;
                tt = mg.animateMoveNodeX(startCol);
            }

            else {
                double startRow = block.getRow()*heightFactor;
                tt = mg.animateMoveNodeY(startRow);
            }
            this.mgList.set(i, mg);
            tt.setOnFinished(event -> {
                this.enableGameButtons();
            });
        }
    }

    /**
     * Helper Function. Converts Block information from backend to Node information on frontend.
     * @param b Block
     * @param node Node
     */
    private void setBlocks(Block b, Node node) {
        Rectangle rec = (Rectangle)node;
        int height, width, startRow, startCol;

        // Pane Size
        int widthFactor = 450 / this.board.getGridSize();
        int heightFactor = 450 / this.board.getGridSize();

        if(b.isHorizontal()){
            height = heightFactor;
            width = widthFactor*b.getSize();
        } else {
            height = heightFactor*b.getSize();
            width = widthFactor;
        }
        startRow = b.getRow()*heightFactor;
        startCol = b.getCol()*widthFactor;

        rec.setHeight(height);
        rec.setWidth(width);
        rec.setX(startCol);
        rec.setY(startRow);
        rec.setTranslateX(0);
        rec.setTranslateY(0);

        setEffects(rec);
    }

    /**
     * Function to set effects to the rectangle.
     * @param rec Rectangle/Block on the Pane
     */
    private void setEffects(Rectangle rec) {
        rec.setEffect(new BoxBlur());

        // Add Effects
        InnerShadow effect = new InnerShadow();
        rec.setEffect(effect);
    }

    /**
     * Determines what medal is deserved from the user performance.
     * Saves Level State (CAMPAIGN ONLY)
     */
    private void handleWin() {
        if (this.board.numMovesProperty().getValue() == this.minMoves) {
            this.result = 3;
        }
        else if (board.numMovesProperty().getValue() <= Math.round(this.minMoves * 1.3)) {
            this.result = 2;
        } else {
            this.result = 1;
        }
        if (this.mode.equals(Mode.CAMPAIGN)) {
            this.settings.setLevelComplete(this.difficulty, this.level, this.result);
        }
    }

    /**
     * Animation sequence for the player block to move to the right-most side once the game is solved.
     */
    private void animateWinSequence() {
        // Get the player node
        this.disableAllButtons();
        for (Node node:this.recNodeList) {
            if (node.getUserData().equals("z")) {
                // Make BoardField Object Invisible
                node.setVisible(false);

                Bounds bounds = node.localToScene(node.getBoundsInLocal());
                // Create Identical Rectangle but in the primary Stage.
                Rectangle animatedRectangle = new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
                animatedRectangle.setId("player");
                setEffects(animatedRectangle);
                this.primaryField.getChildren().add(animatedRectangle);

                // Animation Sequence
                final Timeline timeline = new Timeline();
                timeline.setCycleCount(1);
                final KeyValue kv = new KeyValue(animatedRectangle.xProperty(), this.primaryField.getWidth());
                final KeyFrame kf = new KeyFrame(Duration.millis(1000), kv);
                timeline.getKeyFrames().add(kf);
                timeline.play();
                timeline.setOnFinished(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        nextButton.fire();
                    }
                });
            }
        }
    }

    /**
     * Animation. Fade in/out indefinitely to a specified Node.
     * @param node Target Node for animation
     * @return FadeTransition object
     */
    private FadeTransition pulse(Node node){
        FadeTransition ft = new FadeTransition(Duration.millis(1500), node);

        ft.setFromValue(1);//Specifies the start opacity value for this FadeTransition
        ft.setToValue(0);
        ft.setCycleCount(Timeline.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();
        return ft;
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
                    case "Levels":
                        this.navToLevelSelect(event);
                        break;
                    case "Quit":
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
     * Shows GameWin Scene once Level is Solved.
     * @param event Button Press Event
     * @throws Exception Any Exception thrown when Scene Transition fails
     */
    @FXML
    private void showGameWin(ActionEvent event) throws Exception {
        this.playVictorySound();
        // Initialise Popup Stage
        Stage gameWinStage = new Stage();
        gameWinStage.initStyle(StageStyle.UNDECORATED);
        gameWinStage.initModality(Modality.APPLICATION_MODAL);
        Stage owner = (Stage)((Node)event.getSource()).getScene().getWindow();
        gameWinStage.initOwner(owner);

        // Load Screen
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GameWin.fxml"));
        Parent gameWinParent = loader.load();
        Scene gameWinScene = new Scene(gameWinParent);

        // Attach Controller
        GameWinController gameWinController = loader.getController();
        gameWinController.initData(this.settings, this.board, this.mode, this.difficulty, this.level, this.board.numMovesProperty().getValue(), this.minMoves, this.result);

        gameWinStage.setScene(gameWinScene);

        gameWinStage.setX(owner.getX() + (owner.getWidth() - 600) / 2);
        gameWinStage.setY(owner.getY() + (owner.getHeight() - 400) / 2);

        gameWinStage.show();
    }

    /**
     * Return back to Level Select
     * @param event Levels Button Press Event
     * @throws Exception Any Exception thrown when Scene Transition fails
     */
    @FXML
    private void navToLevelSelect(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("LevelSelect.fxml"));
        Parent levelSelectParent = loader.load();
        Scene levelSelectScene = new Scene(levelSelectParent);

        LevelSelectController levelSelectController = loader.getController();
        levelSelectController.initData(this.settings, this.mode, this.difficulty);

        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(levelSelectScene);
    }

    /**
     * Return back to Menu
     * @param event Quit Button Press Event
     * @throws Exception Any Exception thrown when Scene Transition fails
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
     * Undo Move in the Game (goes to the most recent board state).
     * @param event Undo Button Press Event
     */
    @FXML
    private void undoMove(ActionEvent event) {
        this.board.undoMove();
        this.board.updateNumMoves();
        this.updateBoard();
    }

    /**
     * Redo Move in the Game (undo an undo).
     * @param event Redo Button Press Event
     */
    @FXML
    private void redoMove(ActionEvent event) {
        this.board.redoMove();
        this.board.updateNumMoves();
        this.updateBoard();
    }

    /**
     * Will animate the block to a new location as a step to solve the puzzle.
     * @param event Hint Button Press Event
     */
    @FXML
    private void showHint(ActionEvent event) {
        this.disableGameButtons();
        Integer[] newPosition = {this.solverBlock.getRow(), this.solverBlock.getCol()};
        // Find the ID of this block
        for (int i = 0; i < this.mgList.size(); i++) {
            if (this.solverBlock.getID().equals(this.recNodeList.get(i).getUserData())) {
                MouseGestures mg = this.mgList.get(i);

                // Pane Size
                int widthFactor = 450 / this.board.getGridSize();
                int heightFactor = 450 / this.board.getGridSize();

                TranslateTransition tt;
                if (this.solverBlock.isHorizontal()) {
                    double startCol = this.solverBlock.getCol()*widthFactor;
                    tt = mg.animateMoveNodeX(startCol);
                }

                else {
                    double startRow = this.solverBlock.getRow()*heightFactor;
                    tt = mg.animateMoveNodeY(startRow);
                }

                this.mgList.set(i, mg);
                tt.setOnFinished(moveEvent -> {
                    if (!this.board.gameStateProperty().getValue()) {
                        this.enableGameButtons();
                        this.board.makeMove(this.solverBlock.getID(), newPosition, true);
                        this.board.updateNumMoves();
                        this.board.checkGameOver();
                    }
                });

            }
        }
    }

    /**
     * Return to the initial state of the board.
     * @param event Reset Button Press Event
     */
    @FXML
    private void resetBoard(ActionEvent event) {
        this.board.restart();
        this.board.updateNumMoves();
        this.updateBoard();
    }

    /**
     * Disable the game-related buttons (undo, redo, hint, reset).
     * Usually called right before an animation sequence.
     */
    private void disableGameButtons() {
        this.undoButton.setDisable(true);
        this.redoButton.setDisable(true);
        this.hintButton.setDisable(true);
        this.resetButton.setDisable(true);
    }

    /**
     * Enables the game-related buttons (undo, redo, hint, reset).
     * Usually called once an animation sequence finishes.
     */
    private void enableGameButtons() {
        this.undoButton.setDisable(false);
        this.redoButton.setDisable(false);
        this.hintButton.setDisable(false);
        this.resetButton.setDisable(false);
    }

    /**
     * Disables all buttons. Called once the game is complete.
     */
    private void disableAllButtons() {
        this.undoButton.setDisable(true);
        this.redoButton.setDisable(true);
        this.hintButton.setDisable(true);
        this.resetButton.setDisable(true);
        this.levelSelectButton.setDisable(true);
        this.quitButton.setDisable(true);
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

    /**
     * Plays victorySound audio when game is won.
     */
    @FXML
    private void playVictorySound() {
        this.settings.playVictorySound();
    }
}

