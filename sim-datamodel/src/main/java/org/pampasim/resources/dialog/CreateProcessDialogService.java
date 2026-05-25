package org.pampasim.resources.dialog;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import de.saxsys.mvvmfx.internal.viewloader.View;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.dialog.DialogService;
import org.pampasim.resources.view.CreateProcessDialogView;
import org.pampasim.resources.viewmodel.CreateProcessDialogViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Setter
@Getter
public class CreateProcessDialogService implements DialogService<CreateProcessRecord> {
    private static final Logger LOGGER = LogManager.getLogger(CreateProcessDialogService.class);
    private int memoryPageSize = 0;

    public record ModuleTuple(
            Function<ViewTuple<CreateProcessDialogView,CreateProcessDialogViewModel>, Parent> initializer,
            Function<ViewModel, Object> generator) {};

    public Map<Class<?>, ModuleTuple> moduleInfo = Map.of();

    @Override
    public Optional<CreateProcessRecord> showDialog(Object ... args) {
        ViewTuple<CreateProcessDialogView, CreateProcessDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(CreateProcessDialogView.class).load();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setResizable(true);
        DialogPane dialogPane = (DialogPane) viewTuple.getView();

        moduleInfo.forEach((k,v) -> {
            LOGGER.trace("running initializer for module: {}", k);
            var parent = v.initializer.apply(viewTuple);
            viewTuple.getCodeBehind().moduleSection.getChildren().add(parent);
        });

        dialog.setDialogPane(dialogPane);
        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.APPLY) {
            CreateProcessDialogViewModel vm = viewTuple.getViewModel();

            Map<Class<?>, Object> moduleData = moduleInfo.entrySet().stream()
                    .map((entry) -> {
                        var moduleTuple = entry.getValue();
                        var generator = moduleTuple.generator;
                        var data = generator.apply(vm);
                        return List.of(entry.getKey(), data);
                    }).collect(Collectors.toMap(e -> (Class<?>)e.getFirst(), List::getLast));

            CreateProcessRecord userInput = new CreateProcessRecord(
                    viewTuple.getViewModel().getProcessStart(),
                    viewTuple.getViewModel().getProcessDuration(),
                    viewTuple.getViewModel().getProcessPriority(),
                    viewTuple.getViewModel().convertColor(),
                    moduleData);
            return Optional.of(userInput);
        }
        return Optional.empty();
    }
}