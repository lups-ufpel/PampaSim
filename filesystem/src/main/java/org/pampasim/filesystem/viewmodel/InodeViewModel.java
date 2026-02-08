package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.binding.Bindings;

import org.pampasim.core.events.Event;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.filesystem.core.BlockType;
import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.core.BlockRecord;
import org.pampasim.filesystem.core.Disk;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.FileSystemSimulation;

public class InodeViewModel implements ViewModel {
    @Getter private FileSystemSimulation fileSystemSimulation;
    @Getter private FileSystem fileSystem;
    @Getter private final int index;
    private IntegerProperty singlyIndirectPointerProperty;
    private IntegerProperty[] directAddresses = new IntegerProperty[Inode.ADDRESSES_NUMBER];
    private IntegerProperty[] indirectAddresses = new IntegerProperty[Inode.ADDRESSES_NUMBER];

    public InodeViewModel(FileSystemSimulation fileSystemSimulation, int index) {
        this.index = index;
        this.fileSystemSimulation = fileSystemSimulation;
        this.fileSystem = fileSystemSimulation.getFileSystem();

        int size = Inode.ADDRESSES_NUMBER;
        directAddresses = new IntegerProperty[size];

        for (int i = 0; i < size; i++) {
            directAddresses[i] = new SimpleIntegerProperty(0);
            indirectAddresses[i] = new SimpleIntegerProperty(0);
        }
        singlyIndirectPointerProperty = new SimpleIntegerProperty(0);

        setAttributes(index);
        addDiskWriteListeners(fileSystemSimulation.getDisk());
    }

    public void addDiskWriteListeners(Disk disk){
        disk.getTotalWritesProperty().addListener((InvalidationListener) obs -> {
          setAttributes(index);
        });
    }

    //TODO: missing metadata for now
    public void setAttributes(int index){
      Inode self = Inode.get(fileSystem, index);

      int[] inodeDirectAddresses = self.getDirectAddresses();
      int[] inodeIndirectAddresses = self.getIndirectAddresses();
      for(int i = 0; i < Inode.ADDRESSES_NUMBER; i++){
        directAddresses[i].set(inodeDirectAddresses[i]);
        indirectAddresses[i].set(inodeIndirectAddresses[i]);
      }
      singlyIndirectPointerProperty.set(self.getSinglyIndirectPointer()); 

    }

    public ObservableValue<Number> directAddressDisplayProperty(int index) {
        return Bindings.createIntegerBinding(
            () -> {
                int value = directAddresses[index].get();
                // keeps empty addresses the same
                return (value == 0) ? 0 : fileSystem.absoluteAddressOf(value);
            },
            directAddresses[index]
        );
    }

    public IntegerProperty directAddressProperty(int index) {
        return directAddresses[index];
    }

    public IntegerProperty singlyIndirectPointerProperty() {
        return singlyIndirectPointerProperty;
    }

    public IntegerProperty indirectAddressProperty(int index) {
        return indirectAddresses[index];
    }
}
