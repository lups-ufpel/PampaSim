package org.pampasim.gui;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;

public class SchedulerDialogScope implements Scope {
    private static final int INITIAL_VALUE = 2;
    private final Property<String> schedulerNameProperty = new SimpleStringProperty();
    private final Property<Boolean> preemptionProperty = new SimpleBooleanProperty();
    private final Property<Integer> quantumProperty = new SimpleObjectProperty<>(INITIAL_VALUE);

    public Property<String> schedulerNamePropertyProperty() {
        return schedulerNameProperty;
    }
    public Property<Boolean> preemptionPropertyProperty() {
        return preemptionProperty;
    }
    public Property<Integer> quantumPropertyProperty() {
        return quantumProperty;
    }
}
