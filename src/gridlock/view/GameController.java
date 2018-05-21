package gridlock.view;

import gridlock.model.*;
import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
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

public class GameController {
    private SystemSettings settings;
    private GameBoard board;
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

    public void initData(SystemSettings settings, Mode mode, Difficulty difficulty, Integer level) {
        // Initialise Variables
        this.settings = settings;
        this.mode = mode;
        this.difficulty = difficulty;
        this.level = level;

        this.modeLabel.setText(this.mode.toString());
        this.difficultyLabel.setText(this.difficulty.toString());
        this.levelLabel.setText("Level " + this.level.toString());
        this.movesLabel.setText("Moves: 0");


        this.board = new GameBoard();
        if (mode.equals(Mode.CAMPAIGN)) {
            // Read Board from File
            String levelName = "src/gridlock/resources/" + this.difficulty.toString().toLowerCase() + "/" + this.level.toString() + ".txt";
            this.board.process(levelName);
        }
        // TODO: Board Generator Threading
        else {
            GameBoardGenerator bg = new GameBoardGenerator();
            this.board = bg.generateAPuzzle(this.difficulty);
            this.levelSelectButton.setDisable(true);
        }

        // Add Listener for Win Game Condition
        this.board.gameStateProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    disableButtons();
                    nextButton.setDisable(false);
                    if (mode.equals(Mode.CAMPAIGN)) {
                        // TODO: These are just test numbers for 1,2,3 stars
                        if (board.getNumMoves() <= 15) {
                            settings.setLevelComplete(difficulty, level, 3);
                        } else if (board.getNumMoves() <= 25) {
                            settings.setLevelComplete(difficulty, level, 2);
                        } else {
                            settings.setLevelComplete(difficulty, level, 1);
                        }
                    }
                    animateWinSequence();
                }
                else {
                    nextButton.setDisable(true);
                }
            }
        });

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

        // Draw the Rectangles and add it to the Board
        this.initialiseNodeList();

        // Add Drag/Drop Functionality to the Rectangles
        this.addMouseGestures();

        this.pulse(this.goalArrow);
    }

    @FXML
    private void initialize() {
        this.wrapper.setOpacity(0);
        this.performFadeIn(this.wrapper);
    }

    private void initialiseNodeList() {
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

    private void addMouseGestures() {
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

    // Current Information
    private void updateBoard() {
        this.disableButtons();
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
                this.enableButtons();
            });
        }
    }

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

    private void setEffects(Rectangle rec) {
        rec.setEffect(new BoxBlur());

        // Add Effects
        InnerShadow effect = new InnerShadow();
        rec.setEffect(effect);
    }

    private void animateWinSequence() {
        // Get the player node
        this.disableButtons();
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
     * Pulsing Animation
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

    @FXML
    private void showGameWin(ActionEvent event) throws Exception {
        this.playVictorySound();
        // Initialise Popup Stage
        Stage gameWinStage = new Stage();
        gameWinStage.initStyle(StageStyle.UNDECORATED);
        gameWinStage.initModality(Modality.APPLICATION_MODAL);
        gameWinStage.initOwner(((Node)event.getSource()).getScene().getWindow());

        // Load Screen
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GameWin.fxml"));
        Parent gameWinParent = loader.load();
        Scene gameWinScene = new Scene(gameWinParent);

        // Attach Controller
        GameWinController gameWinController = loader.getController();
        gameWinController.initData(this.settings, this.mode, this.difficulty, this.level, this.board.getNumMoves());

        gameWinStage.setScene(gameWinScene);
        gameWinStage.show();
    }

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
    private void undoMove(ActionEvent event) {
        this.board.undoMove();
        this.board.updateNumMoves();
        this.updateBoard();
    }

    @FXML
    private void redoMove(ActionEvent event) {
        this.board.redoMove();
        this.board.updateNumMoves();
        this.updateBoard();
    }

    @FXML
    private void showHint(ActionEvent event) {
        this.disableButtons();
        Integer[] newPosition = {this.solverBlock.getRow(), this.solverBlock.getCol()};
        this.board.makeMove(this.solverBlock.getID(), newPosition, true);
        this.board.updateNumMoves();
        this.board.checkGameOver();
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
                        this.enableButtons();
                    }
                });

            }
        }
    }

    @FXML
    private void resetBoard(ActionEvent event) {
        this.board.restart();
        this.board.updateNumMoves();
        this.updateBoard();
    }

    private void disableButtons() {
        this.undoButton.setDisable(true);
        this.redoButton.setDisable(true);
        this.hintButton.setDisable(true);
        this.resetButton.setDisable(true);
    }

    private void enableButtons() {
        this.undoButton.setDisable(false);
        this.redoButton.setDisable(false);
        this.hintButton.setDisable(false);
        this.resetButton.setDisable(false);
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

    @FXML
    private void playVictorySound() {
        this.settings.playVictorySound();
    }
}

