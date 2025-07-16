package org.pampasim.resources.viewmodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import org.pampasim.resources.memory.ProcessPageTable;

@Getter
public class PageTableViewModel {
    private final ObservableList<PageTableEntryViewModel> entries = FXCollections.observableArrayList();

    public PageTableViewModel(ProcessPageTable pageTable) {
        if (pageTable != null) {
            pageTable.getAllEntries().forEach(entry ->
                    entries.add(new PageTableEntryViewModel(entry))
            );
        }
    }

}
