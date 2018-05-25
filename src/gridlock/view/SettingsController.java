package gridlock.view;

import gridlock.model.SystemSettings;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * "Settings" from the Menu. Settings.fxml is used to configure the volume, and reset level progress.
 * Accessible through Menu -> Settings
 * Added by James. Edited by Ian.
 */
public class SettingsController {
    private SystemSettings settings;
    @FXML
    private AnchorPane wrapper;
    @FXML
    Slider soundSlider;
    @FXML
    ProgressBar soundProgressBar;
    @FXML
    Slider musicSlider;
    @FXML
    ProgressBar musicProgressBar;

    /**
     * Initialises Settings (mainly for the sounds to work). Used to pass information between controllers.
     * @param settings Settings for the App.
     */
    public void initData(SystemSettings settings) {
        this.settings = settings;
        this.soundSlider.setValue(this.settings.getSoundVolume() * 100);
        this.musicSlider.setValue(this.settings.getMusicVolume() * 100);
    }

    /**
     * Generates a fade in transition
     * Initialises Listeners to the volume sliders
     */
    @FXML
    private void initialize() {
        this.wrapper.setOpacity(0);
        this.performFadeIn(this.wrapper);

        this.soundSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                soundProgressBar.setProgress(newValue.doubleValue() / 100);
                settings.setSoundVolume(soundSlider.getValue() / 100);
            }
        });
        this.musicSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                musicProgressBar.setProgress(newValue.doubleValue() / 100);
                settings.setMusicVolume(musicSlider.getValue() / 100);
            }
        });
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
     * Function to reset level progress in SystemSettings
     * @param event Button Press Event
     */
    @FXML
    private void resetProgress(ActionEvent event) {
        this.settings.resetProgress();
    }

    /**
     * Changes the cursor to a hand when it enters the slider.
     * @param event Mouse Enter Event
     */
    @FXML
    private void sliderEnter(MouseEvent event) {
        Node node = (Node)event.getSource();
        node.setCursor(Cursor.HAND);
    }

    /**
     * Changes the cursor to default when it exits the slider.
     * @param event Mouse Exit Event
     */
    @FXML
    private void sliderExit(MouseEvent event) {
        Node node = (Node)event.getSource();
        node.setCursor(Cursor.DEFAULT);
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
