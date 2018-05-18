package gridlock.view;

import gridlock.model.SystemSettings;
import javafx.animation.FadeTransition;
import javafx.animation.KeyValue;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class MenuController {
    private SystemSettings settings;

    @FXML
    private Button Play;

    @FXML
    private AnchorPane parentPane;

    public void initData(SystemSettings settings) {
        this.settings = settings;
    }

    /**
     * Navigate to Play Settings Scene after press "Play"
     * @param event Play Button
     * @throws Exception
     */

    @FXML
    private void navToPlaySettings(ActionEvent event_1) throws Exception {
        FadeTransition ft = new FadeTransition(Duration.millis(250), parentPane);
        ft.setFromValue(1);//Specifies the start opacity value for this FadeTransition
        ft.setToValue(0.0);
        ft.play();

        ft.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    FXMLLoader loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("PlaySettings.fxml"));
                    Parent playSettingsParent = loader.load();
                    Scene playSettingsScene = new Scene(playSettingsParent);

                    PlaySettingsController playSettingsController = loader.getController();
                    playSettingsController.initData(settings);

                    Stage window = (Stage) ((Node) event_1.getSource()).getScene().getWindow();

                    window.setScene(playSettingsScene);
                }
                catch (Exception e) {
                    System.out.println("Failed");
                }
            }
        });
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("PlaySettings.fxml"));
//        Parent playSettingsParent = loader.load();
//        Scene playSettingsScene = new Scene(playSettingsParent);
//
//        PlaySettingsController playSettingsController = loader.getController();
//        playSettingsController.initData(this.settings);
//
//        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
//
//        window.setScene(playSettingsScene);

//        FadeTransition ft = new FadeTransition(Duration.millis(1200), parentPane);
//        ft.setFromValue(1);//Specifies the start opacity value for this FadeTransition
//        ft.setToValue(0.0);
//        ft.play();
//        ft.setOnFinished(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                window.setScene(playSettingsScene);
//            }
//        });

    }

    /**
     * Navigate to Settings Scene after press "Settings"
     * @param event Settings Button
     * @throws Exception
     */
    @FXML
    private void navToSettings(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Settings.fxml"));
        Parent settingsParent = loader.load();
        Scene settingsScene = new Scene(settingsParent);

        SettingsController settingsController = loader.getController();
        settingsController.initData(this.settings);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(settingsScene);
    }

    /**
     * Navigate to "Help" for instructions on how to play the game.
     * @param event Help Button
     * @throws Exception
     */
    @FXML
    private void navToHelp(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("Help.fxml"));
        Parent helpParent = loader.load();
        Scene helpScene = new Scene(helpParent);

        HelpController helpController = loader.getController();
        helpController.initData(this.settings);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(helpScene);
    }

    /**
     * Navigate to "About" page.
     * @param event About Button
     * @throws Exception
     */
    @FXML
    private void navToAbout(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("About.fxml"));
        Parent aboutParent = loader.load();
        Scene aboutScene = new Scene(aboutParent);

        AboutController aboutController = loader.getController();
        aboutController.initData(this.settings);

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(aboutScene);
    }

    /**
     * Close Window
     * @param event Quit Button
     */
    @FXML
    private void quitGame(ActionEvent event) throws IOException {
        // Save Data
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("src/gridlock/resources/save.data")))) {
            oos.writeObject(this.settings);
        }

        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
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
