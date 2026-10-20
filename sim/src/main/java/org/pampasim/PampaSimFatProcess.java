package org.pampasim;


import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.resources.ProcessImpl;
import org.pampasim.resources.viewmodel.ProcessViewModel;

/**
 * "Fat process" in the way it keeps track of extra references for the JavaFX MVVM GUI
 */
public class PampaSimFatProcess implements org.pampasim.resources.Process {
    @Delegate
    private org.pampasim.resources.Process process;
    @Getter
    @Setter
    ProcessViewModel processViewModel = null;
    ObservableMap<Integer, State> ganttData = FXCollections.observableHashMap();

    public PampaSimFatProcess(org.pampasim.resources.Process process) {
        this.process = process;
        this.processViewModel = null;
    }
}
