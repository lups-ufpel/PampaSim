package org.pampasim.resources.view;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.FxmlView;
import de.saxsys.mvvmfx.InjectViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.utils.notifications.NotificationObserver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.resources.Process;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class ProcessInspectorView implements FxmlView<ProcessViewModel> {
    private final Logger LOGGER = LogManager.getLogger(ProcessInspectorView.class);
    @InjectViewModel
    private ProcessViewModel viewModel;
    @FXML
    private BorderPane mainBorderPane;

    private static final Map<Stage, ProcessInspectorView> openInspectors = new HashMap<>();

    private PCBView pcbView;
    private final NotificationObserver closeObserver = (key, payload) -> close();

    public void initialize() {
        var pcb = FluentViewLoader
                .fxmlView(PCBView.class)
                .viewModel(viewModel)
                .load();
        pcbView = pcb.getCodeBehind();
        mainBorderPane.setCenter(pcb.getView());
        viewModel.subscribe("CloseInspectors", closeObserver);
    }

    static void showFor(ProcessViewModel viewModel) {
        for (var entry : openInspectors.entrySet()) {
            if (entry.getValue().viewModel == viewModel) {
                entry.getKey().requestFocus();
                return;
            }
        }

        var inspector = loadInspector(viewModel);
        var stage = new Stage();
        stage.setTitle("Informações do Processo");
        stage.setScene(new Scene(inspector.getView()));
        stage.setResizable(true);
        openInspectors.put(stage, inspector.getCodeBehind());
        stage.setOnHidden(event -> {
            var closedInspector = openInspectors.remove(stage);
            if (closedInspector != null) {
                closedInspector.dispose();
            }
        });
        stage.show();
    }

    public static void rebindOpenInspectors(Collection<ProcessViewModel> processes) {
        if (openInspectors.isEmpty()) {
            return;
        }

        Map<Process.CreationData, ProcessViewModel> replacements = new IdentityHashMap<>();
        for (var process : processes) {
            replacements.put(process.getCreationData(), process);
        }

        for (var entry : openInspectors.entrySet()) {
            var current = entry.getValue();
            var replacement = replacements.get(current.viewModel.getCreationData());
            if (replacement == null || replacement == current.viewModel) {
                continue;
            }

            var inspector = loadInspector(replacement);
            current.dispose();
            entry.getKey().getScene().setRoot(inspector.getView());
            entry.setValue(inspector.getCodeBehind());
        }
    }

    private static ViewTuple<ProcessInspectorView, ProcessViewModel> loadInspector(ProcessViewModel viewModel) {
        return FluentViewLoader.fxmlView(ProcessInspectorView.class).viewModel(viewModel).load();
    }

    private void dispose() {
        viewModel.unsubscribe("CloseInspectors", closeObserver);
        pcbView.dispose();
        mainBorderPane.setCenter(null);
    }

    public void delete(ActionEvent actionEvent) {
        viewModel.delete(); // publishes CloseInspectors
        LOGGER.debug("called delete on {}", this);
    }

    public void edit(ActionEvent actionEvent) {
        close();
        viewModel.edit();
        LOGGER.debug("called edit on {}", this);
    }

    public void ok(ActionEvent actionEvent) {
        LOGGER.debug("called ok on {}", this);
        close();
    }

    public void close() {
        Stage stage = (Stage) (this.mainBorderPane.getScene()).getWindow();
        stage.close();
    }
}
