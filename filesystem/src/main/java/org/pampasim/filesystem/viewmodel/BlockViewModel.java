package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;

@Getter
public class BlockViewModel implements ViewModel {
    private final int number;
    //private final ObjectProperty<>
    //private final ObjectProperty<PidAllocator.Pid> pid = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.BLACK);
    //private final IntegerProperty pageNumber = new SimpleIntegerProperty(-1); // -1 means unused

    public BlockViewModel(int number) {
        this.number = number;
    }

    Color getColor() {
        return colorProperty.get();
    }

    void setColor(Color color) {
        colorProperty.set(color);
    }
}
