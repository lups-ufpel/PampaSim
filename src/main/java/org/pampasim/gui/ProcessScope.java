package org.pampasim.gui;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

public class ProcessScope implements Scope {
    private static final int INITIAL_VALUE = 1;
    private final Property<Integer> burstProperty = new SimpleObjectProperty<>(INITIAL_VALUE);
    private final Property<Integer> priorityProperty = new SimpleObjectProperty<>(INITIAL_VALUE);
    private final Property<Integer> durationProperty = new SimpleObjectProperty<>(INITIAL_VALUE);

    public Property<Integer> getBurstProperty() {
        return burstProperty;
    }

    public Property<Integer> getPriorityProperty() {
        return priorityProperty;
    }

    public Property<Integer> getDurationProperty() {
        return durationProperty;
    }
}

