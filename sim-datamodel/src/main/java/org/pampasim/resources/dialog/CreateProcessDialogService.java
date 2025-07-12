package org.pampasim.resources.dialog;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.core.dialog.DialogService;
import org.pampasim.resources.view.CreateProcessDialogView;
import org.pampasim.resources.viewmodel.CreateProcessDialogViewModel;
import org.pampasim.resources.viewmodel.ProcessMemoryInfoViewModel;

import java.util.ArrayList;
import java.util.Optional;

@Setter
@Getter
public class CreateProcessDialogService implements DialogService<CreateProcessRecord> {


    private boolean memoryModulePresent = false;

    @Override
    public Optional<CreateProcessRecord> showDialog(Object ... args) {
        ViewTuple<CreateProcessDialogView, CreateProcessDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(CreateProcessDialogView.class).load();

        viewTuple.getViewModel().setMemoryModulePresent(memoryModulePresent);

        

        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.APPLY) {
            CreateProcessDialogViewModel vm = viewTuple.getViewModel();
            ProcessMemoryInfoRecord memoryInfoRecord = null;

            if (vm.isMemoryModulePresent()) {
                ProcessMemoryInfoViewModel mem = vm.getMemoryInfo();
                memoryInfoRecord = new ProcessMemoryInfoRecord(
                        mem.getProcessSize(),
                        new ArrayList<>(mem.getFileBackedPages()),
                        new ArrayList<>(mem.getMemoryAccesses()),
                        new ArrayList<>(mem.getModifiesPage()),
                        mem.getLoopAccessList()
                );
            }
            CreateProcessRecord userInput = new CreateProcessRecord(
                    viewTuple.getViewModel().getProcessStart(),
                    viewTuple.getViewModel().getProcessDuration(),
                    viewTuple.getViewModel().getProcessPriority(),
                    viewTuple.getViewModel().convertColor(),
                    memoryInfoRecord);
            return Optional.of(userInput);
        }
        return Optional.empty();
    }
}