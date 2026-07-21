package org.pampasim.memory;

import de.saxsys.mvvmfx.FluentViewLoader;
import de.saxsys.mvvmfx.ViewModel;
import de.saxsys.mvvmfx.ViewTuple;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kordamp.ikonli.bootstrapicons.BootstrapIcons;
import org.kordamp.ikonli.javafx.FontIcon;
import org.pampasim.core.Simulation;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.memory.view.MemoryInfoView;
import org.pampasim.memory.view.MemoryTabView;
import org.pampasim.memory.viewmodel.MemoryStatisticsViewModel;
import org.pampasim.memory.viewmodel.MemoryTabViewModel;
import org.pampasim.resources.ModuleSimulation;
import org.pampasim.resources.PampaSimModuleBase;
import org.pampasim.resources.dialog.CreateProcessDialogService;
import org.pampasim.resources.memory.MemoryProcessCreationData;
import org.pampasim.resources.view.PCBView;
import org.pampasim.resources.viewmodel.ModuleInfoViewModel;
import org.pampasim.resources.viewmodel.ProcessViewModel;
import org.pampasim.resources.viewmodel.StatisticsViewModel;

import java.math.BigInteger;

public class MemoryModule extends PampaSimModuleBase {
    private static final Logger LOGGER = LogManager.getLogger(MemoryModule.class);
    private MemoryManagement memSim = null;
    private MemoryTabViewModel memoryModuleVM = null;
    private MemoryStatisticsViewModel memoryStatisticsViewModel = null;
    private Tab memoryTab = null;

    public MemoryModule() {}

    @Override
    public void managedInitialize() {
        // Create the memory tab with a pop-out button in the header
        memoryTab = new Tab();
        HBox header = new HBox(5);
        header.setAlignment(Pos.CENTER_LEFT); // center vertically
        Label title = new Label("Memória");
        FontIcon icon = new FontIcon(BootstrapIcons.BOX_ARROW_UP_RIGHT);
        icon.setIconSize(14);
        Button popOutBtn = getPopoutButton(icon, memoryTab);
        header.getChildren().addAll(title, popOutBtn);
        memoryTab.setGraphic(header);
        memoryTab.setClosable(false);
    }

    @Override
    public boolean managedBind(Simulation sim) {
        reinitializeMemoryManagement(sim);
        return true;
    }

    @Override
    public Tab getModuleTab() {
        return memoryTab;
    }

    @Override
    public ModuleInfoViewModel newModuleInfoVM(Object config) throws ModuleSimulation.ConfigError {
        if (! (config instanceof MemoryProcessCreationData processMemConfig)) {
            throw new ModuleSimulation.ConfigError("module info type mismatch");
        }
        return new org.pampasim.memory.viewmodel.MemoryInfoViewModel(
                processMemConfig,
                MemoryConfig.getWorkingSetWindow(),
                MemoryConfig.getMaxPagesPerProcess());
    }

    @Override
    public StatisticsViewModel getStatisticsViewModel() {
        return memoryStatisticsViewModel;
    }

    @Override
    public CreateProcessDialogService.ModuleTuple getCreateProcessDialogServiceModuleTuple() {
        var memoryViewTuple = FluentViewLoader.fxmlView(MemoryInfoView.class).load();
        var memVM = memoryViewTuple.getViewModel();
        var memView = memoryViewTuple.getCodeBehind();
        return new CreateProcessDialogService.ModuleTuple(
                //init
                memView::moduleInitializer,
                // generate
                viewModel -> {
                    var objFact = new org.pampasim.resources.memory.ObjectFactory();
                    var memCData = objFact.createMemoryProcessCreationData();
                    memCData.setPageCount(memVM.getProcessSize());

                    memCData.setFileBackedPages   (objFact.createPageIdList());
                    memCData.setModifyPages       (objFact.createPageIdList());
                    memCData.setAddressAccessList (objFact.createAccessList());

                    { // Populate MemoryProcessCreationData fields
                        memCData.setPageCount(memVM.getProcessSize());
                        var fileBackedPages = memCData.getFileBackedPages().getPageId();
                        var modifyPages = memCData.getModifyPages().getPageId();
                        for (int i = 0; i < memVM.getProcessSize(); i++) {
                            if (i < memVM.getFileBackedPages().size() && memVM.getFileBackedPages().get(i)) {
                                fileBackedPages.add((long)i);
                            }
                            if (i < memVM.getModifiesPage().size() && memVM.getModifiesPage().get(i)) {
                                modifyPages.add((long)i);
                            }
                        }
                        var accessList = memCData.getAddressAccessList();
                        for (var address : memVM.getMemoryAccesses()) {
                            accessList.getAddress().add(BigInteger.valueOf(address));
                        }
                        memCData.setLoopAccessList(memVM.getLoopAccessList());
                    };
                    return memCData;
                });
    }

    @Override
    public ViewModel getViewModel() {
        assert(getModuleState() != ModuleState.UNINITIALIZED);
        return memoryModuleVM;
    }

    @Override
    public ModuleSimulation getSimulation() {
        assert(getModuleState() != ModuleState.UNINITIALIZED);
        return memSim;
    }

    @Override
    public void annexModuleInfoVM(ProcessViewModel pvm, Object mvm) {
        var processMemConfig = (MemoryProcessCreationData) mvm;
        var memInfoVM = new org.pampasim.memory.viewmodel.MemoryInfoViewModel(
                processMemConfig,
                MemoryConfig.getWorkingSetWindow(),
                MemoryConfig.getMaxPagesPerProcess());
        pvm.addModuleInfoViewModel(memInfoVM);
    }

    @Override
    public Class<? extends SimEntity> rootSimEntityClass() {
        return memSim.getClass();
    }

    @Override
    public void invalidate() {
        reinitializeMemoryManagement(this.getSimulation().getParent().getSimulation());
    }

    /**
     * Creates the actual simulation entity that'll compose part of the whole simulated environment
     * and updates the relevant references for this object
     * @param sim parent simulation
     */
    private void reinitializeMemoryManagement(Simulation sim) {
        LOGGER.trace("reinitializeMemoryManagement called");
        memSim = new MemoryManagement();
        memSim.bind(sim);


        memoryStatisticsViewModel = new MemoryStatisticsViewModel();
        memoryModuleVM = new MemoryTabViewModel(
                memSim,
                pvmMap,
                processVMObservableList,
                memoryStatisticsViewModel
        );

        MemoryManagement simMemoryModule = sim.getEntity(MemoryManagement.class);
        assert(simMemoryModule == memSim);

        ViewTuple<MemoryTabView, MemoryTabViewModel> viewTuple = FluentViewLoader
                .fxmlView(MemoryTabView.class)
                .viewModel(memoryModuleVM)
                .load();
        Parent content = viewTuple.getView();

        memoryTab.setContent(content);


        memoryModuleVM.refreshFrameList();
    }

    private Button getPopoutButton(FontIcon icon, Tab memoryTab) {
        Button popOutBtn = new Button();
        popOutBtn.setGraphic(icon);
        popOutBtn.setFocusTraversable(false);

        popOutBtn.setOnAction(e -> {
            if (memoryTab.getContent() == null) return;

            Parent poppedContent = (Parent) memoryTab.getContent();
            memoryTab.setContent(null);

            Stage popOutStage = new Stage();
            popOutStage.setTitle("Memória");

            BorderPane layout = new BorderPane(poppedContent);
            Scene popOutScene = new Scene(layout, 800, 600);
            popOutStage.setScene(popOutScene);
            // FIXME: we don't have access to the main Window from here
            //popOutStage.initOwner(tabPane.getScene().getWindow());

            popOutStage.setOnCloseRequest(event -> {
                memoryTab.setContent(poppedContent);
            });

            popOutStage.show();
        });
        return popOutBtn;
    }
}
