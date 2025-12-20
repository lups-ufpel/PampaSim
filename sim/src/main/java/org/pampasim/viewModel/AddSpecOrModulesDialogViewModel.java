package org.pampasim.viewModel;
import lombok.Getter;
import org.pampasim.dialog.AddModulesRecord;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class AddSpecOrModulesDialogViewModel implements ViewModel {
    private final ObservableList<String> moduleName = FXCollections.observableArrayList();
    @Getter
    private Optional<String> specPath = Optional.empty();
    @Getter
    private StringProperty specFeedbackStringProperty = new SimpleStringProperty("Cenário: Nenhum");
    @Getter
    private final BooleanProperty memoryModuleEnabled = new SimpleBooleanProperty(false);
    @Getter
    private final BooleanProperty fileSystemModuleEnabled = new SimpleBooleanProperty(false);

    public BooleanProperty memoryModuleEnabledProperty() {
        return memoryModuleEnabled;
    }
    public BooleanProperty fileSystemModuleEnabledProperty() {
        return fileSystemModuleEnabled;
    }
    public void setSpecPath(Optional<String> specPath){
      this.specPath = specPath;

      if(specPath.isPresent()){
        specFeedbackStringProperty.set("Cenário: Carregado");
      }
    }

    public Optional<AddModulesRecord> getSelectedModulesOpt(){
      List<String> modules = new ArrayList<>();
      if(memoryModuleEnabled.get()){
        modules.add("memory");
      }

      if(fileSystemModuleEnabled.get()){
        modules.add("filesystem");
      }

      if(modules.size() > 0){
        AddModulesRecord userSelection = new AddModulesRecord(modules);
        return Optional.of(userSelection);

      }  else{

        return Optional.empty();
      }

    }

    public void setModuleNames(List<String> names) {
        moduleName.setAll(names);
    }

}
