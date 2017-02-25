package AgarIO.JavaFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class MainView extends Application{

    public MainView() {}

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainView.fxml"));
        primaryStage.setTitle("AgarIO Controller");
        primaryStage.setScene(new Scene(root, 324, 40));
        primaryStage.setX(-405);
        primaryStage.setY(254);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}
