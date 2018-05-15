package gridlock.view;

import gridlock.model.*;
import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;

public class GameController {
    private SystemSettings settings;
    private Board board;
    private Mode mode;
    private Difficulty difficulty;
    private Integer level;
    private ArrayList<Node> recNodeList;

    @FXML
    private Label modeLabel;
    @FXML
    private Label difficultyLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Label movesLabel;
    @FXML
    private Pane boardField;
    @FXML
    private Button nextButton;

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

        // Read Board from File
        String levelName = "src/gridlock/resources/" + this.difficulty.toString().toLowerCase() + "/" + this.level.toString() + ".txt";
        this.initialiseBoard(levelName);

        // Add Listener for Win Game Condition
        this.board.gameStateProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    nextButton.setDisable(false);
                    // TODO: These are just test numbers for 1,2,3 stars
                    if (board.getNumMoves() <= 15) {
                        settings.setLevelComplete(difficulty, level, 3);
                    }
                    else if (board.getNumMoves() <= 25) {
                        settings.setLevelComplete(difficulty, level, 2);
                    }
                    else {
                        settings.setLevelComplete(difficulty, level, 1);
                    }
                    // Pop up Window
                    nextButton.fire();
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

        // Draw the Rectangles and add it to the Board
        this.initialiseNodeList();

        // Add Drag/Drop Functionality to the Rectangles
        this.addMouseGestures();
    }

    private void initialiseBoard(String file) {
        this.board = new Board();
        levelGenerator(this.difficulty);
        this.board.process(file);
    }

    private void levelGenerator(Difficulty difficulty) {
        BoardSolver levGen = new BoardSolver();
        levGen.process();
        if (difficulty.equals("EASY")) {
            levGen.process();
        } else if (difficulty.equals("MEDIUM")) {

        } else {

        }
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
        for(int i = 0; i < blockL.size(); i++) {
            Node currNode = this.recNodeList.get(i);
            if (blockL.get(i).isHorizontal()) {
                MouseGestures hmg = new MouseGestures(this.settings, blockL.get(i).getID(), this.board, this.boardField, this.board.getGridSize(), this.board.getGridSize(), true, currNode, this.recNodeList);
                hmg.makeDraggable(recNodeList.get(i));
            } else {
                MouseGestures vmg = new MouseGestures(this.settings, blockL.get(i).getID(), this.board, this.boardField, this.board.getGridSize(), this.board.getGridSize(), false, currNode, this.recNodeList);
                vmg.makeDraggable(recNodeList.get(i));
            }
            this.boardField.getChildren().addAll(this.recNodeList.get(i));
        }
    }

    // Current Information
    private void updateBoard() {
        ArrayList<Block> blockList = this.board.getBlocks();
        for (int i = 0; i < blockList.size(); i++) {
            Block block = blockList.get(i);

            // Retrieve the Rectangle and Update it with new position
            Rectangle rec = (Rectangle) this.boardField.getChildren().get(i + 1);
            setBlocks(block, rec);
            this.boardField.getChildren().set(i + 1, rec);

            // update mouse
            if (block.isHorizontal()) {
                MouseGestures hmg = new MouseGestures(this.settings, block.getID(), this.board, this.boardField, this.board.getGridSize(), this.board.getGridSize(), true, this.recNodeList.get(i), this.recNodeList);
                hmg.makeDraggable(recNodeList.get(i));
            } else {
                MouseGestures vmg = new MouseGestures(this.settings, block.getID(), this.board, this.boardField, this.board.getGridSize(), this.board.getGridSize(), false, this.recNodeList.get(i), this.recNodeList);
                vmg.makeDraggable(recNodeList.get(i));
            }
        }
    }

    private void setBlocks(Block b, Rectangle rec) {
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

        // Add Image
        if (b.getID().equals("z")) {
            //rec.setFill(new ImagePattern(new Image("gridlock/static/block_6.jpg")));
            Color c = Color.ALICEBLUE;
            rec.setFill(c);
        }
        else {
            // TODO: How to rotate a texture?
            //rec.setFill(new ImagePattern(new Image("gridlock/static/block_7.jpg")));
            Color c = Color.CORAL;
            rec.setFill(c);
        }
        rec.setEffect(new BoxBlur());

        // Add Effects
        InnerShadow effect = new InnerShadow();
        rec.setEffect(effect);
    }

    @FXML
    private void showGameWin(ActionEvent event) throws Exception {
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
        this.board.printGrid();
    }

    @FXML
    private void redoMove(ActionEvent event) {
        this.board.redoMove();
        this.board.updateNumMoves();
        this.updateBoard();
        this.board.printGrid();
    }

    @FXML
    private void resetBoard(ActionEvent event) {
        this.board.restart();
        this.board.updateNumMoves();
        this.updateBoard();
        this.board.printGrid();
    }

    @FXML
    private void showHint(ActionEvent event) {
        // TODO
    }

    @FXML
    private void playButtonPressSound() {
        this.settings.playButtonPressSound();
    }
}

