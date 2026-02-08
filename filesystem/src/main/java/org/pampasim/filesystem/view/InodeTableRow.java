package org.pampasim.filesystem.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;

public class InodeTableRow {

    private final StringProperty label;
    private final ObservableValue<Number> value;
    private final boolean metadataRow;

    public InodeTableRow(String label, ObservableValue<Number> value) {
        this.label = new SimpleStringProperty(label);
        this.value = value;
        this.metadataRow = false;
    }

    // metadata row constructor
    public InodeTableRow(String label) {
        this.label = new SimpleStringProperty(label);
        this.value = null;
        this.metadataRow = true;
    }

    public StringProperty labelProperty() {
        return label;
    }

    public ObservableValue<Number> valueProperty() {
        return value;
    }

    public boolean isMetadataRow() {
        return metadataRow;
    }
}
