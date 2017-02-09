package AgarIO;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class AgarIOView extends Application implements Initializable{

    public static boolean active = false;

    @FXML private ToggleButton enableToggle;
    @FXML private Button settingsButton;
    @FXML private AnchorPane mainView;
    @FXML private Circle enabledCircle;

    public AgarIOView()
    {
        initView();
    }

    private void initView()
    {
        launch("");
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("mainView.fxml"));
        primaryStage.setTitle("AgarIO Controller");
        primaryStage.setScene(new Scene(root, 324, 53));
        primaryStage.setX(-405);
        primaryStage.setY(254);
        primaryStage.show();
    }

    //TODO move to controller

    @FXML
    public void handleEnableToggle(ActionEvent event)
    {
        toggleEnableButton();
    }

    public void toggleEnableButton()
    {
        if(active)
        {
            enableToggle.setSelected(false);
            setActive(false);
        }else
        {
            enableToggle.setSelected(true);
            setActive(true);
        }
    }

    public void setActive(boolean enable)
    {
        if(enable)
        {
            enabledCircle.setFill(Color.LIGHTGREEN);
            active = true;
        }else
        {
            enabledCircle.setFill(Color.RED);
            active = false;
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainView.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                    switch (ke.getCode())
                    {
                        case CONTROL:
                            toggleEnableButton();
                            break;
                        case ESCAPE:
                            System.exit(0);
                        default:
                            break;
                    }
                    ke.consume(); // <-- stops passing the event to next node
            }
        });
        settingsButton.setDisable(true);
    }
}
