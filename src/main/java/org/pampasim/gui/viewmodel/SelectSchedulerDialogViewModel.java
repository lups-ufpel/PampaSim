package org.pampasim.gui.viewmodel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import org.pampasim.gui.SchedulerDialogScope;

public class SelectSchedulerDialogViewModel implements ViewModel {
    @InjectScope
    private SchedulerDialogScope schedulerDialogScope;

    public Property<String> getSchedulerNameProperty() {
        return schedulerDialogScope.schedulerNamePropertyProperty();
    }
    public Property<Boolean> getPreemptionProperty() {
        return schedulerDialogScope.preemptionPropertyProperty();
    }
    public Property<Integer> getQuantumProperty() {
        return schedulerDialogScope.quantumPropertyProperty();
    }
}
