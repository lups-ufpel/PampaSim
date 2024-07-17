package org.pampasim.gui.scopes;

import de.saxsys.mvvmfx.Scope;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;

@Getter
public class ProcessScope implements Scope {
    private final Property<Integer> startTimeProperty = new SimpleObjectProperty<>();
    private final Property<Integer> durationProperty = new SimpleObjectProperty<>();
    private final Property<Integer> priorityProperty = new SimpleObjectProperty<>();
    private final Property<String> colorProperty = new SimpleObjectProperty<>();
}

