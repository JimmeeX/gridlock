package gridlock;

import gridlock.model.Board;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("view/Menu.fxml"));

        primaryStage.setTitle("Gridlock");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
<<<<<<< HEAD
=======
        //I CHANGED THIS FROM GRIDLOCK TO BOARD - ALINA
        Board gl = new Board();
        gl.process("easy.txt");
>>>>>>> bc0464c7f4b681089a9da70c55269e4c7e87bf6c
        launch(args);
    }

}
