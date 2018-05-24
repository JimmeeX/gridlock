package gridlock;

import gridlock.model.*;
import gridlock.view.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

/**
 * MainApp is to:
 * - Initialise the Stage/Window to the Menu.
 * - Load SystemSettings from a file, or create a new instance.
 * - Initialise Board Generator Threading.
 */
public class MainApp extends Application{
    private SystemSettings settings;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/Menu.fxml"));
        Parent menuParent = loader.load();
        Scene menuScene = new Scene(menuParent);

        this.initSettings();
        this.settings.playBgMusic();

        MenuController menuController = loader.getController();
        menuController.initData(this.settings);

        primaryStage.setOnCloseRequest(e -> {
            try {closeProgram(primaryStage);}
            catch (Exception exception) {System.out.println("Saving data failed");}
        });

        primaryStage.setTitle("Gridlock");
        primaryStage.setScene(menuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
        startThreading();
    }

    /**
     * Initialises SystemSetting object. If "save.data" exists, read from that file. If not, create a new SystemSettings object.
     * @throws Exception IOException: If file is not found.
     */
    private void initSettings() throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("src/gridlock/resources/save.data")))) {
            this.settings = (SystemSettings) ois.readObject();
            this.settings.initSounds(0.5, 0.0);
            this.settings.setBoardGenerator(new GameBoardGenerator2());
            System.out.println("Data successfully loaded.");
        }

        // If Reading Failed, Create new File
        catch (IOException e) {
            System.out.println("No save file found. Creating new File.");
            this.settings = new SystemSettings(0.5,0.0);
        }
    }

    /**
     * Handles if the User clicks on the "X" on the top right of the app. Will save settings data to "save.data"
     * @param stage Primary Stage
     * @throws Exception IOException to handle files.
     */
    private void closeProgram(Stage stage) throws Exception {
        this.settings.getBG().stopThread();
        // Save Data
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("src/gridlock/resources/save.data")))) {
            oos.writeObject(this.settings);
        }
        stage.close();
    }

    /**
     * Initialise Multithreading Level Generator.
     */
    public void startThreading() {
        // Make global thread soon....
        Thread levGen = new Thread(this.settings.getBG());
        levGen.start();
    }

    /**
     * Launches the App Window
     * @param args
     */
    public static void main(String[] args) {
        //GameBoardGenerator2 bg = new GameBoardGenerator2();
        /*for (int i= 1; i<=20; i++) {
            Board b = bg.generateOneBoard("src/gridlock/resources/easy/" + i + ".txt");
        }*/
        /*
        //EASY
        GameBoard easyStart = bg.generateAPuzzle(Difficulty.EASY);
        System.out.println("EASY = ");
        easyStart.printGrid();

        //MEDIUM
        GameBoard medStart = bg.generateAPuzzle(Difficulty.MEDIUM);
        System.out.println("MEDIUM = ");
        medStart.printGrid();

        //HARD
        GameBoard hardStart = bg.generateAPuzzle(Difficulty.HARD);
        System.out.println("HARD = ");
        hardStart.printGrid();*/
        launch(args);
    }

}
