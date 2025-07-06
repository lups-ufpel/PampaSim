package org.pampasim.resources.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.resources.Process;

@Getter
public class ProcessViewModel implements ViewModel {
    private final long creationId;
    private final ObjectProperty<PidAllocator.Pid> pid = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.BLACK);
    private final ObjectProperty<Process.State> state = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> arrivalTick = new SimpleObjectProperty<>(0);
    private final ObjectProperty<Integer> burst = new SimpleObjectProperty<>(0);
    private final ObjectProperty<Double> progress = new SimpleObjectProperty<>(0.0);
    private final ObjectProperty<Integer> priority = new SimpleObjectProperty<>();
    private final BooleanProperty initialized = new SimpleBooleanProperty(false);
    private final IntegerProperty currExecTime = new SimpleIntegerProperty(0);
    private final IntegerProperty burstTime = new SimpleIntegerProperty(0);

    // Armazena informações dos módulos (ex: memória, IO, etc.)
    private final ObservableList<ModuleInfoViewModel> moduleInfoViewModels = FXCollections.observableArrayList();

    public ProcessViewModel(long creationId) {
        this.creationId = creationId;
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

    public void addModuleInfoViewModel(ModuleInfoViewModel moduleViewModel) {
        this.moduleInfoViewModels.add(moduleViewModel);
    }

    public <T extends ModuleInfoViewModel> T getModuleInfoViewModel(Class<T> clazz) {
        return moduleInfoViewModels.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }
}
