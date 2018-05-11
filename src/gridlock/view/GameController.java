package gridlock.view;

import gridlock.model.*;
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
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

public class GameController {
    private Board board;
    private Mode mode;
    private Difficulty difficulty;
    private Integer level;


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

        // Draw Rectangles and add to Pane (so Pane is its Parent).

        for (Block block: this.board.getBlocks()) {
            Rectangle rec = new Rectangle(0,0);
            rec.setUserData(block.getID());
            if(block.getID().equals("z")) {
                rec.setId("player");
            }
            else {
                rec.setId("obstacles");
            }
            setBlocks(block, rec);

            // ===== TEST CODE
            // MouseGestures hmg = new MouseGestures(boardField, 6, 6, false);
            //hmg.makeDraggable(rec);
            //boardField.getChildren().addAll(rec);
            //}
            // rec.strokeWidthProperty();

            // TODO: Apply MouseGestures to each Rectangle (include collisions)
            if (block.isHorizontal()) {
                MouseGestures hmg = new MouseGestures(block.getID(), this.board, this.boardField, 6, 6, true);
                hmg.makeDraggable(rec);

            } else {
                MouseGestures vmg = new MouseGestures(block.getID(), this.board, this.boardField, 6, 6, false);
                vmg.makeDraggable(rec);
            }
            this.boardField.getChildren().addAll(rec);
        }
        System.out.println(this.boardField.getChildren());
    }

    private void initialiseBoard() {

    }

    // Current Information
    private void updateBoard() {
        ArrayList<Block> blockList = this.board.getBlocks();
        for (int i = 0; i < blockList.size(); i++) {
            Block block = blockList.get(i);
            // Retrieve the Rectangle and Update it with new position
            Rectangle rec = (Rectangle)this.boardField.getChildren().get(i+1);
            setBlocks(block, rec);
            this.boardField.getChildren().set(i+1, rec);
        }
    }

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

