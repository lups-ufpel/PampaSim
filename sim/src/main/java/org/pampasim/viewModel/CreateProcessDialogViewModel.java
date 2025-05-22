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
    public int getProcessStart() {
        return processStart.get();
    }
    public int getProcessDuration() {
        return processDuration.get();
    }
    public int getProcessPriority() {
        return processPriority.get();
    }
    public String convertColor() {

        Color col = selectedColor.get();
        int r = (int) Math.round(col.getRed() * 255);
        int g = (int) Math.round(col.getGreen() * 255);
        int b = (int) Math.round(col.getBlue() * 255);
        return String.format("#%02x%02x%02x", r, g, b);
    }
}
