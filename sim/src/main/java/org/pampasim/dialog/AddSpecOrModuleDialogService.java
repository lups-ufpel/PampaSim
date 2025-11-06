package org.pampasim.dialog;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.pampasim.core.dialog.DialogService;
import org.pampasim.view.AddSpecOrModulesDialogView;
import org.pampasim.viewModel.AddSpecOrModuleDialogViewModel;

//temp
//temp

import java.util.List;
import java.util.Optional;

public class AddSpecOrModuleDialogService implements DialogService<AddModulesRecord> {

    List<String> availableSchedulers;

    @Override
    public Optional<AddModulesRecord> showDialog(Object ... args) {
        ViewTuple<AddSpecOrModulesDialogView, AddSpecOrModuleDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(AddSpecOrModulesDialogView.class).load();


        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);

        if(args.length > 0 && args[0] instanceof List<?> rawList) {
            List<String> moduleNames = (List<String>) rawList;
            viewTuple.getViewModel().setModuleNames(moduleNames);

        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.APPLY && viewTuple.getViewModel().didUserSetModule()) {
            AddModuleRecord userSelection = new AddModuleRecord(
            viewTuple.getViewModel().getSelectedModule());
            return Optional.of(userSelection);
        }
        return Optional.empty();
    }
}

