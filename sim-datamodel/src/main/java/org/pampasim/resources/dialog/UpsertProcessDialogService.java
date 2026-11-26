package org.pampasim.resources.dialog;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
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
import org.pampasim.resources.Process;
import org.pampasim.resources.view.UpsertProcessDialogView;
import org.pampasim.resources.viewmodel.ModuleInfoViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;
import org.pampasim.resources.viewmodel.UpsertProcessDialogViewModel;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Used to create OR modify an existing process!
 */
@Setter
@Getter
public class UpsertProcessDialogService implements DialogService<UpsertProcessRecord> {
    private static final Logger LOGGER = LogManager.getLogger(UpsertProcessDialogService.class);
    private int memoryPageSize = 0;

    public UpsertProcessDialogService() {
        moduleInfo = new HashMap<>();
    }

    public record ModuleTuple(
            Function<ViewTuple<UpsertProcessDialogView, UpsertProcessDialogViewModel>, Parent> initializer,
            Function<ViewModel, Object> generator,
            Optional<Object> previousValue)
    {};

    public Map<Class<?>, ModuleTuple> moduleInfo;

    @Override
    public Optional<UpsertProcessRecord> showDialog(Object ... args) {
        var vm = new UpsertProcessDialogViewModel(moduleInfo);

        Optional<ProcessViewModel> existingPVMOpt = Optional.ofNullable((args.length > 0) ? (ProcessViewModel)args[0] : null);

        if (existingPVMOpt.isPresent()) {
            var pvm = existingPVMOpt.get();
            LOGGER.trace("populating from {}", pvm);
            vm.processStartProperty().set(pvm.getCreationData().arrivalTick());
            vm.processDurationProperty().set(pvm.getCreationData().durationTicks());
            vm.processPriorityProperty().set(pvm.getCreationData().startPriority());
            vm.colorHexProperty().setValue(pvm.getColorProperty().get());
            for (ModuleInfoViewModel moduleInfoViewModel : pvm.getModuleInfoViewModels()) {
                var moduleClass = moduleInfoViewModel.getPampaSimModuleClass();
                var previousValue = pvm.getCreationData().moduleCreationData().get(moduleClass);
                vm.getModuleInfo().compute(moduleClass, (k, v) -> {
                    return new ModuleTuple(v.initializer, v.generator, Optional.ofNullable(previousValue));
                });
            }
        }

        ViewTuple<UpsertProcessDialogView, UpsertProcessDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(UpsertProcessDialogView.class)
                        .viewModel(vm)
                        .load();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setResizable(true);
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);
        Optional<ButtonType> result = dialog.showAndWait();

        if(result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.APPLY) {
            Map<Class<?>, Object> moduleData = moduleInfo.entrySet().stream()
                    .map((entry) -> {
                        var moduleTuple = entry.getValue();
                        var generator = moduleTuple.generator;
                        var data = generator.apply(vm);
                        return List.of(entry.getKey(), data);
                    }).collect(Collectors.toMap(e -> (Class<?>)e.getFirst(), List::getLast));

            UpsertProcessRecord userInput = new UpsertProcessRecord(
                    viewTuple.getViewModel().getProcessStart(),
                    viewTuple.getViewModel().getProcessDuration(),
                    viewTuple.getViewModel().getProcessPriority(),
                    viewTuple.getViewModel().convertColor(),
                    moduleData,
                    false);
            return Optional.of(userInput);
        }
        return Optional.empty();
    }
}