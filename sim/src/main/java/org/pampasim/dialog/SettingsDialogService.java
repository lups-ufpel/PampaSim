package org.pampasim.dialog;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.core.dialog.DialogService;
import org.pampasim.entity.schedulers.Scheduler;
import org.pampasim.memory.dialog.MemoryConfigSelectionRecord;
import org.pampasim.memory.entity.algorithms.PageReplacementAlgorithm;
import org.pampasim.viewModel.SimulationSetupDialogViewModel;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SettingsDialogService implements DialogService<SchedulerSelectionRecord> {

    List<String> availableSchedulers;
    @Getter
    @Setter
    private boolean memoryModulePresent = false;
    private List<? extends Class<? extends Scheduler>> schedulerClasses;
    private List<? extends Class<? extends PageReplacementAlgorithm>> pageReplacementAlgorithms;

    public SettingsDialogService(List<? extends Class<? extends Scheduler>> schedulerClasses, List<? extends Class<? extends PageReplacementAlgorithm>> pageReplacementAlgorithms) {
        this.schedulerClasses = schedulerClasses;
        this.pageReplacementAlgorithms = pageReplacementAlgorithms;
    }
    @Override
    public Optional<SchedulerSelectionRecord> showDialog(Object... args) {
        var dialogViewModel = new SimulationSetupDialogViewModel();
        dialogViewModel.setMemoryModulePresent(memoryModulePresent);

        // TODO: find a way to use short/long descriptions to build the UI
        var schedulerNames = schedulerClasses
                .stream()
                .map(clazz -> { return clazz.getSimpleName(); })
                .toList();
        dialogViewModel.setSchedulerNames(schedulerNames);

        var pageReplacementAlgorithmNames = pageReplacementAlgorithms
                .stream()
                .map(clazz -> clazz.getSimpleName())
                .toList();
        dialogViewModel.setPageSubstitutionAlgorithmNames(pageReplacementAlgorithmNames);

        if (args.length > 0 && args[0] instanceof SchedulerSelectionRecord initialSettings) {
            dialogViewModel.initializeFrom(initialSettings);
        }

        ViewTuple<org.pampasim.view.SimulationSetupDialogView, SimulationSetupDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(org.pampasim.view.SimulationSetupDialogView.class)
                        .viewModel(dialogViewModel)
                        .load();

        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.APPLY) {
            // create memory config from viewmodel values
            MemoryConfigSelectionRecord memoryConfig = new MemoryConfigSelectionRecord(
                    viewTuple.getViewModel().getPageSize(),
                    viewTuple.getViewModel().getMaxPagesPerProcess(),
                    viewTuple.getViewModel().getFramesInRAM(),
                    viewTuple.getViewModel().getFramesInSwap(),
                    viewTuple.getViewModel().getSwapOperationLength(),
                    viewTuple.getViewModel().getWorkingSetWindow(),
                    viewTuple.getViewModel().getPageSubstitutionAlgorithm(),
                    viewTuple.getViewModel().isGlobalPageSubstitution(),
                    viewTuple.getViewModel().isAnticipatedPageLoading(),
                    viewTuple.getViewModel().getPrePagingRange(),
                    viewTuple.getViewModel().isVariablePageAllocation(),
                    viewTuple.getViewModel().getVariablePageAllocationTopThreshold(),
                    viewTuple.getViewModel().getVariablePageAllocationBottomThreshold(),
                    viewTuple.getViewModel().isTlbEnabled(),
                    viewTuple.getViewModel().getTlbEntries()
            );

            SchedulerSelectionRecord userSelection = new SchedulerSelectionRecord(
                    viewTuple.getViewModel().getSelectedScheduler(),
                    viewTuple.getViewModel().isPreemptive(),
                    viewTuple.getViewModel().getQuantum(),
                    memoryConfig
            );

            return Optional.of(userSelection);
        }

        return Optional.empty();
    }
}
