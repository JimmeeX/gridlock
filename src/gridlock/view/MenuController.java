package gridlock.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MenuController {

    /**
     * Navigate to Play Settings Scene after press "Play"
     * @param event Play Button
     * @throws Exception
     */
    @FXML
    private void navToPlaySettings(ActionEvent event) throws Exception {
        Parent playSettingsParent = FXMLLoader.load(getClass().getResource("PlaySettings.fxml"));
        Scene playSettingsScene = new Scene(playSettingsParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(playSettingsScene);
    }

    /**
     * Navigate to Settings Scene after press "Settings"
     * @param event Settings Button
     * @throws Exception
     */
    @FXML
    private void navToSettings(ActionEvent event) throws Exception {
        Parent settingsParent = FXMLLoader.load(getClass().getResource("Settings.fxml"));
        Scene settingsScene = new Scene(settingsParent);
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
        Parent helpParent = FXMLLoader.load(getClass().getResource("Help.fxml"));
        Scene helpScene = new Scene(helpParent);
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
        Parent aboutParent = FXMLLoader.load(getClass().getResource("About.fxml"));
        Scene aboutScene = new Scene(aboutParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(aboutScene);
    }

    /**
     * Close Window
     * @param event Quit Button
     */
    @FXML
    private void quitGame(ActionEvent event) {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
    }

}
