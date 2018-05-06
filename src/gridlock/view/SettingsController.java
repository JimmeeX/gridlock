package gridlock.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class SettingsController {
    @FXML
    Slider soundSlider;
    @FXML
    Slider musicSlider;

    @FXML
    private void navToMenu(ActionEvent event) throws Exception {
        Parent menuParent = FXMLLoader.load(getClass().getResource("Menu.fxml"));
        Scene menuScene = new Scene(menuParent);
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(menuScene);
    }
    //TODO: Handle Music + Sound (+ Perhaps other settings)
    private void getSoundLevel() {
        // TODO
    }
    private void getMusicLevel() {
        // TODO
    }
}
