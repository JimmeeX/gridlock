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

/**
 * SystemSettings is like a mini database when a user runs the game. Contains sounds, music, level progress, and background Board Generation.
 * Some settings can be customised through Menu -> Settings
 */
public class SystemSettings implements Serializable {
    private transient DoubleProperty soundVolume;
    private transient DoubleProperty musicVolume;

    private transient MediaPlayer moveBlockSound;
    private transient MediaPlayer buttonPressSound;
    private transient MediaPlayer victorySound;
    private transient MediaPlayer bgMusic;

    private Integer[] easyLevels;
    private Integer[] mediumLevels;
    private Integer[] hardLevels;

    private transient GameBoardGenerator bg;

    /**
     * Constructor
     * Initialise Sounds, Media Players
     * Initialise Levels to Incomplete
     * Initialise Board Generator
     * @param soundVolume Initial Sound Volume
     * @param musicVolume Initial Music Volume
     */
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
        this.bg = new GameBoardGenerator();

        this.resetProgress();
    }

    /**
     * Secondary constructor to just initialise sounds. Used when level data has been loaded on startup.
     * @param soundVolume Initial Sound Volume
     * @param musicVolume Initial Music Volume
     */
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

    /**
     * Mark a specific CAMPAIGN level as complete.
     * @param difficulty Difficulty of the Level
     * @param level Level Number
     * @param value 1: Bronze; 2: Silver; 3: Gold
     */
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

    /**
     * Retrieve the result for a specific set of CAMPAIGN levels
     * @param difficulty Difficulty Mode.
     * @return List of Results (GOLD, SILVER, BRONZE) for specified difficulty.
     */
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

    /**
     * Resets progress of the CAMPAIGN levels
     */
    public void resetProgress() {
        Arrays.fill(this.easyLevels, 0);
        Arrays.fill(this.mediumLevels, 0);
        Arrays.fill(this.hardLevels, 0);
    }

    /**
     * Getter: Sound Volume {0 ~ 1}
     * @return Double: Sound Volume
     */
    public double getSoundVolume() {
        return soundVolume.get();
    }

    /**
     * Setter: Sound Volume (0 ~ 1)
     * @param soundVolume Double: Sound Volume
     */
    public void setSoundVolume(double soundVolume) {
        this.soundVolume.set(soundVolume);
    }

    /**
     * Setter: Set Board Generator.
     * It is used here to connect to the front end easily.
     * @param newBG Board Generator
     */
    public void setBoardGenerator(GameBoardGenerator newBG) {
        this.bg = newBG;
    }

    /**
     * Getter: Music Volume {0 ~ 1}
     * @return Double: Music Volume
     */
    public double getMusicVolume() {
        return musicVolume.get();
    }

    /**
     * Setter: Music Volume (0 ~ 1)
     * @param musicVolume Double: Music Volume
     */
    public void setMusicVolume(double musicVolume) {
        this.musicVolume.set(musicVolume);
    }

    /**
     * Getter: Board Generator
     * @return BoardGenerator
     */
    public GameBoardGenerator getBG() { return this.bg; }

    /**
     * Getter
     * @return List of easy level results
     */
    public GameBoard getEasy() { return this.bg.getEasy(); }

    /**
     * Getter
     * @return List of medium level results
     */
    public GameBoard getMedium() { return this.bg.getMedium(); }

    /**
     * Getter
     * @return List of hard level results
     */
    public GameBoard getHard() { return this.bg.getHard(); }

    /**
     * Sound played when mouse drags the blocks around in the game interface.
     */
    public void playMoveBlockSound() {
        this.moveBlockSound.seek(this.moveBlockSound.getStartTime());
        this.moveBlockSound.play();
    }

    /**
     * Sound played when a button is pressed.
     */
    public void playButtonPressSound() {
        this.buttonPressSound.seek(this.buttonPressSound.getStartTime());
        this.buttonPressSound.play();
    }

    /**
     * Sound played when a level has been solved.
     */
    public void playVictorySound() {
        this.victorySound.seek(this.victorySound.getStartTime());
        this.victorySound.play();
    }

    /**
     * Play some background music.
     */
    public void playBgMusic() {
        this.bgMusic.play();
    }

    /**
     * Apply soundVolume changes to the actual sounds.
     * @param volume New Sound Volume (0~1)
     */
    private void applySoundVolumes(Double volume) {
        this.buttonPressSound.setVolume(volume);
        this.moveBlockSound.setVolume(volume);
        this.victorySound.setVolume(volume);
    }

    /**
     * Apply musicVolume changes to the actual music.
     * @param volume New Music Volume (0~1)
     */
    private void applyMusicVolumes(Double volume) {
        this.bgMusic.setVolume(volume);
    }

    /**
     * String Method
     * @return String representation of SystemSettings
     */
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
