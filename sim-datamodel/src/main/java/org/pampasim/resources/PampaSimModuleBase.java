package org.pampasim.resources;

import javafx.collections.ObservableList;
import org.pampasim.PampaSimModule;
import org.pampasim.resources.viewmodel.ProcessViewModel;

import java.util.Map;

public abstract class PampaSimModuleBase implements PampaSimModule {
    protected Map<Process, ProcessViewModel> pvmMap = null;
    protected ObservableList<ProcessViewModel> processVMObservableList = null;

    @Override
    public void setProcessToProcessVMMap(Map<Process, ProcessViewModel> pvmMap) {
        this.pvmMap = pvmMap;
    }

    @Override
    public void setProcessVMObservableList(ObservableList<ProcessViewModel> obProcList) {
        this.processVMObservableList = obProcList;
    }
}
