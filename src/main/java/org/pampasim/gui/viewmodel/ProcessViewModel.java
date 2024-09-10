package org.pampasim.gui.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.pampasim.SimResources.Process;

public class ProcessViewModel implements ViewModel {

    private final StringProperty pid = new SimpleStringProperty();
    private final IntegerProperty priority = new SimpleIntegerProperty();
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>();
    private final ObjectProperty<Process.State> state = new SimpleObjectProperty<>();

    public ProcessViewModel(String pid, int priority, Color color) {
        this.pid.set(pid);
        this.priority.set(priority);
        this.color.set(color);
    }
    public IntegerProperty priorityProperty() {
        return priority;
    }
    public ObjectProperty<Color> colorObjectProperty() {
        return color;
    }
    public ObjectProperty<Color> colorProperty() {
        return color;
    }
    public StringProperty pidProperty() {
        return pid;
    }
    public int getPriority() {
        return priority.get();
    }
    public Color getColor(){
        return color.get();
    }
    public String pid() {
        return pid.get();
    }
    public Process.State getState() {
        return state.get();
    }
    public void setState(Process.State state) {
        this.state.set(state);
    }
    public ObjectProperty<Process.State> stateProperty() {
        return state;
    }
}
