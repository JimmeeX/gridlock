package gridlock.view;

import gridlock.model.SystemSettings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.stage.Stage;

public class SettingsController {
    private SystemSettings settings;
    @FXML
    Slider soundSlider;
    @FXML
    ProgressBar soundProgressBar;
    @FXML
    Slider musicSlider;
    @FXML
    ProgressBar musicProgressBar;

    public void initData(SystemSettings settings) {
        this.settings = settings;
        this.soundSlider.setValue(this.settings.getSoundVolume() * 100);
        this.musicSlider.setValue(this.settings.getMusicVolume() * 100);
    }

    @FXML
    private void initialize() {
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

    @FXML
    private void resetProgress(ActionEvent event) {
        this.settings.resetProgress();
    }

    @FXML
    private void playButtonPressSound() {
        this.settings.playButtonPressSound();
    }
}
