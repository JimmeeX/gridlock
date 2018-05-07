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

public class GameController {
    private Board board;
    private Mode mode;
    private Difficulty difficulty;
    private Integer level;
    private ArrayList<Block> Blocklist;


    @FXML
    private Label modeLabel;
    @FXML
    private Label difficultyLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private Button nextButton;
    @FXML
    private Pane boardField;

    // Temporary
    @FXML
    private Rectangle r1;
    @FXML
    private Rectangle r2;
    // Add in ArrayList<Nodes> blocks and other stuff

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

        //Set Blocklist
        this.Blocklist = this.board.getBlocks();
        for(Block block: Blocklist){
            Rectangle rec = new Rectangle(250, 75);
            rec.setFill(Paint.valueOf("CORAL"));
            setBlocks(block,rec);
        }

        // TODO: Draw Rectangles and add to Pane (so Pane is its Parent).


        // TODO: Apply MouseGestures to each Rectangle (include collisions)
        // Example
        MouseGestures vmg = new MouseGestures(boardField, 6, 6, false);
        MouseGestures hmg = new MouseGestures(boardField, 6, 6, true);
        vmg.makeDraggable(this.r1);
        hmg.makeDraggable(this.r2);

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
    }

    private Rectangle createBoundsRectangle(Bounds bounds) {
        Rectangle rect = new Rectangle();

        rect.setFill(Color.TRANSPARENT);
        rect.setStroke(Color.LIGHTGRAY.deriveColor(1, 1, 1, 0.5));
        rect.setStrokeType(StrokeType.INSIDE);
        rect.setStrokeWidth(3);

        rect.setX(bounds.getMinX());
        rect.setY(bounds.getMinY());
        rect.setWidth(bounds.getWidth());
        rect.setHeight(bounds.getHeight());
        return rect;
    }

    public void setBlocks(Block b, Rectangle rectangle){
        int height, width,startrow, startcol;
        boolean isHorizontal = b.isHorizontal();
        int size = b.getSize();
        int row = b.getRow();
        int col = b.getCol();
        if(isHorizontal){
             height = 75;
             width = 125*size;
        } else {
            height = 235*size;
            width = 75;
        }
        startrow = row*75;
        startcol = col*125;

        rectangle.setHeight(height);
        rectangle.setWidth(width);
        rectangle.setX(startrow);
        rectangle.setY(startcol);
    }
    @FXML
    private void navToMenu(ActionEvent event) throws Exception {
        Parent menuParent = FXMLLoader.load(getClass().getResource("Menu.fxml"));
        Scene menuScene = new Scene(menuParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(menuScene);
    }

}

