package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import javafx.beans.property.SimpleStringProperty;

import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.filesystem.core.BlockType;

@Getter
public class BlockViewModel implements ViewModel {
    private final int number;
    @Getter private final SimpleStringProperty circleLabel = new SimpleStringProperty("");
    //private final ObjectProperty<>
    private final ObjectProperty<BlockType> typeProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<Color> colorProperty = new SimpleObjectProperty<>(Color.BLACK);
    //private final IntegerProperty pageNumber = new SimpleIntegerProperty(-1); // -1 means unused

    // labels are for development purposes
    public BlockViewModel(int number, String circleLabel, BlockType type) {
        this.number = number;
        this.circleLabel.setValue(circleLabel);
        this.typeProperty.setValue(type);
    }

    Color getColor() {
        return colorProperty.get();
    }

    public void setColor(Color color) {
        colorProperty.set(color);
    }

    public BlockType getType(){
      return typeProperty.get();
    }

    public ObjectProperty<BlockType> typeProperty(){
      return typeProperty;
    }
}
