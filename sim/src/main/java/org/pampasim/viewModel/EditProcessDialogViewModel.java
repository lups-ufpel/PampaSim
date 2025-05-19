package org.pampasim.viewModel;

import de.saxsys.mvvmfx.InjectScope;
import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.Property;
import org.pampasim.scopes.EditProcessScope;
import org.pampasim.scopes.ProcessScope;

public class EditProcessDialogViewModel implements ViewModel {

    @InjectScope
    private EditProcessScope processScope;

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
