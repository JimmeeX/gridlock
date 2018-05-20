package gridlock;

import gridlock.model.*;
import gridlock.view.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;

public class MainApp extends Application {
    private SystemSettings settings;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/Menu.fxml"));
        Parent menuParent = loader.load();
        Scene menuScene = new Scene(menuParent);

        // Initialise Settings
        // Try Reading from Serialized Data
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("src/gridlock/resources/save.data")))) {
            this.settings = (SystemSettings) ois.readObject();
            this.settings.initSounds(1.0, 1.0);
            System.out.println("Data successfully loaded.");
        }

        // If Reading Failed, Create new File
        catch (IOException e) {
            System.out.println("No save file found. Creating new File.");
            this.settings = new SystemSettings(1.0,1.0);
        }

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
    }

    private void closeProgram(Stage stage) throws Exception {
        // Save Data
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("src/gridlock/resources/save.data")))) {
            oos.writeObject(this.settings);
        }
        stage.close();
    }

    public static void main(String[] args) {
        BoardGenerator bg = new BoardGenerator();
        //GameBoard b = bg.generateOneBoard("src/gridlock/resources/hard/" + 6 + ".txt");

        //EASY
//        Difficulty d = Difficulty.EASY;
//        GameBoard easy = bg.generateBoard(d);
//        System.out.println("EASY = ");
//        GameBoard easyStart = bg.generateOneBoard(easy, 3, 7);
//        while (easyStart == null) {
//            easy = bg.generateBoard(d);
//            //System.out.println("EASY = ");
//            easyStart = bg.generateOneBoard(easy, 3,7);
//        }
//        easy.printGrid();
//        easyStart.printGrid();

        //MEDIUM
//        Difficulty d = Difficulty.MEDIUM;
//        GameBoard med = bg.generateBoard(d);
//        System.out.println("MEDIUM = ");
//        //med.printGrid();
//        GameBoard medStart = bg.generateOneBoard(med, 8, 13);
//        while (medStart == null) {
//            med = bg.generateBoard(d);
//            //System.out.println("MEDIUM = ");
//            medStart = bg.generateOneBoard(med, 8,13);
//        }
//        med.printGrid();
//        medStart.printGrid();

        //HARD
        Difficulty d = Difficulty.HARD;
        GameBoard hard = bg.generateBoard(d);
        System.out.println("HARD = ");
        GameBoard hardStart = bg.generateOneBoard(hard, 14,20);
        while (hardStart == null) {
            hard = bg.generateBoard(d);
            //System.out.println("HARD = ");
            hardStart = bg.generateOneBoard(hard, 14, 20);
        }
        hard.printGrid();
        hardStart.printGrid();

        launch(args);
    }
}
