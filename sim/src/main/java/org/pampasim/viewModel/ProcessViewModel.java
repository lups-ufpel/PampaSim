package org.pampasim.viewModel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
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

    public ProcessViewModel(Process.CreationData cdata) {
        this.creationData = cdata;
        this.setState(Process.State.NEW);
    }

    public void setState(Process.State newState) {
        this.state.set(newState);
    }

    public Process.State getState() {
        return state.get();
    }

    public ObjectProperty<Process.State> stateProperty() {
        return state;
    }
}
