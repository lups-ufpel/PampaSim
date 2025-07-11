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

import java.util.Optional;

public class CreateProcessDialogService implements DialogService<CreateProcessRecord> {


    @Getter
    @Setter
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
            CreateProcessRecord userInput = new CreateProcessRecord(
                    viewTuple.getViewModel().getProcessStart(),
                    viewTuple.getViewModel().getProcessDuration(),
                    viewTuple.getViewModel().getProcessPriority(),
                    viewTuple.getViewModel().convertColor(), null);
            return Optional.of(userInput);
        }
        return Optional.empty();
    }
}