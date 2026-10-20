package org.pampasim;


import javafx.collections.ObservableMap;
import lombok.Getter;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.resources.Process;
import org.pampasim.resources.viewmodel.ProcessViewModel;

/**
 * "Fat process" in the way it keeps track of extra references for the JavaFX MVVM GUI
 */
public class PampaSimFatProcess extends org.pampasim.resources.Process {
    @Getter
    ProcessViewModel processViewModel;
    ObservableMap<Integer, State> ganttData;

    public PampaSimFatProcess(PidAllocator.Pid pid, CreationData creationData) {
        super(pid, creationData);
        processViewModel = null;

    }

}
