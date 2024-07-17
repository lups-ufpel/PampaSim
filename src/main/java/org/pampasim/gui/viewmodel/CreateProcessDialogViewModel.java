package org.pampasim.gui.viewmodel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import org.pampasim.gui.scopes.ProcessScope;

public class CreateProcessDialogViewModel implements ViewModel {

    @InjectScope
    private ProcessScope processScope;

    public Property<Integer> getStartProperty() {
        return processScope.getStartTimeProperty();
    }

    public Property<Integer> getDurationProperty() {
        return processScope.getDurationProperty();
    }

    public Property<Integer> getPriorityProperty() {
        return processScope.getPriorityProperty();
    }

    public Property<String> getColorProperty(){
        return processScope.getColorProperty();
    }
}
