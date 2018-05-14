package gridlock.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.util.Arrays;

public class SystemSettings {
    private DoubleProperty soundVolume;
    private DoubleProperty musicVolume;

    private MediaPlayer moveBlockSound;
    private MediaPlayer buttonPressSound;

    private Integer[] easyLevels;
    private Integer[] mediumLevels;
    private Integer[] hardLevels;

    public SystemSettings(Double soundVolume, Double musicVolume) {
        this.soundVolume = new SimpleDoubleProperty();
        this.musicVolume = new SimpleDoubleProperty();

        this.soundVolume.setValue(soundVolume);
        this.musicVolume.setValue(musicVolume);

        // Initialise Sounds
        Media moveBlockMedia = new Media(new File("src/gridlock/static/audio/block_move_0.wav").toURI().toString());
        this.moveBlockSound = new MediaPlayer(moveBlockMedia);
        this.moveBlockSound.setVolume(this.soundVolume.getValue());

        Media buttonPressMedia = new Media(new File("src/gridlock/static/audio/button_press_0.wav").toURI().toString());
        this.buttonPressSound = new MediaPlayer(buttonPressMedia);
        this.buttonPressSound.setVolume(this.soundVolume.getValue());

        // Initialise Volume Listeners
        this.soundVolume.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                applySoundVolumes(newValue.doubleValue());
            }
        });

        // Initialise to 20 0's
        this.easyLevels = new Integer[20];
        this.mediumLevels = new Integer[20];
        this.hardLevels = new Integer[20];
        Arrays.fill(this.easyLevels, 0);
        Arrays.fill(this.mediumLevels, 0);
        Arrays.fill(this.hardLevels, 0);
    }

    public void setLevelComplete(Difficulty difficulty, Integer level, Integer value) {
        switch (difficulty.toString()) {
            case "EASY":
                this.easyLevels[level-1] = Math.max(this.easyLevels[level-1], value);
                break;

            case "MEDIUM":
                this.mediumLevels[level-1] = Math.max(this.mediumLevels[level-1], value);
                break;

            case "HARD":
                this.hardLevels[level-1] = Math.max(this.hardLevels[level-1], value);
                break;

            default:
                System.out.println("Invalid Input");
        }
    }

    public Integer[] getLevelComplete(Difficulty difficulty) {
        switch (difficulty.toString()) {
            case "EASY":
                return this.easyLevels;

            case "MEDIUM":
                return this.mediumLevels;

            case "HARD":
                return this.hardLevels;

            default:
                System.out.println("Invalid Input");
                return null;
        }
    }

    public double getSoundVolume() {
        return soundVolume.get();
    }

    public DoubleProperty soundVolumeProperty() {
        return soundVolume;
    }

    public void setSoundVolume(double soundVolume) {
        this.soundVolume.set(soundVolume);
    }

    public double getMusicVolume() {
        return musicVolume.get();
    }

    public DoubleProperty musicVolumeProperty() {
        return musicVolume;
    }

    public void setMusicVolume(double musicVolume) {
        this.musicVolume.set(musicVolume);
    }

    public void playMoveBlockSound() {
        this.moveBlockSound.seek(this.moveBlockSound.getStartTime());
        this.moveBlockSound.play();
    }

    public void playButtonPressSound() {
        this.buttonPressSound.seek(this.buttonPressSound.getStartTime());
        this.buttonPressSound.play();
    }

    public Integer[] getEasyLevels() {
        return easyLevels;
    }

    public void setEasyLevels(Integer[] easyLevels) {
        this.easyLevels = easyLevels;
    }

    public Integer[] getMediumLevels() {
        return mediumLevels;
    }

    public void setMediumLevels(Integer[] mediumLevels) {
        this.mediumLevels = mediumLevels;
    }

    public Integer[] getHardLevels() {
        return hardLevels;
    }

    public void setHardLevels(Integer[] hardLevels) {
        this.hardLevels = hardLevels;
    }

    private void applySoundVolumes(Double volume) {
        this.buttonPressSound.setVolume(volume);
        this.moveBlockSound.setVolume(volume);
    }

    @Override
    public String toString() {
        return "SystemSettings{" +
                "soundVolume=" + soundVolume +
                ", musicVolume=" + musicVolume +
                ", moveBlockSound=" + moveBlockSound +
                ", buttonPressSound=" + buttonPressSound +
                ", easyLevels=" + Arrays.toString(easyLevels) +
                ", mediumLevels=" + Arrays.toString(mediumLevels) +
                ", hardLevels=" + Arrays.toString(hardLevels) +
                '}';
    }
}