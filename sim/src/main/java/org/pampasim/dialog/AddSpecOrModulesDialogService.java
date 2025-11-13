package org.pampasim.dialog;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.pampasim.core.dialog.DialogService;
import org.pampasim.view.AddSpecOrModulesDialogView;
import org.pampasim.viewModel.AddSpecOrModulesDialogViewModel;


import java.util.List;
import java.util.Optional;

public class AddSpecOrModulesDialogService implements DialogService<AddSpecOrModulesRecord> {

    List<String> availableSchedulers;

    @Override
    public Optional<AddSpecOrModulesRecord> showDialog(Object ... args) {
        ViewTuple<AddSpecOrModulesDialogView, AddSpecOrModulesDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(AddSpecOrModulesDialogView.class).load();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("PampaSim Setup");
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);

        if(args.length > 0 && args[0] instanceof List<?> rawList) {
            List<String> moduleNames = (List<String>) rawList;
            viewTuple.getViewModel().setModuleNames(moduleNames);

        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.APPLY) {
            Optional<String> specPath = viewTuple.getViewModel().getSpecPath();
            AddSpecOrModulesRecord userSelection = new AddSpecOrModulesRecord(
            viewTuple.getViewModel().getSelectedModulesOpt(), specPath);
            return Optional.of(userSelection);
        }
        return Optional.empty();
    }
}

