package org.pampasim.resources.viewmodel;
import de.saxsys.mvvmfx.ViewModel;
import javafx.collections.ObservableList;
import org.pampasim.core.SimulationBase;

public interface StatisticsViewModel extends ViewModel {
    public abstract void updateStatistics(SimulationBase simulation, ObservableList<ProcessViewModel> processes);
}
