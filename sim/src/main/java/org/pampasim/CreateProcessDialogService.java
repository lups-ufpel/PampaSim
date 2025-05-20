package org.pampasim;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.pampasim.view.CreateProcessDialogView;
import org.pampasim.view.SelectSchedulerDialogView;
import org.pampasim.viewModel.CreateProcessDialogViewModel;

import javax.annotation.Nullable;
import java.util.Optional;

public class CreateProcessDialogService implements DialogService<CreateProcessRecord> {

    @Override
    public Optional<CreateProcessRecord> showDialog(Object ... args) {

        ViewTuple<CreateProcessDialogView, CreateProcessDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(CreateProcessDialogView.class).load();
        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);
        dialog.showAndWait();
        return Optional.empty();
    }
}
