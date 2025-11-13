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
    private Optional<String> specPath = Optional.empty();
    @Getter
    @Setter
    private Optional<AddModulesRecord> selectedModulesOpt = Optional.empty();
    @Getter
    private StringProperty modulesFeedbackStringProperty = new SimpleStringProperty("Módulos: Processador");
    @Getter
    private StringProperty specFeedbackStringProperty = new SimpleStringProperty("Cenário: Nenhum");

    public void openAddModuleDialog(AddModulesDialogService addModulesDialogService){
      //List<String> modules = simulatedScenario.getSpec().listAvailableModules();
      selectedModulesOpt = addModulesDialogService.showDialog(moduleName);
      if(selectedModulesOpt.isPresent()){
        for(String s : selectedModulesOpt.get().modules())
        {
          String newModule = ", ";
          if(s.equals("memory")){ // manual translation :(
            newModule += "Memória";
          } else{
            throw new IllegalStateException("Missing translation for module " + s);
          }

          modulesFeedbackStringProperty.set(modulesFeedbackStringProperty.get() + newModule); 
        }
      }
    }

    public void setSpecPath(Optional<String> specPath){
      this.specPath = specPath;

      if(specPath.isPresent()){
        specFeedbackStringProperty.set("Cenário: Carregado");
      }
    }

    public void setModuleNames(List<String> names) {
        moduleName.setAll(names);
    }

}
