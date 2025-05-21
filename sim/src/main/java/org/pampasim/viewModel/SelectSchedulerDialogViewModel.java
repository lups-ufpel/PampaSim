package org.pampasim.viewModel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class SelectSchedulerDialogViewModel implements ViewModel {

    private final ObservableList<String> schedulerName = FXCollections.observableArrayList();

    // ─────────────── Values the user can change ─────────────
    private final StringProperty selectedScheduler = new SimpleStringProperty();
    private final BooleanProperty preemptive       = new SimpleBooleanProperty(false);
    private final IntegerProperty quantum          = new SimpleIntegerProperty();


    /* ========== public API ========== */

    public void setSchedulerNames(List<String> names) {
        schedulerName.setAll(names);
        setDefaultSelectedScheduler(names);
    }

    public void setDefaultSelectedScheduler(List<String> names) {
        if(!names.isEmpty()) {
            selectedScheduler.set(names.get(0));
        }
    }

    public ObservableList<String> schedulerNameProperty () {
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
