package gridlock.view;

import gridlock.model.SystemSettings;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HelpWinController {
    private SystemSettings settings;
    @FXML
    private AnchorPane wrapper;
    @FXML
    private Canvas canvas;
    @FXML
    private Label movesLabel;
    @FXML
    private Label minMovesLabel;

    public void initData(SystemSettings settings, Integer numMoves, Integer minMoves, Integer result) {
        this.settings = settings;
        this.minMovesLabel.setText("Goal: " + minMoves.toString());
        this.movesLabel.setText("Moves: " + numMoves.toString());

        this.drawMedals(result);
    }

    @FXML
    private void initialize() {
        this.wrapper.setOpacity(0);
        this.performFadeIn(this.wrapper);
    }

    private void drawMedals(Integer result) {
        Image medalImage = new Image("file:src/gridlock/static/images/medals.png");
        switch (result) {
            case 1:
                medalImage = new Image("file:src/gridlock/static/images/medal_bronze.png");
                break;
            case 2:
                medalImage = new Image("file:src/gridlock/static/images/medal_silver.png");
                break;
            case 3:
                medalImage = new Image("file:src/gridlock/static/images/medal_gold.png");
                break;
        }
        GraphicsContext gc = this.canvas.getGraphicsContext2D();

        gc.drawImage(medalImage, 25, 75, 100, 200);
        gc.drawImage(medalImage, 500, 75, 100, 200);
    }

    @FXML
    private void changeSceneControl(ActionEvent event) {
        FadeTransition ft = this.performFadeOut(this.wrapper);
        ft.setOnFinished (fadeEvent -> {
            try {
                Button button = (Button) event.getSource();
                switch (button.getText()) {
                    case "Restart":
                        this.restartLevel(event);
                        break;
                    case "Back":
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
    private void restartLevel(ActionEvent event) throws Exception {
        // On owner stage, reload the same level
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Help.fxml"));
        Parent helpParent = loader.load();
        Scene helpScene = new Scene(helpParent);

        HelpController helpController = loader.getController();
        helpController.initData(this.settings);

        Stage popupWindow = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Stage owner = (Stage)popupWindow.getOwner();
        owner.setScene(helpScene);

        // Close the popup window -> return to the game with a new board state
        popupWindow.close();
    }

    @FXML
    private void navToMenu(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Menu.fxml"));
        Parent menuParent = loader.load();
        Scene menuScene = new Scene(menuParent);

        MenuController menuController = loader.getController();
        menuController.initData(this.settings);

        Stage popupWindow = (Stage)((Node)event.getSource()).getScene().getWindow();
        Stage owner = (Stage)popupWindow.getOwner();
        owner.setScene(menuScene);

        // Close popup
        popupWindow.close();
    }

    private FadeTransition performFadeOut(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
        return ft;
    }

    private FadeTransition performFadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
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
}
