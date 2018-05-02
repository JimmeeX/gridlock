package gridlock.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

public class MenuController {
    @FXML
    private ToggleGroup difficulty;
    @FXML
    private ToggleGroup gameMode;

    @FXML
    private void navToMenu(ActionEvent event) throws Exception {
        Parent menuParent = FXMLLoader.load(getClass().getResource("templates/Menu.fxml"));
        Scene menuScene = new Scene(menuParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(menuScene);
    }

    @FXML
    private void navToPlaySettings(ActionEvent event) throws Exception {
        Parent playSettingsParent = FXMLLoader.load(getClass().getResource("templates/PlaySettings.fxml"));
        Scene playSettingsScene = new Scene(playSettingsParent);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(playSettingsScene);
    }

    @FXML
    private void navToSettings(ActionEvent event) throws Exception {
        Parent settingsParent = FXMLLoader.load(getClass().getResource("templates/Settings.fxml"));
        Scene settingsScene = new Scene(settingsParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(settingsScene);
    }

    @FXML
    private void navToHelp(ActionEvent event) throws Exception {
        Parent helpParent = FXMLLoader.load(getClass().getResource("templates/Help.fxml"));
        Scene helpScene = new Scene(helpParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(helpScene);
    }

    @FXML
    private void navToAbout(ActionEvent event) throws Exception {
        Parent aboutParent = FXMLLoader.load(getClass().getResource("templates/About.fxml"));
        Scene aboutScene = new Scene(aboutParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(aboutScene);
    }

    @FXML
    private void quitGame(ActionEvent event) {
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.close();
        System.out.println("You pressed Quit");
    }
}