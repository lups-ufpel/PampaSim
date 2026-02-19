package org.pampasim.filesystem.view;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class FATEntry {

    private final IntegerProperty index = new SimpleIntegerProperty();
    private final IntegerProperty value = new SimpleIntegerProperty();

    public FATEntry(int index, int value) {
        this.index.set(index);
        this.value.set(value);
    }

    public int getIndex() {
        return index.get();
    }

    public IntegerProperty indexProperty() {
        return index;
    }

    public int getValue() {
        return value.get();
    }

    public IntegerProperty valueProperty() {
        return value;
    }
}
