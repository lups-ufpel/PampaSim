package org.pampasim.viewModel;
import jakarta.xml.bind.JAXBElement;
import lombok.Getter;
import org.pampasim.SpecEntityDataMismatch;
import org.pampasim.core.utils.KVPair;
import org.pampasim.dsl.spec.Spec;
import org.pampasim.dialog.AddModulesRecord;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.pampasim.entity.schedulers.RespectsQuantum;
import org.pampasim.memory.MemoryModule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public class AddSpecOrModulesDialogViewModel implements ViewModel {
    private final ObservableList<String> moduleName = FXCollections.observableArrayList();
    @Getter
    private Optional<org.pampasim.core.utils.KVPair<String, Spec>> specInfo = Optional.empty();
    @Getter
    private StringProperty specFeedbackStringProperty = new SimpleStringProperty("Cenário: Nenhum");
    @Getter
    private final BooleanProperty memoryModuleEnabled = new SimpleBooleanProperty(false);

    public BooleanProperty memoryModuleEnabledProperty() {
        return memoryModuleEnabled;
    }
    public void loadSpec(Optional<String> specPath){
      specPath.ifPresent(path -> {
          Spec spec = null;
          try {
              spec = Spec.loadSpec(new FileInputStream(path));
          } catch (FileNotFoundException e) {
              throw new RuntimeException(e);
          }
          var specDesc = "Cenário: ";
          var schedulerFQCN = spec.getSchedulerConfig().getFullyQualifiedClassName();
          if (schedulerFQCN == null) {
              throw new RuntimeException("null schedulerFQCN!");
          }
          var schedulerObj = org.pampasim.util.FlywheelData.getSchedulerObjByFQCNMap().get(schedulerFQCN);
          specDesc += schedulerObj.shortDescription();
          if (schedulerObj instanceof RespectsQuantum respectsQuantum) {
              try {
                  respectsQuantum.applySpecEntityConfig(spec.getSchedulerConfig());
              } catch (SpecEntityDataMismatch e) {
                  throw new RuntimeException(e);
              }
              var quantum = respectsQuantum.getQuantum();
              specDesc += " com quantum " + quantum;
          }
          var numModules = spec.getInnerSpec().getExtraModules().getModule().size() + 1;
          specDesc += ", e "
                  + numModules
                  + " "
                  + ((numModules > 1)? "módulos" : "módulo");
          specFeedbackStringProperty.set(specDesc);
          specInfo = Optional.of(new KVPair<>(path, spec));
      });
    }

    public Optional<AddModulesRecord> getSelectedModulesOpt(){
      if(memoryModuleEnabled.get()){
          AddModulesRecord userSelection = new AddModulesRecord(List.of(MemoryModule.class.getCanonicalName()));
            return Optional.of(userSelection);
      }

      return Optional.empty();
    }

    public void setModuleNames(List<String> names) {
        moduleName.setAll(names);
    }

}
