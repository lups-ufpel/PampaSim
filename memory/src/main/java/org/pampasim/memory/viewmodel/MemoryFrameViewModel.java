package org.pampasim.memory.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;

@Getter
public class MemoryFrameViewModel implements ViewModel {
    private final int frameNum;
    private final ObjectProperty<PidAllocator.Pid> pid = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.BLACK);

    MemoryFrameViewModel(int frameNum) {
        this.frameNum = frameNum;
    }

    Color getColor() {
        return colorProperty.get();
    }

    void setColor(Color color) {
        colorProperty.set(color);
    }
}
