package gridlock.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;

public class SystemSettings implements Serializable {
    private transient DoubleProperty soundVolume;
    private transient DoubleProperty musicVolume;

    private transient MediaPlayer moveBlockSound;
    private transient MediaPlayer buttonPressSound;
    private transient MediaPlayer victorySound;
    private transient MediaPlayer bgMusic;

    // TODO: Do highscore instead of 0,1,2,3
    private Integer[] easyLevels;
    private Integer[] mediumLevels;
    private Integer[] hardLevels;

    private transient GameBoardGenerator bg; // 28, 76, 177, 185; mainapp 30

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

        Media victoryMedia = new Media(new File("src/gridlock/static/audio/win_level_1.wav").toURI().toString());
        this.victorySound = new MediaPlayer(victoryMedia);
        this.victorySound.setVolume(this.soundVolume.getValue());

        Media bgMedia = new Media(new File("src/gridlock/static/audio/Limes-Jovial.mp3").toURI().toString());
        this.bgMusic = new MediaPlayer(bgMedia);
        this.bgMusic.setVolume(this.musicVolume.getValue());
        this.bgMusic.setCycleCount(MediaPlayer.INDEFINITE);

        // Initialise Volume Listeners
        this.soundVolume.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                applySoundVolumes(newValue.doubleValue());
            }
        });

        this.musicVolume.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                applyMusicVolumes(newValue.doubleValue());
            }
        });

        // Initialise to 20 0's
        this.easyLevels = new Integer[20];
        this.mediumLevels = new Integer[20];
        this.hardLevels = new Integer[20];

        // Initialise the BoardGenerator
        this.bg = new GameBoardGenerator(); // Trial... (not commited yet)

        this.resetProgress();
    }

    public void initSounds(Double soundVolume, Double musicVolume) {
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

        Media victoryMedia = new Media(new File("src/gridlock/static/audio/win_level_1.wav").toURI().toString());
        this.victorySound = new MediaPlayer(victoryMedia);
        this.victorySound.setVolume(this.soundVolume.getValue());

        Media bgMedia = new Media(new File("src/gridlock/static/audio/Limes-Jovial.mp3").toURI().toString());
        this.bgMusic = new MediaPlayer(bgMedia);
        this.bgMusic.setVolume(this.musicVolume.getValue());
        this.bgMusic.setCycleCount(MediaPlayer.INDEFINITE);

        // Initialise Volume Listeners
        this.soundVolume.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                applySoundVolumes(newValue.doubleValue());
            }
        });

        this.musicVolume.addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                applyMusicVolumes(newValue.doubleValue());
            }
        });

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

    public void resetProgress() {
        Arrays.fill(this.easyLevels, 0);
        Arrays.fill(this.mediumLevels, 0);
        Arrays.fill(this.hardLevels, 0);
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

    public void setBoardGenerator(GameBoardGenerator newBG) {
        this.bg = newBG;
    }

    public double getMusicVolume() {
        return musicVolume.get();
    }

    public GameBoardGenerator getBG() { return this.bg; }

    public GameBoard getEasy() { return this.bg.getEasy(); }

    public GameBoard getMedium() { return this.bg.getMedium(); }

    public GameBoard getHard() { return this.bg.getHard(); }

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

    public void playVictorySound() {
        this.victorySound.seek(this.victorySound.getStartTime());
        this.victorySound.play();
    }

    public void playBgMusic() {
        this.bgMusic.play();
    }

    private void applySoundVolumes(Double volume) {
        this.buttonPressSound.setVolume(volume);
        this.moveBlockSound.setVolume(volume);
        this.victorySound.setVolume(volume);
    }

    private void applyMusicVolumes(Double volume) {
        this.bgMusic.setVolume(volume);
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
