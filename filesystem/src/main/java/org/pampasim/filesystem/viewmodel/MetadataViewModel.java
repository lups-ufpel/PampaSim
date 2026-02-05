package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;

import java.time.Instant;

import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.file.FileMetadata;
import org.pampasim.filesystem.inode.Inode;
import org.pampasim.filesystem.FileSystemSimulation;
import javafx.beans.InvalidationListener;
import java.util.NoSuchElementException;

public class MetadataViewModel implements ViewModel {

    private InvalidationListener totalWritesListener;
    private FileSystemSimulation fileSystemSimulation;
    private String filePath = ""; //one of these will be blank
    private int inodeIndex = 0;

    private final IntegerProperty currentSize =
            new SimpleIntegerProperty(this, "currentSize");

    private final ObjectProperty<Instant> creationTime =
            new SimpleObjectProperty<>(this, "creationTime");

    private final ObjectProperty<Instant> lastAccessTime =
            new SimpleObjectProperty<>(this, "lastAccessTime");

    private final ObjectProperty<Instant> lastModifiedTime =
            new SimpleObjectProperty<>(this, "lastModifiedTime");

    private final BooleanProperty directory =
            new SimpleBooleanProperty(this, "directory");

    public MetadataViewModel(FileSystemSimulation fileSystemSimulation, String filePath){
      this.fileSystemSimulation = fileSystemSimulation;
      this.filePath = filePath;
      setAttributes(fileSystemSimulation.getFileSystem());
      addListener(fileSystemSimulation);
    }

    public MetadataViewModel(FileSystemSimulation fileSystemSimulation, int inodeIndex){
      this.fileSystemSimulation = fileSystemSimulation;
      this.inodeIndex = inodeIndex;
      setAttributes(fileSystemSimulation.getFileSystem());
      addListener(fileSystemSimulation);
    }

    public void setAttributes(FileSystem fileSystem){
      FileMetadata metadata = switch(fileSystem.getAllocationScheme()){
        case CONTIGUOUS -> fileSystem.findMetadata(filePath);
        case FAT -> fileSystem.findMetadata(filePath);
        case INODES -> Inode.getMetadata(fileSystem, inodeIndex);
      };

      currentSize.set(metadata.getCurrentSizeBytes());
      creationTime.set(metadata.getCreationTime());
      lastAccessTime.set(metadata.getLastAccess());
      lastModifiedTime.set(metadata.getLastModified());
      directory.set(metadata.isDirectory());
    }

    public void addListener(FileSystemSimulation sim) {
        totalWritesListener = obs -> {
            try {
                setAttributes(sim.getFileSystem());
            } catch (NoSuchElementException e) {
                // when file this metadata refers to has been removed
                removeListener(sim);
            }
        };

        sim.getDisk()
           .getTotalWritesProperty()
           .addListener(totalWritesListener);
    }

    public void removeListener(FileSystemSimulation sim) {
        if (totalWritesListener != null) {
            sim.getDisk()
               .getTotalWritesProperty()
               .removeListener(totalWritesListener);
            totalWritesListener = null;
        }
    }

    public int getCurrentSize() {
        return currentSize.get();
    }

    public Instant getCreationTime() {
        return creationTime.get();
    }

    public Instant getLastAccessTime() {
        return lastAccessTime.get();
    }

    public Instant getLastModifiedTime() {
        return lastModifiedTime.get();
    }

    public boolean isDirectory() {
        return directory.get();
    }

    /* =======================
       Property getters
       ======================= */

    public IntegerProperty currentSizeProperty() {
        return currentSize;
    }

    public ObjectProperty<Instant> creationTimeProperty() {
        return creationTime;
    }

    public ObjectProperty<Instant> lastAccessTimeProperty() {
        return lastAccessTime;
    }

    public ObjectProperty<Instant> lastModifiedTimeProperty() {
        return lastModifiedTime;
    }

    public BooleanProperty directoryProperty() {
        return directory;
    }
}
