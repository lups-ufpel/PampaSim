package org.pampasim.resources.viewmodel;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.ObservableList;
import org.pampasim.core.SimulationBase;

import java.util.List;

public interface StatisticsViewModel extends ViewModel {
    public abstract void updateStatistics(SimulationBase simulation, List<ProcessViewModel> processes);
}
