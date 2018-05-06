package gridlock.view;

import gridlock.model.Board;
import gridlock.model.Difficulty;
import gridlock.model.Mode;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Collection;

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
    private Button nextButton;

    public void initData(Mode mode, Difficulty difficulty, Integer level) {
        this.mode = mode;
        this.difficulty = difficulty;
        this.level = level;

        this.modeLabel.setText(this.mode.toString());
        this.difficultyLabel.setText(this.difficulty.toString());
        this.levelLabel.setText(this.level.toString());

        // TODO: Initialise Board Here
        this.board = new Board();
        this.board.printGrid();

        // TODO: Draw Board Here
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