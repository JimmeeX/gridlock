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
        long startTime = System.nanoTime();
        BoardGenerator bg = new BoardGenerator();
        Board b = bg.generateOneBoard();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;
        System.out.println("Duration " + duration + "/1000 seconds.");
        launch(args);
    }
}
