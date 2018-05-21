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

public class HelpController {
    private SystemSettings settings;
    private GameBoard board;
    private ArrayList<Node> recNodeList;
    private ArrayList<MouseGestures> mgList;
    private ArrayList<SequentialTransition> animations;
    private IntegerProperty sequenceId;

    @FXML
    private AnchorPane wrapper;
    @FXML
    private Label movesLabel;
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
    private Label helpText;

    public void initData(SystemSettings settings) {
        this.settings = settings;
        this.movesLabel.setText("Moves: 0");
        this.board = new GameBoard();
        this.board.process("src/gridlock/resources/tut.txt");

        this.board.gameStateProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    disableButtons();
                    nextButton.setDisable(false);
                    animateWinSequence();
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

        // Draw the Rectangles and add it to the Board
        this.initialiseNodeList();

        // Deactivate Functionality of buttons and make them disappear.
        this.deactivate();

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

                        // Add Drag/Drop Functionality to the Rectangles
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

        // Play Animation
        this.animations.get(0).play();
    }

    @FXML
    private void initialize() {
        this.wrapper.setOpacity(0);
        FadeTransition ft = this.fadeIn(this.wrapper);
        ft.play();
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
            this.boardField.getChildren().addAll(rec);
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

    private SequentialTransition animationSequence1() {
        // Goal Node Fade In
        Node playerNode = this.getPlayerNode();
        FadeTransition playerFadeIn = this.fadeIn(playerNode);

        // Text Phase 1 Fade In
        this.helpText.setText("1. This is the player block. The goal is to drag the player block to the goal on the right.");
        FadeTransition textFadeIn = this.fadeIn(this.helpText);

        // Pulsing Goal Block
        FadeTransition playerPulse = this.pulse(playerNode);

        // Pulsing Arrow
        FadeTransition goalPulse = this.pulse(this.goalArrow);

        ParallelTransition pulse = new ParallelTransition(playerPulse, goalPulse);

        return new SequentialTransition(playerFadeIn, textFadeIn, pulse);
    }

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

    private SequentialTransition animationSequence5() {
        FadeTransition textOut = this.fadeOut(this.helpText);
        textOut.setOnFinished(event -> {
            this.helpText.setText("5. Good luck!");
        });

        FadeTransition textIn = this.fadeIn(this.helpText);

        return (new SequentialTransition(textOut, textIn));
    }

    private Node getPlayerNode() {
        for (Node node:this.recNodeList) {
            if (node.getUserData().equals("z")) {
                return node;
            }
        }
        return null;
    }

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

    private FadeTransition pulse(Node node){
        FadeTransition ft = new FadeTransition(Duration.millis(1500), node);

        ft.setFromValue(1);//Specifies the start opacity value for this FadeTransition
        ft.setToValue(0);
        ft.setCycleCount(Timeline.INDEFINITE);
        ft.setAutoReverse(true);
        return ft;
    }

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

    @FXML
    private void showHelpWin(ActionEvent event) throws Exception {
        this.playVictorySound();
        // Initialise Popup Stage
        Stage helpWinStage = new Stage();
        helpWinStage.initStyle(StageStyle.UNDECORATED);
        helpWinStage.initModality(Modality.APPLICATION_MODAL);
        helpWinStage.initOwner(((Node)event.getSource()).getScene().getWindow());

        // Load Screen
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("HelpWin.fxml"));
        Parent helpWinParent = loader.load();
        Scene helpWinScene = new Scene(helpWinParent);

        // Attach Controller
        HelpWinController helpWinController = loader.getController();
        helpWinController.initData(this.settings, this.board.getNumMoves());

        helpWinStage.setScene(helpWinScene);
        helpWinStage.show();
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
        Block block = this.board.getHint(false);
        Integer[] newPosition = {block.getRow(), block.getCol()};
        this.board.makeMove(block.getID(), newPosition, true);
        this.board.updateNumMoves();
        this.board.checkGameOver();
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

    @FXML
    private void continueAnimation(ActionEvent event) {
        this.sequenceId.setValue(this.sequenceId.getValue()+1);
        if (this.sequenceId.getValue() != 0) {
            this.animations.get(this.sequenceId.getValue() - 1).stop();
        }
//        if (this.sequenceId.getValue() != )
        this.animations.get(this.sequenceId.getValue()).play();
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


    private FadeTransition fadeOut(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(1);
        ft.setToValue(0);
        return ft;
    }

    private FadeTransition fadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(0);
        ft.setToValue(1);
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
