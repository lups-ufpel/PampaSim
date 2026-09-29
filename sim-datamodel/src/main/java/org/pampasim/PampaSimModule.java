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

import java.util.List;
import java.util.Map;

/**
 * PampaSim module behavioral abstraction
 * Modules MUST keep their ModuleState up to date by observing their simulation property's changes
 * Javadocs follow RFC 2119 (keywords for requirement levels)
 */
public interface PampaSimModule {
    /**
     * Debug aid and invariant ensurer. Use it to gate operations that have stateful prerequisites, ones that need a
     * current simulation reference, for example.
     */
    enum ModuleState { INVALID, UNINITIALIZED, UNBOUND, BOUND, STALE_BINDING }
    void initialize();
    void bind(Simulation sim);
    void invalidateBinding();
    ModuleState getModuleState();

    /**
     * All PampaSim modules MUST have a module tab, even if simple.
     * @return Tab JavaFX node
     */
    Tab getModuleTab();

    /**
     * PampaSim modules MAY display their tabs inside the process inspector.
     * This method exposes the implementation if it exists.
     * @return null or the object carrying the implementation
     */
    PCBView.ModulePCBView getPCBViewExtension();


    /**
     * Not really sure what this one's meant to do...
     * FIXME?
     * @param config config
     * @return return
     * @throws ModuleSimulation.ConfigError when config is invalid
     */
    ModuleInfoViewModel newModuleInfoVM(Object config) throws ModuleSimulation.ConfigError;

    /**
     * PampaSim modules MAY register statistics trackers.
     * @return null or the StatisticsViewModel for this module
     */
    StatisticsViewModel getStatisticsViewModel();

    /**
     * PampaSim modules MAY annex module information to processes.
     * When that is the case, they MUST provide a way for the user to enter whatever
     * information is user-decided alongside the usual process forms.
     * If no module information needs user input, this MAY be ommited.
     * @return null or the initializer-generator function pair for the process creation dialog service
     */
    CreateProcessDialogService.ModuleTuple getCreateProcessDialogServiceModuleTuple();

    ViewModel getViewModel();
    ModuleSimulation getSimulation();

    /**
     * Necessary evil to keep track of Process to ProcessViewModel associations non-intrusively wrt. Process data
     * TODO: think about using Process factories and a specialization of Process to use Liskov Subst.?
     * @param pvmMap the Process to ProcessViewModel map
     */
    void setProcessToProcessVMMap(Map<Process, ProcessViewModel> pvmMap);

    /**
     * Necessary evil to keep track of all ProcessViewModels
     * TODO: think about using Process factories and a specialization of Process to use Liskov Subst.?
     * @param obProcList the Observable Process List
     */
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
     * Used to reverse-lookup the module when processing event payloads
     * MUST be specified when the annexed module data mechanism is used
     * @return the list of payload classes
     */
    List<Class<?>> getEventPayloadClasses();
}
