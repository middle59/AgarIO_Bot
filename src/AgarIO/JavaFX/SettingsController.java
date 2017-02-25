package AgarIO.JavaFX;

import AgarIO.AgarIOManager;
import AgarIO.Grid.ScreenConfiguration;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController  implements Initializable{

    private MainController mainController;
    private AIController aiController;
    @FXML private CheckBox enableDisplay;
    @FXML private ChoiceBox configurationBox;
    @FXML private Button aiSettingsButton;
    @FXML private Button applyButton;
    @FXML private Button closeButton;

    public SettingsController() {}

    @FXML
    public void handleAISettings()
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("aiView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            aiController = fxmlLoader.getController();
            aiController.setSettingsController(this);
            Stage stage = new Stage();
            stage.setTitle("AI Settings");
            stage.setX(-405);
            stage.setY(354);
            stage.setScene(new Scene(root1));
            stage.show();
            setDisableAIButton(true);
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeChildStages()
    {
        if(aiController != null)
        {
            aiController.handleCloseButton();
        }
    }

    public void setDisableAIButton(boolean disable)
    {
        aiSettingsButton.setDisable(disable);
    }

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
