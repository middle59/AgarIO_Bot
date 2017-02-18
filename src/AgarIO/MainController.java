package AgarIO;

import javafx.application.Application;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable{

    @FXML private ToggleButton enableToggle;
    @FXML private Button settingsButton;
    @FXML private AnchorPane mainView;
    @FXML private Circle enabledCircle;

    @FXML
    public void handleEnableToggle()
    {
        toggleEnableButton();
    }

    @FXML
    public void handleSettingsButton()
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settingsView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            SettingsController settingsController = fxmlLoader.getController();
            settingsController.setMainController(this);
            Stage stage = new Stage();
            stage.setTitle("Settings");
            stage.setX(-405);
            stage.setY(354);
            stage.setScene(new Scene(root1));
            stage.show();
            setDisableAllButtons(true);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void toggleEnableButton()
    {
        if(AgarIOManager.active)
        {
            enableToggle.setSelected(false);
            setActive(false);
        }else
        {
            enableToggle.setSelected(true);
            setActive(true);
        }
    }

    public void setDisableAllButtons(boolean disable)
    {
        enableToggle.setDisable(disable);
        settingsButton.setDisable(disable);
    }

    public void setActive(boolean enable)
    {
        if(enable)
        {
            enabledCircle.setFill(Color.LIGHTGREEN);
            AgarIOManager.active = true;
            settingsButton.setDisable(true);
        }else
        {
            enabledCircle.setFill(Color.RED);
            AgarIOManager.active = false;
            settingsButton.setDisable(false);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
            mainView.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
                public void handle(KeyEvent ke) {
                    switch (ke.getCode()) {
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
    }
}
