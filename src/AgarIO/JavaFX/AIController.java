package AgarIO.JavaFX;

import AgarIO.AI.AbstractAI;
import AgarIO.AgarIOManager;
import AgarIO.Grid.ScreenConfiguration;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AIController implements Initializable{

    private SettingsController settingsController;
    private List<AbstractAI> aiList;
    @FXML private ChoiceBox configurationBox;
    @FXML private Label descriptionTextLabel;
    @FXML private Button applyButton;
    @FXML private Button closeButton;

    public AIController() {}

    @FXML
    public void handleCloseButton()
    {
        if(settingsController!=null)
        {
            settingsController.setDisableAIButton(false);
        }
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    public void setSettingsController(SettingsController settingsController)
    {
        this.settingsController = settingsController;
    }

    @FXML
    public void handleApplyButton()
    {
        AgarIOManager.getSnapshotDecisionAid().activeAI = aiList.get(configurationBox.getSelectionModel().getSelectedIndex());
        handleCloseButton();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        descriptionTextLabel.setWrapText(true);
        this.aiList = AgarIOManager.getSnapshotDecisionAid().aiList;
        for(AbstractAI aiType : aiList) {
            configurationBox.getItems().add(aiType.AI_Name);
        }
        if(configurationBox.getItems().size() > 0)
        {
            AbstractAI activeAI = AgarIOManager.getSnapshotDecisionAid().activeAI;
            int activeAIIndex = aiList.indexOf(activeAI);
            configurationBox.setValue(configurationBox.getItems().get(activeAIIndex));
            descriptionTextLabel.setText(aiList.get(activeAIIndex).description);
        }

        configurationBox.getSelectionModel().selectedIndexProperty().addListener(
                new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        descriptionTextLabel.setText(aiList.get(newValue.intValue()).description);
                    }
                }
        );

    }
}
