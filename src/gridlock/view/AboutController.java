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
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * "About" from the Menu. About.fxml illustrates the team members and their roles. Nothing more.
 * Accessible through Menu -> About
 * Added by James. Edited by Ian.
 */
public class AboutController {
    private SystemSettings settings;

    @FXML
    private AnchorPane wrapper;

    /**
     * Initialises Settings (mainly for the sounds to work). Used to pass information between controllers.
     * @param settings Settings for the App.
     */
    public void initData(SystemSettings settings) {
        this.settings = settings;
    }

    /**
     * Generates a fade in transition when changing scenes.
     */
    @FXML
    private void initialize() {
        this.wrapper.setOpacity(0);
        this.performFadeIn(this.wrapper);
    }

    /**
     * Handles the Buttons which are responsible for changing scenes.
     * @param event Button Press Event
     */
    @FXML
    private void changeSceneControl(ActionEvent event) {
        FadeTransition ft = this.performFadeOut(this.wrapper);
        ft.setOnFinished (fadeEvent -> {
            try {
                Button button = (Button) event.getSource();
                switch (button.getText()) {
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


    /**
     * Return back to Menu
     * @param event Button Press Event
     * @throws Exception Any Exception
     */
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

    /**
     * Fade Out Animation (mostly used for Scene transitioning)
     * @param node The target node to perform Fade Out
     * @return Fade Transition Object
     */
    private FadeTransition performFadeOut(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.play();
        return ft;
    }

    /**
     * Fade In Animation (mostly used for Scene transitioning)
     * @param node The target node to perform Fade In
     * @return Fade Transition Object
     */
    private FadeTransition performFadeIn(Node node) {
        FadeTransition ft = new FadeTransition(Duration.millis(250), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
        return ft;
    }

    /**
     * Triggered when Mouse enters a Node.
     * Used when mouse enters a button, which will increase the size of the button.
     * Used in conjunction with buttonExitAnimation
     * @param event Mouse Enter Event
     */
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

    /**
     * Triggered when Mouse exits a Node.
     * Used when mouse enters a button, which will increase the size of the button.
     * Used in conjunction with buttonEnterAnimation
     * @param event Mouse Exit Event
     */
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

    /**
     * Plays buttonSound audio when a button is pressed.
     */
    @FXML
    private void playButtonPressSound() {
        this.settings.playButtonPressSound();
    }
}