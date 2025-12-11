package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.pampasim.resources.viewmodel.MemoryInfoViewModel;
import org.pampasim.resources.viewmodel.PageTableEntryViewModel;
import org.pampasim.resources.viewmodel.PageTableViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.function.Function;

public class ProcessInspectorView implements FxmlView<ProcessViewModel> {

    @InjectViewModel
    private ProcessViewModel viewModel;
    @FXML
    private BorderPane mainBorderPane;

    public void initialize() {
        var pcbView = FluentViewLoader
                .fxmlView(PCBView.class)
                .viewModel(viewModel)
                .load().getView();
        mainBorderPane.setCenter(pcbView);
    }

    public void delete(ActionEvent actionEvent) {
    }

    public void edit(ActionEvent actionEvent) {
        //EditProcessDialogService editProcessDialogService = new EditProcessDialogService();
    }

    public void ok(ActionEvent actionEvent) {
    }
}
