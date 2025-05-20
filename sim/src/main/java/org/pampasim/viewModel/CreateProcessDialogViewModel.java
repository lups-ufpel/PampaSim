package org.pampasim.viewModel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

public class CreateProcessDialogViewModel implements ViewModel {


    private final IntegerProperty processStart = new SimpleIntegerProperty();
    private final IntegerProperty processDuration = new SimpleIntegerProperty();
    private final IntegerProperty processPriority = new SimpleIntegerProperty();

    private final ObjectProperty<javafx.scene.paint.Color> selectedColor = new SimpleObjectProperty<>(javafx.scene.paint.Color.BLUE);

    public IntegerProperty processStartProperty() {
        return processStart;
    }
    public IntegerProperty processDurationProperty() {
        return processDuration;
    }
    public IntegerProperty processPriorityProperty() {
        return processPriority;
    }
    public Property<Color> colorHexProperty() {
        return selectedColor;
    }
}
