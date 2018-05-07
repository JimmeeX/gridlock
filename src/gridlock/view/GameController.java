package gridlock.view;

import gridlock.model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

public class GameController {
    private Board gridlock;
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

        // Read Board from File/**
        //     * undo the previous move made
        //     * @post this.nextLocations.size()++
        //     * @post this.prevLocation.size()--
        //     */
        //    public void undoMove() {
        //        if (this.prevLocations.size() != 0) {
        //            Block copy = this.prevLocations.get(this.prevLocations.size() - 1);
        //            Block block = new Block(copy.getID(), copy.getPosition().get(0)[0], copy.getPosition().get(0)[1]);
        //            this.prevLocations.remove(copy);
        //            for (Block oldBlock: this.blocks) {
        //                if (oldBlock.getID().equals(block.getID())) {
        //                    Block toAdd = new Block(oldBlock.getID(), oldBlock.getPosition().get(0)[0], oldBlock.getPosition().get(0)[1]);
        //                    this.nextLocations.add(toAdd);
        //                }
        //            }
        //            makeMove(block.getID(), block.getPosition().get(0));
        //            this.prevLocations.remove(this.prevLocations.size() - 1);
        //        }
        //    }
        //
        //    /**
        //     * redo the move undone
        //     * @post this.nextLocation.size()--
        //     * @post this.prevLocation.size()++
        //     */
        //    public void redoMove() {
        //        if (this.nextLocations.size() != 0) {
        //            Block copy = this.nextLocations.get(0);
        //            Block block = new Block(copy.getID(), copy.getPosition().get(0)[0], copy.getPosition().get(0)[1]);
        //            this.nextLocations.remove(copy);
        //            for (Block oldBlock: this.blocks) {
        //                if (oldBlock.getID().equals(block.getID())) {
        //                    Block toAdd = new Block(oldBlock.getID(), oldBlock.getPosition().get(0)[0], oldBlock.getPosition().get(0)[1]);
        //                    this.prevLocations.add(toAdd);
        //                }
        //            }
        //            makeMove(block.getID(), block.getPosition().get(0));
        //            this.prevLocations.remove(this.prevLocations.size() - 1);
        //        }
        //    }
        //
        //    /**
        //     * restart back to the initial position of the board
        //     * @post this.numOfMoves = 0
        //     * @post this.prevLocations.size() = 0
        //     * @post this.nextLocation.size() = 0
        //     */
        //    public void restart() {
        //        for (int item = this.prevLocations.size(); item > 0; item--) {
        //            Block block = this.prevLocations.get(item - 1);
        //            makeMove(block.getID(), block.getPosition().get(0));
        //        }
        //        this.prevLocations.clear();
        //        this.nextLocations.clear();
        //        this.numOfMoves = 0;
        //    }
        this.gridlock = new Board();
        this.gridlock.process("src/gridlock/resources/easy/1.txt");
        System.out.println(this.gridlock);

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

    @FXML
    private void navToMenu(ActionEvent event) throws Exception {
        Parent menuParent = FXMLLoader.load(getClass().getResource("Menu.fxml"));
        Scene menuScene = new Scene(menuParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(menuScene);
    }

}

