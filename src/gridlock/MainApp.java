package gridlock;

import gridlock.model.*;
import gridlock.view.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/Menu.fxml"));
        Parent menuParent = loader.load();
        Scene menuScene = new Scene(menuParent);

        SystemSettings settings;
        // Initialise Settings
        // Try Reading from Serialized Data
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("src/gridlock/resources/save.data")))) {
            settings = (SystemSettings) ois.readObject();
            settings.initSounds(1.0, 1.0);
            System.out.println("Data successfully loaded.");
        }

        // If Reading Failed, Create new File
        catch (IOException e) {
            System.out.println("No save file found. Creating new File.");
            settings = new SystemSettings(1.0,1.0);
        }

        MenuController menuController = loader.getController();
        menuController.initData(settings);

        primaryStage.setTitle("Gridlock");
        primaryStage.setScene(menuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        EndBoardGenerator ebg = new EndBoardGenerator();
        Board b = null;
        while (b == null) b = ebg.newEndBoard();
        b.printGrid();
        launch(args);
    }
}
