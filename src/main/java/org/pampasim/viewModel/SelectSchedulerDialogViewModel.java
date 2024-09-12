package org.pampasim.viewModel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import org.pampasim.scopes.SchedulerDialogScope;

public class SelectSchedulerDialogViewModel implements ViewModel {
    @InjectScope
    private SchedulerDialogScope schedulerDialogScope;

    public Property<String> getSchedulerNameProperty() {
        return schedulerDialogScope.getSchedulerNameProperty();
    }
    public Property<Boolean> getPreemptionProperty() {
        return schedulerDialogScope.getPreemptionProperty();
    }
    public Property<Integer> getQuantumProperty() {
        return schedulerDialogScope.getQuantumProperty();
    }
}
