package org.pampasim.resources.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.pampasim.resources.dialog.UpsertProcessDialogService;

import java.util.HashMap;
import java.util.Map;

public class UpsertProcessDialogViewModel implements ViewModel {

    private final IntegerProperty processStart = new SimpleIntegerProperty();
    private final IntegerProperty processDuration = new SimpleIntegerProperty();
    private final IntegerProperty processPriority = new SimpleIntegerProperty();

    private final ObjectProperty<Color> selectedColor = new SimpleObjectProperty<>(pleasantRandomColor());

    @Getter
    private final Map<Class<?>, UpsertProcessDialogService.ModuleTuple> moduleInfo;

    // gpt color theory ahead
    private static double hue = Math.random() * 360;

    public UpsertProcessDialogViewModel(Map<Class<?>, UpsertProcessDialogService.ModuleTuple> moduleInfo) {
        this.moduleInfo = moduleInfo;
    }

    private static Color pleasantRandomColor() {
        hue = (hue + 137.508) % 360;

        double saturation = 0.65 + Math.random() * 0.2;
        double brightness = 0.75 + Math.random() * 0.15;

        return Color.hsb(hue, saturation, brightness);
    }

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
