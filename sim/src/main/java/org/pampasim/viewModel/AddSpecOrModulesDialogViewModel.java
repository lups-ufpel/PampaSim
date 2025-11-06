package org.pampasim.viewModel;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.dialog.AddModulesRecord;
import org.pampasim.dialog.AddModulesDialogService;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.pampasim.dialog.AddSpecOrModulesRecord;

import java.util.List;
import java.util.Optional;

public class AddSpecOrModulesDialogViewModel implements ViewModel {
    private final ObservableList<String> moduleName = FXCollections.observableArrayList();
    @Getter
    @Setter
    private Optional<String> specPath = Optional.empty();
    @Getter
    @Setter
    private Optional<AddModulesRecord> selectedModulesOpt = Optional.empty();
    private boolean didUserSetModule = false; // seems like total jank but it works

    public void openAddModuleDialog(AddModulesDialogService addModulesDialogService){
      //List<String> modules = simulatedScenario.getSpec().listAvailableModules();
      selectedModulesOpt = addModulesDialogService.showDialog(moduleName);
    }

    public void setModuleNames(List<String> names) {
        moduleName.setAll(names);
    }
}
