package org.pampasim.dialog;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.pampasim.core.dialog.DialogService;
import org.pampasim.view.AddModuleDialogView;
import org.pampasim.viewModel.AddModuleDialogViewModel;

import java.util.List;
import java.util.Optional;

public class AddModuleDialogService implements DialogService<AddModuleRecord> {

    List<String> availableSchedulers;

    @Override
    public Optional<AddModuleRecord> showDialog(Object ... args) {

        ViewTuple<AddModuleDialogView, AddModuleDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(AddModuleDialogView.class).load();

        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);

        if(args.length > 0 && args[0] instanceof List<?> rawList) {
            List<String> moduleNames = (List<String>) rawList;
            viewTuple.getViewModel().setModuleNames(moduleNames);

        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.APPLY) {
            // handle data from select scheduler.
            AddModuleRecord userSelection = new AddModuleRecord(
                    viewTuple.getViewModel().getSelectedModule());
            return Optional.of(userSelection);
        }
        return Optional.empty();
    }
}

