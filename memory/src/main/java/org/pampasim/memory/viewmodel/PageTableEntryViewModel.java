package org.pampasim.memory.viewmodel;

import javafx.beans.property.*;
import org.pampasim.resources.memory.PageTableEntry;

public class PageTableEntryViewModel {
    private final IntegerProperty pageNumber = new SimpleIntegerProperty();
    private final BooleanProperty valid = new SimpleBooleanProperty();
    private final BooleanProperty dirty = new SimpleBooleanProperty();
    private final BooleanProperty referenced = new SimpleBooleanProperty();
    private final BooleanProperty fileBacked = new SimpleBooleanProperty();
    private final ObjectProperty<Integer> frameAddress = new SimpleObjectProperty<>();

    public PageTableEntryViewModel(PageTableEntry entry) {
        pageNumber.set(entry.getPageNumber());
        valid.set(entry.isValid());
        dirty.set(entry.isDirty());
        referenced.set(entry.isReferenced());
        fileBacked.set(entry.isFileBacked());
        frameAddress.set(entry.getFrameAddress());
    }

    public IntegerProperty pageNumberProperty() { return pageNumber; }
    public BooleanProperty validProperty() { return valid; }
    public BooleanProperty dirtyProperty() { return dirty; }
    public BooleanProperty referencedProperty() { return referenced; }
    public BooleanProperty fileBackedProperty() { return fileBacked; }
    public ObjectProperty<Integer> frameAddressProperty() { return frameAddress; }
}
