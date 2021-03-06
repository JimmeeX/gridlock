package gridlock.view;

import gridlock.model.Block;
import gridlock.model.GameBoard;
import gridlock.model.SystemSettings;
import javafx.animation.*;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
 * "Help" from the Menu. Help.fxml is a tutorial designed to illustrated how to play the game.
 * Accessible through Menu -> Help
 * Quite similar to GameController, with less functionality and more animation.
 * Added by James. Edited by Ian, Alina
 */
public class HelpController {
    private SystemSettings settings;
    private GameBoard board;
    private Integer minMoves;
    private Integer result;
    private ArrayList<Node> recNodeList;
    private ArrayList<MouseGestures> mgList;
    private ArrayList<SequentialTransition> animations;
    private IntegerProperty sequenceId;

    @FXML
    private AnchorPane wrapper;
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
    private Button continueButton;
    @FXML
    private Button quitButton;
    @FXML
    private Label helpText;

    /**
     * Initialises Settings (mainly for the sounds to work). Used to pass information between controllers.
     * Initialises Tutorial GameBoard
     * Draws Blocks and Places them on Board.
     * Initialises Animation Sequence
     * @param settings Settings for the App.
     */
    public void initData(SystemSettings settings) {
        this.settings = settings;
        this.movesLabel.setText("Moves: 0");

        this.initBoard();

        this.board.setMinMoves();
        this.minMoves = this.board.getMinMoves();
        this.minMovesLabel.setText("Goal: " + this.minMoves);

        // Draw the Rectangles and add it to the Board
        this.initNodeList();

        // Deactivate Functionality of buttons and make them disappear.
        this.deactivate();

        // Initialise Animation Sequence
        this.initAnimation();
    }

    /**
     * Generates a fade in transition
     */
    @FXML
    private void initialize() {
        this.wrapper.setOpacity(0);
        FadeTransition ft = this.fadeIn(this.wrapper);
        ft.play();
    }

    /**
     * Generates a board with added listeners.
     */
    private void initBoard() {
        this.board = new GameBoard();
        this.board.process("src/gridlock/resources/tut.txt");

        this.board.gameStateProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    disableAllButtons();
                    nextButton.setDisable(false);
                    handleWin();
                    animateWinSequence();
                }
                else {
                    nextButton.setDisable(true);
                }
            }
        });

        // Add Listener for Board Moves
        this.board.numMovesProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                movesLabel.setText("Moves: " + newValue.toString());
            }
        });
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
            this.boardField.getChildren().addAll(rec);
        }
    }

    /**
     * Creates the main animation sequence for the tutorial.
     */
    private void initAnimation() {
        this.sequenceId = new SimpleIntegerProperty();
        this.sequenceId.setValue(0);

        this.sequenceId.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                switch (newValue.intValue()) {
                    case 1:
                        Node node = getPlayerNode();
                        node.setOpacity(1);
                        goalArrow.setOpacity(1);
                        break;
                    case 2:
                        for (Node hNode:getHorizontalNodes()) {
                            hNode.setOpacity(1);
                        }
                        break;
                    case 3:
                        for (Node vNode:getVerticalNodes()) {
                            vNode.setOpacity(1);
                            undoButton.setVisible(true);
                            redoButton.setVisible(true);
                            hintButton.setVisible(true);
                            resetButton.setVisible(true);
                        }
                        break;
                    case 4:
                        undoButton.setOpacity(1);
                        redoButton.setOpacity(1);
                        hintButton.setOpacity(1);
                        resetButton.setOpacity(1);
                        undoButton.setDisable(false);
                        redoButton.setDisable(false);
                        hintButton.setDisable(false);
                        resetButton.setDisable(false);

                        addMouseGestures();
                }
                if (newValue.intValue() == animations.size() - 1) {
                    continueButton.setDisable(true);
                }
            }
        });

        this.animations = new ArrayList<>();
        this.animations.add(this.animationSequence1());
        this.animations.add(this.animationSequence2());
        this.animations.add(this.animationSequence3());
        this.animations.add(this.animationSequence4());
        this.animations.add(this.animationSequence5());

        this.animations.get(0).play();
    }

    /**
     * Adds Drag/Drop Functionality to the Rectangles.
     */
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
        }
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
     * Deactivate Game Buttons, and make them invisible. Called at the start of the tutorial.
     */
    private void deactivate() {
        for (Node node: this.recNodeList) {
            node.setOpacity(0);
        }
        this.undoButton.setVisible(false);
        this.redoButton.setVisible(false);
        this.hintButton.setVisible(false);
        this.resetButton.setVisible(false);
        this.undoButton.setOpacity(0);
        this.redoButton.setOpacity(0);
        this.hintButton.setOpacity(0);
        this.resetButton.setOpacity(0);
        this.undoButton.setDisable(true);
        this.redoButton.setDisable(true);
        this.hintButton.setDisable(true);
        this.resetButton.setDisable(true);
    }

    /**
     * Determines what medal is deserved from the user performance.
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
     * Tutorial Animation Sequence Phase 1. (Main goal of the game)
     * @return Sequence of Animations for Phase 1
     */
    private SequentialTransition animationSequence1() {
        // Goal Node Fade In
        Node playerNode = this.getPlayerNode();
        FadeTransition playerFadeIn = this.fadeIn(playerNode);

        // Text Phase 1 Fade In
        this.helpText.setText("1. This is the player block. To win, drag it to the goal on the right.");
        FadeTransition textFadeIn = this.fadeIn(this.helpText);

        // Pulsing Goal Block
        FadeTransition playerPulse = this.pulse(playerNode);

        // Pulsing Arrow
        FadeTransition goalPulse = this.pulse(this.goalArrow);

        ParallelTransition pulse = new ParallelTransition(playerPulse, goalPulse);

        return new SequentialTransition(playerFadeIn, textFadeIn, pulse);
    }

    /**
     * Tutorial Animation Sequence Phase 2. (Horizontal Blocks)
     * @return Sequence of Animations for Phase 2
     */
    private SequentialTransition animationSequence2() {
        FadeTransition textOut = this.fadeOut(this.helpText);
        textOut.setOnFinished(event -> {
            this.helpText.setText("2. The horizontal blocks can only move left or right!");
        });

        FadeTransition textIn = this.fadeIn(this.helpText);

        // Horizontal Node Fade In
        ParallelTransition horizontalFadeIn = new ParallelTransition();
        ArrayList<Node> horizontalNodes = this.getHorizontalNodes();
        for(Node node:horizontalNodes) {
            FadeTransition ft = fadeIn(node);
            horizontalFadeIn.getChildren().add(ft);
        }

        PauseTransition pause = new PauseTransition(Duration.millis(1000));

        ParallelTransition horizontalPulse = new ParallelTransition();
        for(Node node:horizontalNodes) {
            FadeTransition ft = pulse(node);
            horizontalPulse.getChildren().add(ft);
        }

        return (new SequentialTransition(textOut, textIn, horizontalFadeIn, pause, horizontalPulse));
    }

    /**
     * Tutorial Animation Sequence Phase 3. (Vertical Blocks)
     * @return Sequence of Animations for Phase 3
     */
    private SequentialTransition animationSequence3() {
        FadeTransition textOut = this.fadeOut(this.helpText);
        textOut.setOnFinished(event -> {
            this.helpText.setText("3. The vertical blocks can only move up or down!");
        });

        FadeTransition textIn = this.fadeIn(this.helpText);

        // Vertical Node Fade In
        ArrayList<Node> verticalNodes = this.getVerticalNodes();

        ParallelTransition verticalFadeIn = new ParallelTransition();
        for(Node node:verticalNodes) {
            FadeTransition ft = fadeIn(node);
            verticalFadeIn.getChildren().add(ft);
        }

        PauseTransition pause = new PauseTransition(Duration.millis(1000));

        // Vertical Node pulse
        ParallelTransition verticalPulse = new ParallelTransition();
        for(Node node:verticalNodes) {
            FadeTransition ft = pulse(node);
            verticalPulse.getChildren().add(ft);
        }

        return (new SequentialTransition(textOut, textIn, verticalFadeIn, pause, verticalPulse));
    }

    /**
     * Tutorial Animation Sequence Phase 4. (Game Buttons: Undo, Reset, Hint, Redo)
     * @return Sequence of Animations for Phase 4
     */
    private SequentialTransition animationSequence4() {
        FadeTransition textOut = this.fadeOut(this.helpText);
        textOut.setOnFinished(event -> {
            this.helpText.setText("4. If you are stuck, you can undo, redo, use hints, or reset on the left.");
        });

        FadeTransition textIn = this.fadeIn(this.helpText);

        FadeTransition undoIn = this.fadeIn(this.undoButton);
        FadeTransition redoIn = this.fadeIn(this.redoButton);
        FadeTransition hintIn = this.fadeIn(this.hintButton);
        FadeTransition resetIn = this.fadeIn(this.resetButton);
        ParallelTransition buttonIn = new ParallelTransition(undoIn, redoIn, hintIn, resetIn);

        FadeTransition undoPulse = this.pulse(this.undoButton);
        FadeTransition redoPulse = this.pulse(this.redoButton);
        FadeTransition hintPulse = this.pulse(this.hintButton);
        FadeTransition resetPulse = this.pulse(this.resetButton);
        ParallelTransition buttonPulse = new ParallelTransition(undoPulse, redoPulse, hintPulse, resetPulse);

        return (new SequentialTransition(textOut, textIn, buttonIn, buttonPulse));
    }

    /**
     * Tutorial Animation Sequence Phase 5. (Finished Tutorial)
     * @return Sequence of Animations for Phase 5.
     */
    private SequentialTransition animationSequence5() {
        FadeTransition textOut = this.fadeOut(this.helpText);
        textOut.setOnFinished(event -> {
            this.helpText.setText("5. Good luck!");
        });

        FadeTransition textIn = this.fadeIn(this.helpText);

        return (new SequentialTransition(textOut, textIn));
    }

    /**
     * Helper Function to get the Player Node
     * @return Node: Player Node
     */
    private Node getPlayerNode() {
        for (Node node:this.recNodeList) {
            if (node.getUserData().equals("z")) {
                return node;
            }
        }
        return null;
    }

    /**
     * Helper Function to get Horizontal Nodes (Excluding Player Node)
     * @return ArrayList of Horizontal Nodes
     */
    private ArrayList<Node> getHorizontalNodes() {
        ArrayList<Block> blockList = this.board.getBlocks();
        ArrayList<Node> nodeList = new ArrayList<>();
        for (int i = 0; i < blockList.size(); i++) {
            Block b = blockList.get(i);
            if (b.isHorizontal() && !b.getID().equals("z")) {
                nodeList.add(this.recNodeList.get(i));
            }
        }
        return nodeList;
    }

    /**
     * Helper Function to get Vertical Nodes
     * @return ArrayList of Vertical Nodes
     */
    private ArrayList<Node> getVerticalNodes() {
        ArrayList<Block> blockList = this.board.getBlocks();
        ArrayList<Node> nodeList = new ArrayList<>();
        for (int i = 0; i < blockList.size(); i++) {
            if (!blockList.get(i).isHorizontal()) {
                nodeList.add(this.recNodeList.get(i));
            }
        }
        return nodeList;
    }

    /**
     * Animation. Fade in/out indefinitely to a specified Node.
     * @param node Target Node for animation
     * @return FadeTransition object
     */
    private FadeTransition pulse(Node node){
        FadeTransition ft = new FadeTransition(Duration.millis(1500), node);

        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setCycleCount(Timeline.INDEFINITE);
        ft.setAutoReverse(true);
        return ft;
    }

    /**
     * Handles the Buttons which are responsible for changing scenes.
     * @param event Button Press Event
     */
    @FXML
    private void changeSceneControl(ActionEvent event) {
        FadeTransition ft = this.fadeOut(this.wrapper);
        ft.play();
        ft.setOnFinished (fadeEvent -> {
            try {
                Button button = (Button) event.getSource();
                switch (button.getText()) {
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
     * Shows HelpWin Scene once Tutorial is Solved.
     * @param event Button Press Event
     * @throws Exception Any Exception thrown when Scene Transition fails
     */
    @FXML
    private void showHelpWin(ActionEvent event) throws Exception {
        this.playVictorySound();
        // Initialise Popup Stage
        Stage helpWinStage = new Stage();
        helpWinStage.initStyle(StageStyle.UNDECORATED);
        helpWinStage.initModality(Modality.APPLICATION_MODAL);
        Stage owner = (Stage)((Node)event.getSource()).getScene().getWindow();
        helpWinStage.initOwner(owner);

        // Load Screen
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("HelpWin.fxml"));
        Parent helpWinParent = loader.load();
        Scene helpWinScene = new Scene(helpWinParent);

        // Attach Controller
        HelpWinController helpWinController = loader.getController();
        helpWinController.initData(this.settings, this.board.numMovesProperty().getValue(), this.minMoves, this.result);

        helpWinStage.setScene(helpWinScene);

        helpWinStage.setX(owner.getX() + (owner.getWidth() - 600) / 2);
        helpWinStage.setY(owner.getY() + (owner.getHeight() - 400) / 2);

        helpWinStage.show();
    }

    /**
     * Return back to Menu
     * @param event Button Press Event
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
        Block block = this.board.getHint(false);
        Integer[] newPosition = {block.getRow(), block.getCol()};
        // Find the ID of this block
        for (int i = 0; i < this.mgList.size(); i++) {
            if (block.getID().equals(this.recNodeList.get(i).getUserData())) {
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
                tt.setOnFinished(moveEvent -> {
                    if (!this.board.gameStateProperty().getValue()) {
                        this.enableGameButtons();
                        this.board.makeMove(block.getID(), newPosition, true);
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
     * Goes to the next sequence in the animation.
     * @param event Continue Button Press Event
     */
    @FXML
    private void continueAnimation(ActionEvent event) {
        this.sequenceId.setValue(this.sequenceId.getValue()+1);
        if (this.sequenceId.getValue() != 0) {
            this.animations.get(this.sequenceId.getValue() - 1).stop();
        }
        this.animations.get(this.sequenceId.getValue()).play();
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
        this.undoButton.setOpacity(0.5);
        this.redoButton.setDisable(true);
        this.redoButton.setOpacity(0.5);
        this.hintButton.setDisable(true);
        this.hintButton.setOpacity(0.5);
        this.resetButton.setDisable(true);
        this.resetButton.setOpacity(0.5);
        this.continueButton.setDisable(true);
        this.quitButton.setDisable(true);
    }

    /**
     * Animation: Fade Out to a target Node
     * @param node The Target Node
     * @return FadeTransition
     */
    private FadeTransition fadeOut(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(1);
        ft.setToValue(0);
        return ft;
    }

    /**
     * Animation: Fade in to a target Node
     * @param node Tje Target Node
     * @return FadeTransition
     */
    private FadeTransition fadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(0);
        ft.setToValue(1);
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
