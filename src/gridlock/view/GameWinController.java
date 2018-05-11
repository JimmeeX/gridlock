package gridlock.view;

import gridlock.model.Board;
import gridlock.model.Difficulty;
import gridlock.model.Mode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import javax.swing.*;

public class GameWinController {
    private Mode mode;
    private Difficulty difficulty;
    private Integer level;

    // Temp
    @FXML
    private Label starsLabel;
    @FXML
    private Label movesLabel;

    public void initData(Mode mode, Difficulty difficulty, Integer level, Integer numMoves) {
        this.mode = mode;
        this.difficulty =difficulty;
        this.level = level;

        this.starsLabel.setText("3 Stars");
        this.movesLabel.setText(numMoves.toString());
    }

    @FXML
    private void navToNextLevel(ActionEvent event) throws Exception {


    }

    @FXML
    private void restartLevel(ActionEvent event) throws Exception {
        // Close the popup window -> return to the game with a new board state
    }

    @FXML
    private void navToMenu(ActionEvent event) throws Exception {
        Parent menuParent = FXMLLoader.load(getClass().getResource("Menu.fxml"));
        Scene menuScene = new Scene(menuParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(menuScene);
    }
}
