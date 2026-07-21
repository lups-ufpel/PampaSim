package org.pampasim.resources;

import javafx.collections.ObservableList;
import lombok.Getter;
import org.pampasim.PampaSimModule;
import org.pampasim.core.Simulation;
import org.pampasim.resources.dialog.CreateProcessDialogService;
import org.pampasim.resources.view.PCBView;
import org.pampasim.resources.viewmodel.ProcessViewModel;
import org.pampasim.resources.viewmodel.StatisticsViewModel;

import java.util.Map;

public abstract class PampaSimModuleBase implements PampaSimModule {
    @Getter
    protected ModuleState moduleState = ModuleState.UNINITIALIZED;
    protected Map<Process, ProcessViewModel> pvmMap = null;
    protected ObservableList<ProcessViewModel> processVMObservableList = null;

    @Override
    public final void initialize() {
        assert(getModuleState() == ModuleState.UNINITIALIZED);
        managedInitialize();
        moduleState = ModuleState.UNBOUND;
    }

    /** abstracts away keeping the module state updated.
     */
    public abstract void managedInitialize();
    @Override
    public final void bind(Simulation sim) {
        assert(getModuleState() == ModuleState.UNBOUND
                || getModuleState() == ModuleState.STALE_BINDING);
        if (! managedBind(sim)) { moduleState = ModuleState.BOUND; }
    }

    /** abstracts away keeping the module state updated.
     * @return whether binding was successful
     */
    public abstract boolean managedBind(Simulation sim);

    @Override
    public void invalidateBinding() {
        assert(getModuleState() == ModuleState.BOUND);
        this.moduleState = ModuleState.STALE_BINDING;
    }

    @Override
    public void setProcessToProcessVMMap(Map<Process, ProcessViewModel> pvmMap) {
        this.pvmMap = pvmMap;
    }

    @Override
    public void setProcessVMObservableList(ObservableList<ProcessViewModel> obProcList) {
        this.processVMObservableList = obProcList;
    }

    // default implementations for the optionals
    @Override
    public PCBView.ModulePCBView getPCBViewExtension() { return null; }

    @Override
    public StatisticsViewModel getStatisticsViewModel() { return null; }

    @Override
    public CreateProcessDialogService.ModuleTuple getCreateProcessDialogServiceModuleTuple() { return null; }
}
