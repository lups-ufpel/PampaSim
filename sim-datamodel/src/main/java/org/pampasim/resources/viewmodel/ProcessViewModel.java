package org.pampasim.resources.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.NonNull;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.resources.Process;

import java.lang.ref.WeakReference;
import java.util.Optional;
import java.util.function.Consumer;

@Getter
public class ProcessViewModel implements ViewModel {
    private WeakReference<Process> processRef = null;
    private Process.CreationData creationData;
    private final ObjectProperty<PidAllocator.Pid> pid = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.BLACK);
    private final ObjectProperty<Process.State> state = new SimpleObjectProperty<>();
    private final ObjectProperty<Integer> arrivalTick = new SimpleObjectProperty<>(0);
    private final ObjectProperty<Integer> burst = new SimpleObjectProperty<>(0);
    private final ObjectProperty<Double> progress = new SimpleObjectProperty<>(0.0);
    private final ObjectProperty<Integer> priority = new SimpleObjectProperty<>();
    private final BooleanProperty initialized = new SimpleBooleanProperty(false);
    private final IntegerProperty readyWaitingTime = new SimpleIntegerProperty(0);
    private final IntegerProperty currExecTime = new SimpleIntegerProperty(0);
    private final IntegerProperty burstTime = new SimpleIntegerProperty(0);
    private final IntegerProperty endTime = new SimpleIntegerProperty();

    // bodge to make the UD part of CRUD work with the edit and remove buttons
    private final Consumer<ProcessViewModel> editCallback;
    private final Consumer<ProcessViewModel> deleteCallback;

    // Armazena informações dos módulos (ex: memória, IO, etc.)
    private final ObservableList<ModuleInfoViewModel> moduleInfoViewModels = FXCollections.observableArrayList();

    public ProcessViewModel(@NonNull Process.CreationData creationData, Consumer<ProcessViewModel> editCallback, Consumer<ProcessViewModel> deleteCallback) {
        this.creationData = creationData;
        this.editCallback = editCallback;
        this.deleteCallback = deleteCallback;
    }

    /// callback hell magic, TODO FIXME
    public void delete() {
        this.deleteCallback.accept(this);
        this.publish("CloseInspectors");
    }

    /// callback hell magic, TODO FIXME
    public void edit() {
        this.editCallback.accept(this);
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

    public boolean tryBinding(@NonNull Process candidate) {
        return Optional.of(candidate)
                .filter(proc -> proc.getCreationData().equals(this.creationData))
                .map(proc -> {
                    this.processRef = new WeakReference<>(candidate);
                    return proc;
                }).isPresent();
    }

    public <T extends ModuleInfoViewModel> T getModuleInfoViewModel(Class<T> clazz) {
        return moduleInfoViewModels.stream()
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .findFirst()
                .orElse(null);
    }
}
