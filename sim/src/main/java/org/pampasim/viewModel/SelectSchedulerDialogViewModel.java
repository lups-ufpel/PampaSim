package org.pampasim.viewModel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;

public class SelectSchedulerDialogViewModel implements ViewModel {

    private final StringProperty schedulerName = new SimpleStringProperty();
    private final BooleanProperty hasPreemption = new SimpleBooleanProperty(false);
    private final IntegerProperty quantum = new SimpleIntegerProperty();


    public String getSchedulerName() {
        return schedulerName.get();
    }

    public StringProperty schedulerNameProperty() {
        return schedulerName;
    }

    public boolean isHasPreemption() {
        return hasPreemption.get();
    }

    public BooleanProperty hasPreemptionProperty() {
        return hasPreemption;
    }

    public int getQuantum() {
        return quantum.get();
    }

    public IntegerProperty quantumProperty() {
        return quantum;
    }
}
