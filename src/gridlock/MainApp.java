package gridlock;

import gridlock.model.SystemSettings;
import gridlock.view.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/Menu.fxml"));
        Parent menuParent = loader.load();
        Scene menuScene = new Scene(menuParent);

        // Initialise Settings
        SystemSettings settings = new SystemSettings(0.5, 0.5);
        MenuController menuController = loader.getController();
        menuController.initData(settings);

        primaryStage.setTitle("Gridlock");
        primaryStage.setScene(menuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        //BoardSimulation bs = new BoardSimulation();
        //bs.playGame();
        launch(args);
    }
}
