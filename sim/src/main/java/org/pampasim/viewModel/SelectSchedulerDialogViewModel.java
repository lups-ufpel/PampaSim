package org.pampasim.viewModel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class SelectSchedulerDialogViewModel implements ViewModel {

    private final ObjectProperty<ObservableList<String>> schedulerName = new SimpleObjectProperty<>(FXCollections.observableArrayList());

    // ─────────────── Values the user can change ─────────────
    private final StringProperty selectedScheduler = new SimpleStringProperty();
    private final BooleanProperty preemptive       = new SimpleBooleanProperty(false);
    private final IntegerProperty quantum          = new SimpleIntegerProperty();


    /* ========== public API ========== */

    public void setSchedulerNames(List<String> names) {
        schedulerName.get().setAll(names);
        if(!schedulerName.get().isEmpty() && selectedScheduler.get() == null) {
            selectedScheduler.set(schedulerName.get().getFirst());
        }
    }

    public ObjectProperty<ObservableList<String>> schedulerNameProperty () {
        return schedulerName;
    }

    public StringProperty selectedSchedulerProperty() {
        return selectedScheduler;
    }
    public BooleanProperty preemptiveProperty() {
        return preemptive;
    }
    public IntegerProperty quantumProperty() {
        return quantum;
    }

    /* Helper getters (used by the service after showAndWait()) */
    public String  getSelectedScheduler() { return selectedScheduler.get(); }
    public boolean isPreemptive()         { return preemptive.get();        }
    public int     getQuantum()           { return quantum.get();           }

}
