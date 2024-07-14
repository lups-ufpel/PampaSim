package org.pampasim.gui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.*;
import lombok.Getter;

@Getter
public class SchedulerDialogScope implements Scope {
    private static final int INITIAL_VALUE = 2;
    private final Property<String> schedulerNameProperty = new SimpleStringProperty();
    private final Property<Boolean> preemptionProperty = new SimpleBooleanProperty();
    private final Property<Integer> quantumProperty = new SimpleObjectProperty<>(INITIAL_VALUE);
}
