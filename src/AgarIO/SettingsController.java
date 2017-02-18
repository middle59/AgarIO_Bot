package AgarIO;

import AgarIO.Grid.ScreenConfiguration;
import javafx.application.Application;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Observable;
import java.util.ResourceBundle;

public class SettingsController  implements Initializable{

    private MainController mainController;
    @FXML private CheckBox enableDisplay;
    @FXML private ChoiceBox configurationBox;
    @FXML private Button applyButton;
    @FXML private Button closeButton;

    public SettingsController() {}

    @FXML
    public void handleCloseButton()
    {
        //setDisableAllButtons(false);
        if(mainController!=null)
        {
            mainController.setDisableAllButtons(false);
        }
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void setMainController(MainController mainController)
    {
        this.mainController = mainController;
    }

    @FXML
    public void handleApplyButton()
    {
        AgarIOManager.setConfigurationByName((String) configurationBox.getValue());
        AgarIOManager.ENABLE_DISPLAY = enableDisplay.isSelected();
        AgarIOManager.showDisplayCheck();
        handleCloseButton();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for(ScreenConfiguration screenConfiguration : AgarIOManager.configurationList) {
            configurationBox.getItems().add(screenConfiguration.getConfigurationName());
        }
        if(configurationBox.getItems().size() > 0 && AgarIOManager.screenConfiguration != null)
        {
            configurationBox.setValue(AgarIOManager.screenConfiguration.getConfigurationName());
        }

        enableDisplay.setSelected(AgarIOManager.ENABLE_DISPLAY);
    }
}
