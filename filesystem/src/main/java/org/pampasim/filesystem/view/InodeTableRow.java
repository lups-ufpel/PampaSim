package org.pampasim.filesystem.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;

public class InodeTableRow {

    private final StringProperty label;
    private final IntegerProperty value;
    private final boolean metadataRow;

    public InodeTableRow(String label, IntegerProperty value) {
        this.label = new SimpleStringProperty(label);
        this.value = value;
        this.metadataRow = false;
    }

    // metadata row constructor
    public InodeTableRow(String label) {
        this.label = new SimpleStringProperty(label);
        this.value = new SimpleIntegerProperty(0);
        this.metadataRow = true;
    }

    public StringProperty labelProperty() {
        return label;
    }

    public IntegerProperty valueProperty() {
        return value;
    }

    public boolean isMetadataRow() {
        return metadataRow;
    }
}
