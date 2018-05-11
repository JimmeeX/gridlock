package gridlock.view;

import gridlock.model.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class GameController {
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
    private Pane boardField;
    @FXML
    private Button nextButton;

    public void initData(Mode mode, Difficulty difficulty, Integer level) {
        // Initialise Variables
        this.mode = mode;
        this.difficulty = difficulty;
        this.level = level;

        this.modeLabel.setText(this.mode.toString());
        this.difficultyLabel.setText(this.difficulty.toString());
        this.levelLabel.setText(this.level.toString());

        // Read Board from File
        this.initialiseBoard("src/gridlock/resources/easy/1.txt");

        // Add Listener for Win Game Condition
        this.board.gameStateProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    nextButton.setDisable(false);
                    // Pop up Window
                    nextButton.fire();
                }
                else {
                    nextButton.setDisable(true);
                }
            }
        });

        // Draw the Rectangles and add it to the Board
        this.initialiseNodeList();

        // Add Drag/Drop Functionality to the Rectangles
        this.addMouseGestures();
    }

    private void initialiseBoard(String file) {
        this.board = new Board();
        this.board.process(file);
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
                MouseGestures hmg = new MouseGestures(blockL.get(i).getID(), this.board, this.boardField, 6, 6, true, currNode, this.recNodeList);
                hmg.makeDraggable(recNodeList.get(i));

            } else {
                MouseGestures vmg = new MouseGestures(blockL.get(i).getID(), this.board, this.boardField, 6, 6, false, currNode, this.recNodeList);
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
        }
    }

    private void setBlocks(Block b, Rectangle rec) {
        int height, width, startRow, startCol;

        // TODO: Generalise numbers to boardField.pane / grid(X/Y)
        if(b.isHorizontal()){
            height = 75;
            width = 75*b.getSize();
        } else {
            height = 75*b.getSize();
            width = 75;
        }
        startRow = b.getRow()*75;
        startCol = b.getCol()*75;

        rec.setHeight(height);
        rec.setWidth(width);
        rec.setX(startCol);
        rec.setY(startRow);
        rec.setTranslateX(0);
        rec.setTranslateY(0);
    }

    @FXML
    private void showGameWin(ActionEvent event) throws Exception {
//        Stage gameWinStage = new Stage();
//        gameWinStage.initModality();
//        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("GameWin.fxml"));
//        Parent gameWinPopup = fxmlLoader.load();
//        Stage gameWinStage = new Stage();
//        gameWinStage.show();
        Popup gameWinPopup = new Popup();

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("GameWin.fxml"));

        Parent gameWinParent = loader.load();
        Scene gameWinScene = new Scene(gameWinParent);

        GameWinController gameWinController = loader.getController();
        gameWinController.initData(this.mode, this.difficulty, this.level, 10);
        gameWinPopup.getContent().add(gameWinParent);

        gameWinPopup.show(((Node)event.getSource()).getScene().getWindow());
    }

    @FXML
    private void navToMenu(ActionEvent event) throws Exception {
        Parent menuParent = FXMLLoader.load(getClass().getResource("Menu.fxml"));
        Scene menuScene = new Scene(menuParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(menuScene);
    }

    @FXML
    private void undoMove(ActionEvent event) {
        this.board.printGrid();
        this.board.undoMove();
        this.updateBoard();
        this.board.printGrid();
    }

    @FXML
    private void redoMove(ActionEvent event) {
        this.board.printGrid();
        this.board.redoMove();
        this.updateBoard();
        this.board.printGrid();
    }

    @FXML
    private void resetBoard(ActionEvent event) {
        this.board.printGrid();
        this.board.restart();
        this.updateBoard();
        this.board.printGrid();
    }
}

