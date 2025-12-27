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
import org.pampasim.memory.dialog.MemoryConfigSelectionRecord;
import org.pampasim.viewModel.SimulationSetupDialogViewModel;
import org.pampasim.filesystem.FileSystemConfigSelectionRecord;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class SettingsDialogService implements DialogService<SchedulerSelectionRecord> {

    List<String> availableSchedulers;
    @Getter
    @Setter
    private boolean memoryModulePresent = false;
    @Getter
    @Setter
    private boolean fileSystemModulePresent = false;

    @Override
    public Optional<SchedulerSelectionRecord> showDialog(Object... args) {

        ViewTuple<org.pampasim.view.SimulationSetupDialogView, SimulationSetupDialogViewModel> viewTuple =
                FluentViewLoader.fxmlView(org.pampasim.view.SimulationSetupDialogView.class).load();

        viewTuple.getViewModel().setMemoryModulePresent(memoryModulePresent);
        viewTuple.getViewModel().setFileSystemModulePresent(fileSystemModulePresent);

        Dialog<ButtonType> dialog = new Dialog<>();
        DialogPane dialogPane = (DialogPane) viewTuple.getView();
        dialog.setDialogPane(dialogPane);

        if (args.length > 0 && args[0] instanceof List<?> rawList) {
            List<String> schedulerNames = (List<String>) rawList;
            viewTuple.getViewModel().setSchedulerNames(schedulerNames);
        }

        if (memoryModulePresent && args.length > 1 && args[1] instanceof List<?> rawList) {
            List<String> pageReplacementAlgorithmNames = (List<String>) rawList.stream()
                    .filter(str -> !str.equals("AbstractPageReplacementAlgorithm"))
                    .collect(Collectors.toList());
            viewTuple.getViewModel().setPageSubstitutionAlgorithmNames(pageReplacementAlgorithmNames);
        }

        if (fileSystemModulePresent && args.length > 2 && args[2] instanceof List<?> rawList){
            List<String> allocationSchemeNames = (List<String>) rawList;
            viewTuple.getViewModel().setAllocationSchemeNames(allocationSchemeNames);
        }

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

            FileSystemConfigSelectionRecord fileSystemConfig = new FileSystemConfigSelectionRecord(
                    viewTuple.getViewModel().getBlockSizeBytes(),
                    viewTuple.getViewModel().getNumberOfBlocks(),
                    viewTuple.getViewModel().getAllocationScheme()
            );

            SchedulerSelectionRecord userSelection = new SchedulerSelectionRecord(
                    viewTuple.getViewModel().getSelectedScheduler(),
                    viewTuple.getViewModel().isPreemptive(),
                    viewTuple.getViewModel().getQuantum(),
                    memoryConfig,
                    fileSystemConfig
            );

            return Optional.of(userSelection);
        }

        return Optional.empty();
    }
}

