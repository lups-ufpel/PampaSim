package org.pampasim.viewModel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class SelectSchedulerDialogViewModel implements ViewModel {

    private final BooleanProperty memoryModulePresent = new SimpleBooleanProperty(false);
    private final ObservableList<String> schedulerName = FXCollections.observableArrayList();

    // ─────────────── Values the user can change ─────────────

        // ─────────────── Process Scheduling ─────────────
    private final StringProperty selectedScheduler = new SimpleStringProperty();
    private final BooleanProperty preemptive       = new SimpleBooleanProperty(false);
    private final IntegerProperty quantum          = new SimpleIntegerProperty(2);

        // ─────────────── Memory Parameters ─────────────
    private final IntegerProperty pageSize                       = new SimpleIntegerProperty(512);    // KB
    private final IntegerProperty maxPagesPerProcess             = new SimpleIntegerProperty(1024);
    private final IntegerProperty framesInRAM                    = new SimpleIntegerProperty(256);
    private final IntegerProperty framesInSwap                   = new SimpleIntegerProperty(1024);
    private final IntegerProperty swapOperationLength            = new SimpleIntegerProperty(3);
    private final IntegerProperty workingSetWindow               = new SimpleIntegerProperty(10);

    private final StringProperty pageSubstitutionAlgorithm       = new SimpleStringProperty("Random");
    private final BooleanProperty globalPageSubstitution         = new SimpleBooleanProperty(true);
    private final BooleanProperty anticipatedPageLoading         = new SimpleBooleanProperty(true);
    private final IntegerProperty prePagingRange                 = new SimpleIntegerProperty(5);

    private final BooleanProperty variablePageAllocation         = new SimpleBooleanProperty(false);
    private final DoubleProperty variablePageAllocationTopThreshold    = new SimpleDoubleProperty(0.75);
    private final DoubleProperty variablePageAllocationBottomThreshold = new SimpleDoubleProperty(0.25);

    private final BooleanProperty tlbEnabled                     = new SimpleBooleanProperty(false);
    private final IntegerProperty tlbEntries                     = new SimpleIntegerProperty(16);


    /* ========== public API ========== */

    public void setSchedulerNames(List<String> names) {
        schedulerName.setAll(names);
        setDefaultSelectedScheduler(names);
    }

    public void setDefaultSelectedScheduler(List<String> names) {
        if(!names.isEmpty()) {
            selectedScheduler.set(names.getFirst());
        }
    }

    public ObservableList<String> schedulerNameProperty () {
        return schedulerName;
    }

    public StringProperty selectedSchedulerProperty() {
        return selectedScheduler;
    }
    public BooleanProperty preemptiveProperty() {
        return preemptive;
    }
    public IntegerProperty quantumProperty() {
        return quantum;
    }

    public IntegerProperty pageSizeProperty()                             { return pageSize; }
    public IntegerProperty maxPagesPerProcessProperty()                   { return maxPagesPerProcess; }
    public IntegerProperty framesInRAMProperty()                          { return framesInRAM; }
    public IntegerProperty framesInSwapProperty()                         { return framesInSwap; }
    public IntegerProperty swapOperationLengthProperty()                  { return swapOperationLength; }
    public IntegerProperty workingSetWindowProperty()                     { return workingSetWindow; }
    public StringProperty  pageSubstitutionAlgorithmProperty()            { return pageSubstitutionAlgorithm; }
    public BooleanProperty globalPageSubstitutionProperty()               { return globalPageSubstitution; }
    public BooleanProperty anticipatedPageLoadingProperty()               { return anticipatedPageLoading; }
    public IntegerProperty prePagingRangeProperty()                       { return prePagingRange; }
    public BooleanProperty variablePageAllocationProperty()               { return variablePageAllocation; }
    public DoubleProperty  variablePageAllocationTopThresholdProperty()   { return variablePageAllocationTopThreshold; }
    public DoubleProperty  variablePageAllocationBottomThresholdProperty(){ return variablePageAllocationBottomThreshold; }
    public BooleanProperty tlbEnabledProperty()                           { return tlbEnabled; }
    public IntegerProperty tlbEntriesProperty()                           { return tlbEntries; }

    /* Helper getters (used by the service after showAndWait()) */
    public String  getSelectedScheduler() { return selectedScheduler.get(); }
    public boolean isPreemptive()         { return preemptive.get(); }
    public int     getQuantum()           { return quantum.get(); }

    public int     getPageSize()                             { return pageSize.get(); }
    public int     getMaxPagesPerProcess()                   { return maxPagesPerProcess.get(); }
    public int     getFramesInRAM()                          { return framesInRAM.get(); }
    public int     getFramesInSwap()                         { return framesInSwap.get(); }
    public int     getSwapOperationLength()                  { return swapOperationLength.get(); }
    public int     getWorkingSetWindow()                     { return workingSetWindow.get(); }
    public String  getPageSubstitutionAlgorithm()            { return pageSubstitutionAlgorithm.get(); }
    public boolean isGlobalPageSubstitution()                { return globalPageSubstitution.get(); }
    public boolean isAnticipatedPageLoading()                { return anticipatedPageLoading.get(); }
    public int     getPrePagingRange()                       { return prePagingRange.get(); }
    public boolean isVariablePageAllocation()                { return variablePageAllocation.get(); }
    public double  getVariablePageAllocationTopThreshold()   { return variablePageAllocationTopThreshold.get(); }
    public double  getVariablePageAllocationBottomThreshold(){ return variablePageAllocationBottomThreshold.get(); }
    public boolean isTlbEnabled()                            { return tlbEnabled.get(); }
    public int     getTlbEntries()                           { return tlbEntries.get(); }


    public BooleanProperty memoryModulePresentProperty() {
        return memoryModulePresent;
    }
    public boolean isMemoryModulePresent() {
        return memoryModulePresent.get();
    }
    public void setMemoryModulePresent(boolean present) {
        memoryModulePresent.set(present);
    }
}
