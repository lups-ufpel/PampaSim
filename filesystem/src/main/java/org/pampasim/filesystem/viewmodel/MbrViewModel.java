package org.pampasim.filesystem.viewmodel;

import lombok.Getter;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import javafx.beans.property.SimpleStringProperty;

import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.Partition;

@Getter
public class MbrViewModel implements ViewModel {
    private Partition[] partitions;

    public MbrViewModel(Partition[] partitions){
      this.partitions = partitions;
    }
}
