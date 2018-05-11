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
        this.board = new Board();
        this.board.process("src/gridlock/resources/easy/1.txt");
        System.out.println(this.board);
        System.out.println("================ IN GAME CONTROLLER ====================");
        this.board.printGrid();
        this.recNodeList = new ArrayList<>();

        // Add gameStateListener
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
            //this.boardField.getChildren().addAll(rec);
        }
            ArrayList<Node> recNL = this.recNodeList;
            ArrayList<Block> blockL = this.board.getBlocks();
            for(int i = 0; i < blockL.size(); i++) {
                Node currNode = this.recNodeList.get(i);
                // TODO: Apply MouseGestures to each Rectangle (include collisions)
                if (blockL.get(i).isHorizontal()) {
                    MouseGestures hmg = new MouseGestures(blockL.get(i).getID(), this.board, this.boardField, 6, 6, true, currNode, this.recNodeList);
                    hmg.makeDraggable(recNodeList.get(i));

                } else {
                    MouseGestures vmg = new MouseGestures(blockL.get(i).getID(), this.board, this.boardField, 6, 6, false, currNode, this.recNodeList);
                    vmg.makeDraggable(recNodeList.get(i));
                }
                this.boardField.getChildren().addAll(recNL.get(i));
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
        // Example
       /* MouseGestures vmg = new MouseGestures(boardField, 6, 6, false);
        MouseGestures hmg = new MouseGestures(boardField, 6, 6, true);
        vmg.makeDraggable(this.r1);
        hmg.makeDraggable(this.r2);*/


//        int num = 36;
//        int maxColumns = 6;
//        int maxRows = 6;
//        GridPane boardGame = new GridPane();
//        boardGame.setAlignment(Pos.CENTER);
//        Collection<StackPane> stackPanes = new ArrayList<StackPane>();
//        for (int row = 0; row < maxRows; row++) {
//            for (int col = maxColumns - 1; col >= 0; col--) {
//                StackPane stackPane = new StackPane();
//
//                // To occupy fixed space set the max and min size of
//                // stackpanes.
//                // stackPane.setPrefSize(150.0, 200.0);
//                stackPane.setMaxSize(100.0, 100.0);
//                stackPane.setMinSize(100.0, 100.0);
//
//                boardGame.add(stackPane, col, row);
//                stackPanes.add(stackPane);
//                num--;
//            }
//        }
//        boardGame.setGridLinesVisible(true);
//        boardGame.autosize();
//


    private void setBlocks(Block b, Rectangle rec){
        int height, width, startrow, startcol;
        boolean isHorizontal = b.isHorizontal();
        int size = b.getSize();
        int row = b.getRow();
        int col = b.getCol();

//        int gridX = this.boardField.getWidth() /

        if(isHorizontal){
             height = 75;
             width = 75*size;
        } else {
            height = 75*size;
            width = 75;
        }
        startrow = row*75;
        startcol = col*75;

        rec.setHeight(height);
        rec.setWidth(width);
        rec.setX(startcol);
        rec.setY(startrow);
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

   /* public void Randomcolor(Rectangle rec){
        float r,g,b;
        Random rand = new Random();
        r = rand.nextFloat();
        g =rand.nextFloat();
        b = rand.nextFloat();

        Color randomColor;
        randomColor = new Color(r, g, b);

        rec.setFill(randomColor);
    }*/

}

