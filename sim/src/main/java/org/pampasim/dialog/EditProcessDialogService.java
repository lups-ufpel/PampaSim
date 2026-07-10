package org.pampasim.dialog;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.paint.Color;
import org.pampasim.core.dialog.DialogService;
import org.pampasim.resources.dialog.CreateProcessRecord;
import org.pampasim.view.EditProcessDialogView;
import org.pampasim.viewModel.EditProcessDialogViewModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EditProcessDialogService implements DialogService<EditProcessRecord> {
    @Override
    public Optional<EditProcessRecord> showDialog(Object ... args) {
        ViewTuple<EditProcessDialogView, EditProcessDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(EditProcessDialogView.class).load();

        if (args.length != 5
                || !(args[0] instanceof Integer)
                || !(args[1] instanceof Integer)
                || !(args[2] instanceof Integer)
                || !(args[3] instanceof ObjectProperty)
                || !(args[4] instanceof Map)
        ) {
            throw new IllegalArgumentException("Invalid arguments for showDialog");
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);
        int start = (int) args[0];
        int duration = (int) args[1];
        int priority = (int) args[2];
        ObjectProperty<Color> color = (ObjectProperty<Color>) args[3];
        Map<Class<?>, Object> moduleInfo = (Map<Class<?>, Object>) args[4];
        viewTuple.getCodeBehind().setProcessData(start, duration, priority, color); //TODO: NOT THE BEST OPTION
        // this is blocking AFAIK
        return dialog.showAndWait()
            .filter(r -> r.getButtonData() != ButtonBar.ButtonData.CANCEL_CLOSE)
            .map(r -> {
                CreateProcessRecord userInput = new CreateProcessRecord(
                        viewTuple.getViewModel().getProcessStart(),
                        viewTuple.getViewModel().getProcessDuration(),
                        viewTuple.getViewModel().getProcessPriority(),
                        viewTuple.getViewModel().convertColor(),
                        moduleInfo // FIXME/TODO: Hack job, need to make it so we can edit module info too
                        );
                boolean delete = r.getButtonData() == ButtonBar.ButtonData.LEFT;
                return new EditProcessRecord(userInput, delete);
            });
    }
}
