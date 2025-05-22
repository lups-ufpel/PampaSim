package org.pampasim;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;
import org.pampasim.view.EditProcessDialogView;
import org.pampasim.viewModel.EditProcessDialogViewModel;

import java.util.Optional;

public class EditProcessDialogService implements DialogService<EditProcessRecord> {

    @Override
    public Optional<EditProcessRecord> showDialog(Object ... args) {


        ViewTuple<EditProcessDialogView, EditProcessDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(EditProcessDialogView.class).load();

        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);
        if (args.length != 4
                || !(args[0] instanceof Integer)
                || !(args[1] instanceof Integer)
                || !(args[2] instanceof Integer)
                || !(args[3] instanceof ObjectProperty)) {
            throw new IllegalArgumentException("Invalid arguments for showDialog");
        }
        int start = (int) args[0];
        int duration = (int) args[1];
        int priority = (int) args[2];
        ObjectProperty<Color> color = (ObjectProperty<Color>) args[3];
        viewTuple.getCodeBehind().setProcessData(start, duration, priority, color); //TODO: NOT THE BEST OPTION
        Optional<ButtonType> result = dialog.showAndWait();
        System.out.println(result.toString());
        if(result.isPresent() && result.get().getButtonData() != ButtonBar.ButtonData.CANCEL_CLOSE) {
            CreateProcessRecord userInput = new CreateProcessRecord(
                    viewTuple.getViewModel().getProcessStart(),
                    viewTuple.getViewModel().getProcessDuration(),
                    viewTuple.getViewModel().getProcessPriority(),
                    viewTuple.getViewModel().convertColor());
            boolean removable = result.get().getButtonData() == ButtonBar.ButtonData.LEFT;
            EditProcessRecord editProcessRecord = new EditProcessRecord(
                    userInput,
                    removable);
            return Optional.of(editProcessRecord);
        }
        return Optional.empty();
    }
}
