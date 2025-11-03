package org.pampasim.dialog;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.pampasim.core.dialog.DialogService;
import org.pampasim.view.AddSpecOrModuleDialogView;
import org.pampasim.viewModel.AddSpecOrModuleDialogViewModel;
import org.pampasim.viewModel.PampaSimViewModel;

//temp
import org.pampasim.view.AddModuleDialogView;
import org.pampasim.viewModel.AddModuleDialogViewModel;
//temp

import java.util.List;
import java.util.Optional;

public class AddSpecOrModuleDialogService implements DialogService<AddModuleRecord> {

    List<String> availableSchedulers;

    @Override
    public Optional<AddModuleRecord> showDialog(Object ... args) {
        ViewTuple<AddSpecOrModuleDialogView, AddSpecOrModuleDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(AddSpecOrModuleDialogView.class).load();


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

