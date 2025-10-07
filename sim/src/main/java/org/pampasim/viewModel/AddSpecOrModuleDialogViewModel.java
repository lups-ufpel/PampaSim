package org.pampasim.viewModel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.paint.Color;

import java.util.List;

public class AddSpecOrModuleDialogViewModel implements ViewModel {
    private final ObservableList<String> moduleName = FXCollections.observableArrayList();
    private final StringProperty selectedModule = new SimpleStringProperty();


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
