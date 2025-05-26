package org.pampasim.viewModel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Getter;
import lombok.Setter;
import org.pampasim.resources.Process;
import org.pampasim.core.utils.PidAllocator;

@Getter
public class ProcessViewModel implements ViewModel {
    private final ObjectProperty<PidAllocator.Pid> pid = new SimpleObjectProperty<>();
    private final Process.CreationData creationData;
    private final IntegerProperty priority = new SimpleIntegerProperty();
    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.BLACK);
    private final ObjectProperty<Process.State> state = new SimpleObjectProperty<>();
    private final BooleanProperty initialized = new SimpleBooleanProperty(false);
    private final IntegerProperty currExecTime = new SimpleIntegerProperty(0);
    private final IntegerProperty burstTime = new SimpleIntegerProperty(0);
    @Setter
    private Circle circleRepr = null;

    public ProcessViewModel(Process.CreationData cdata) {
        this.creationData = cdata;
        //this.pid.setValue(null);
        this.state.setValue(Process.State.NEW); // bodge fix for now
    }

    public void setState(Process.State newState) {
        this.state.setValue(newState);
    }

    public Process.State getState() {
        return state.get();
    }

    public ObjectProperty<Process.State> stateProperty() {
        return state;
    }
}
