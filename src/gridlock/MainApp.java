package gridlock;

import gridlock.model.Gridlock;
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
        Gridlock gl = new Gridlock();
        gl.process("easy.txt");
        launch(args);
    }

}
