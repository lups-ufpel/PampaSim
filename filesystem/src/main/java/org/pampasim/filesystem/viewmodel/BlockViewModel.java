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
    private final ObjectProperty<BlockType> typeProperty = new SimpleObjectProperty<>();

    // labels are for development purposes
    public BlockViewModel(int number, String circleLabel, BlockType type) {
        this.number = number;
        this.circleLabel.setValue(circleLabel);
        this.typeProperty.setValue(type);
    }

    public BlockType getType(){
      return typeProperty.get();
    }

    public void setType(BlockType type){
      this.typeProperty.set(type);
    }

    public ObjectProperty<BlockType> typeProperty(){
      return typeProperty;
    }

    public void onClicked(){
      System.out.println("clicked on block " + number);
      switch(typeProperty.get()){
        case MBR:
        case EMPTY:
        case INITIALIZATION:
        case SUPERBLOCK:
        case FREE_BLOCKS_BITMAP:
        case FREE_INODES_BITMAP:
        case INODE_TABLE:
        case INODE:
        case FILE:
        case DIRECTORY:
      }
    }
}
