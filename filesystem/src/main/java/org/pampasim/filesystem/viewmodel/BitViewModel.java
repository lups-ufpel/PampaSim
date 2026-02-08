package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import javafx.beans.property.SimpleStringProperty;

@Getter
public class BitViewModel implements ViewModel {

    private final int number;

    private final SimpleStringProperty circleLabel = new SimpleStringProperty("");
    private final BooleanProperty bitProperty = new SimpleBooleanProperty(false);

    private final ReadOnlyIntegerWrapper displayNumber =
        new ReadOnlyIntegerWrapper();

    public BitViewModel(int number, boolean bit, int numberOffset) {
        this.number = number;
        this.bitProperty.set(bit);

        displayNumber.set(number + numberOffset);
    }

    // internal value
    public int getNumber() {
        return number;
    }

    // display value
    public ReadOnlyIntegerProperty displayNumberProperty() {
        return displayNumber.getReadOnlyProperty();
    }

    public int getDisplayNumber() {
        return displayNumber.get();
    }

    public BooleanProperty bitProperty() {
        return bitProperty;
    }

    public Boolean getBit() {
        return bitProperty.get();
    }

    public void setBit(Boolean bit) {
        this.bitProperty.set(bit);
    }
}
