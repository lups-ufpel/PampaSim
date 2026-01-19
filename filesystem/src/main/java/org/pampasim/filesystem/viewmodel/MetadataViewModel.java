package org.pampasim.filesystem.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;

import java.time.Instant;

import org.pampasim.filesystem.core.FileSystem;
import org.pampasim.filesystem.file.FileMetadata;

public class MetadataViewModel implements ViewModel {

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

    public MetadataViewModel(FileSystem fileSystem, String filePath){
      FileMetadata metadata = fileSystem.findMetadata(filePath);
      currentSize.set(metadata.getCurrentSizeBytes());
      creationTime.set(metadata.getCreationTime());
      lastAccessTime.set(metadata.getLastAccess());
      lastModifiedTime.set(metadata.getLastModified());
      directory.set(metadata.isDirectory());
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
