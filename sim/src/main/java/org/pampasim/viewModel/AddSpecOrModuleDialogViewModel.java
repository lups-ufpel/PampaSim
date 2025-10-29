package org.pampasim.viewModel;
import org.pampasim.dialog.AddModuleRecord;
import org.pampasim.dialog.AddModuleDialogService;


import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Optional;

public class AddSpecOrModuleDialogViewModel implements ViewModel {
    private final ObservableList<String> moduleName = FXCollections.observableArrayList();
    private final StringProperty selectedModule = new SimpleStringProperty();

    public AddModuleRecord  openAddModuleDialog(AddModuleDialogService addModuleDialogService){
    //List<String> modules = simulatedScenario.getSpec().listAvailableModules();
      Optional<AddModuleRecord> userSelection = addModuleDialogService.showDialog(moduleName);
      return userSelection.orElse(null);
    }

    public void setModuleNames(List<String> names) {
        moduleName.setAll(names);
        setDefaultSelectedModule(names);
    }
    public void setDefaultSelectedModule(List<String> names) {
        if(!names.isEmpty()) {
            selectedModule.set(names.getFirst());
        }
    }

    public StringProperty selectedModuleProperty() {
        return selectedModule;
    }
    public ObservableList<String> moduleNameProperty() { return moduleName; }
    public String getSelectedModule() { return selectedModule.get(); }

}
