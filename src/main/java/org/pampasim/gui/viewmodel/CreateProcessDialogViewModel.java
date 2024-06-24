package org.pampasim.gui.viewmodel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import org.pampasim.gui.ProcessScope;

public class CreateProcessDialogViewModel implements ViewModel {

    @InjectScope
    private ProcessScope processScope;

    public Property<Integer> getBurstProperty() {
        return processScope.getBurstProperty();
    }

    public Property<Integer> getPriorityProperty() {
        return processScope.getPriorityProperty();
    }

    public Property<Integer> getDurationProperty() {
        return processScope.getDurationProperty();
    }
}
