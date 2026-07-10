package org.pampasim;

import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.ObservableList;
import javafx.scene.control.Tab;
import org.pampasim.core.Simulation;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.resources.ModuleSimulation;
import org.pampasim.resources.Process;
import org.pampasim.resources.dialog.CreateProcessDialogService;
import org.pampasim.resources.view.PCBView;
import org.pampasim.resources.viewmodel.ModuleInfoViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;
import org.pampasim.resources.viewmodel.StatisticsViewModel;

import java.util.Map;

public interface PampaSimModule {
    Tab getModuleTab();
    PCBView.ModulePCBView getPCBViewExtension();
    ModuleInfoViewModel newModuleInfoVM(Object config) throws ModuleSimulation.ConfigError;
    StatisticsViewModel getStatisticsViewModel();
    CreateProcessDialogService.ModuleTuple getCreateProcessDialogServiceModuleTuple();
    ViewModel getViewModel();
    ModuleSimulation getSimulation();

    void setProcessToProcessVMMap(Map<Process, ProcessViewModel> pvmMap);
    void setProcessVMObservableList(ObservableList<ProcessViewModel> obProcList);

    /**
     * Used during spec load to associate module
     * view models with their process view models
     * @param pvm target process view model
     * @param mvm module info view model to be annexed
     */
    void annexModuleInfoVM(ProcessViewModel pvm, Object mvm);
    Class<? extends SimEntity> rootSimEntityClass();

    /**
     * Clear state and restart module, may be required after a `this.applyConfig` call
     */
    void invalidate();
}
