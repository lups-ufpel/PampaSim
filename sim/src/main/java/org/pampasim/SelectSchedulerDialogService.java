package org.pampasim;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.pampasim.view.SelectSchedulerDialogView;
import org.pampasim.viewModel.SelectSchedulerDialogViewModel;

import java.util.Optional;

public class SelectSchedulerDialogService implements DialogService {

    @Override
    public <T> Optional<T> showDialog() {

        ViewTuple<SelectSchedulerDialogView, SelectSchedulerDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(SelectSchedulerDialogView.class).load();

        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.APPLY) {
            // handle data from select scheduler.
            return Optional.empty();
        }
        return Optional.empty();
    }
}
