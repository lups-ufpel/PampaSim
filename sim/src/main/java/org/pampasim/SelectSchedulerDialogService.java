package org.pampasim;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import org.pampasim.view.SelectSchedulerDialogView;
import org.pampasim.viewModel.SelectSchedulerDialogViewModel;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SelectSchedulerDialogService implements DialogService<SchedulerSelection> {

    List<String> availableSchedulers;

    @Override
    public Optional<SchedulerSelection> showDialog(Object ... args) {


        ViewTuple<SelectSchedulerDialogView, SelectSchedulerDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(SelectSchedulerDialogView.class).load();

        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);

        if(args.length > 0 && args[0] instanceof List<?> rawList) {
            List<String> schedulerNames = (List<String>) rawList;
            viewTuple.getViewModel().setSchedulerNames(schedulerNames);

        }

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.APPLY) {
            // handle data from select scheduler.
            SchedulerSelection userSelection = new SchedulerSelection(
                    viewTuple.getViewModel().getSelectedScheduler(),
                    viewTuple.getViewModel().isPreemptive(),
                    viewTuple.getViewModel().getQuantum());
            return Optional.of(userSelection);
        }
        return Optional.empty();
    }
}
