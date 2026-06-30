package org.pampasim.view.pcb;

import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;
import org.pampasim.resources.view.PCBView;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.Set;

public class Basics implements PCBView.ModulePCBView {
    @InjectViewModel
    private ProcessViewModel viewModel;

    @FXML
    private Label pidLabel;
    @FXML public Label colorLabel;
    @FXML private Label stateLabel;
    @FXML private Label arrivalTickLabel;
    @FXML private Label priorityLabel;
    @FXML private Label readyWaitingTime;
    @FXML private Label currExecTimeLabel;
    @FXML private Label burstLabel;
    @FXML private Tab PCBTab;

    public void initialize() {
        pidLabel.textProperty().bind(viewModel.getPid().asString());
        Color color = viewModel.getColorProperty().get();
        if (color != null) {
            String hex = toHex(color);
            colorLabel.setText(hex);
            colorLabel.setStyle("-fx-background-color: " + hex + "; -fx-text-fill: " + getTextColorForBackground(color) + ";");
        }
        stateLabel.textProperty().bind(viewModel.stateProperty().asString());
        arrivalTickLabel.textProperty().bind(viewModel.getArrivalTick().asString());
        priorityLabel.textProperty().bind(viewModel.getPriority().asString());
        readyWaitingTime.textProperty().bind(viewModel.getReadyWaitingTime().asString());
        currExecTimeLabel.textProperty().bind(viewModel.getCurrExecTime().asString());
        burstLabel.textProperty().bind(viewModel.getBurst().asString());
    }
    private String toHex(Color color) {
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    private String getTextColorForBackground(Color color) {
        double luminance = 0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue();
        return luminance < 0.5 ? "white" : "black";
    }

    public Set<Tab> getTabs() {
        return Set.of(PCBTab);
    }
}
