package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import javafx.beans.property.SimpleStringProperty;

import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.BlockRecord;

@Getter
public class BlockViewModel implements ViewModel {
    private final int number;
    @Getter private final SimpleStringProperty circleLabel = new SimpleStringProperty("");
    private final ObjectProperty<BlockRecord> blockRecordProperty = new SimpleObjectProperty<>();

    // labels are for development purposes
    public BlockViewModel(int number, String circleLabel, BlockRecord blockRecord) {
        this.number = number;
        this.circleLabel.setValue(circleLabel);
        this.blockRecordProperty.setValue(blockRecord);
    }

    public BlockRecord getBlockRecord(){
      return blockRecordProperty.get();
    }

    public BlockType getType(){
      return blockRecordProperty.get().type();
    }

    public void setBlockRecord(BlockRecord blockRecord){
      this.blockRecordProperty.set(blockRecord);
    }

    public ObjectProperty<BlockRecord> blockRecordProperty(){
      return blockRecordProperty;
    }

}
