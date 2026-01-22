package org.pampasim.filesystem.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class InodeTableRow {

    private final StringProperty field;
    private final IntegerProperty value;

    public InodeTableRow(String field, IntegerProperty value) {
        this.field = new SimpleStringProperty(field);
        this.value = value;
    }

    public StringProperty fieldProperty() {
        return field;
    }

    public IntegerProperty valueProperty() {
        return value;
    }
}
