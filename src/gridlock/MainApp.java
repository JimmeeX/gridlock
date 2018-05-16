package gridlock;

import gridlock.model.*;
import gridlock.view.MenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/Menu.fxml"));
        Parent menuParent = loader.load();
        Scene menuScene = new Scene(menuParent);

        // Initialise Settings
        SystemSettings settings = new SystemSettings(1.0, 1.0);
        MenuController menuController = loader.getController();
        menuController.initData(settings);

        primaryStage.setTitle("Gridlock");
        primaryStage.setScene(menuScene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        long startTime = System.nanoTime();
        BoardGenerator bg = new BoardGenerator();
        Board b = null;
        while (b == null) b = bg.newWinBoard();
        b.printGrid();
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;
        System.out.println("Duration " + duration + "/1000 seconds.");
        launch(args);
    }
}
