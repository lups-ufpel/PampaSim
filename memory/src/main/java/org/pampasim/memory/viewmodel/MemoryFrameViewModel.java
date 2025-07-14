package org.pampasim.memory.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;

@Getter
public class MemoryFrameViewModel implements ViewModel {
    private final int frameNum;
    private final ObjectProperty<PidAllocator.Pid> pid = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.BLACK);
    private final IntegerProperty pageNumber = new SimpleIntegerProperty(-1); // -1 means unused
    private final BooleanProperty referenced = new SimpleBooleanProperty(false);

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
