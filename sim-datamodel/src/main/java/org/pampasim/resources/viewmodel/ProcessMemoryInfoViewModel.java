package org.pampasim.resources.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import javafx.beans.property.*;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProcessMemoryInfoViewModel implements ViewModel {

    private final IntegerProperty processSize = new SimpleIntegerProperty();
    private final ListProperty<Boolean> fileBackedPages = new SimpleListProperty<>(
            FXCollections.observableArrayList(new ArrayList<>())
    );
    private final ListProperty<Integer> memoryAccesses = new SimpleListProperty<>(
            FXCollections.observableArrayList(new ArrayList<>())
    );
    private final ListProperty<Boolean> modifiesPage = new SimpleListProperty<>(
            FXCollections.observableArrayList(new ArrayList<>())
    );
    private final SimpleBooleanProperty loopAccessList = new SimpleBooleanProperty();

    public IntegerProperty processSizeProperty() {
        return processSize;
    }

    public ListProperty<Boolean> fileBackedPagesProperty() {
        return fileBackedPages;
    }

    public ListProperty<Integer> memoryAccessesProperty() {
        return memoryAccesses;
    }

    public ListProperty<Boolean> modifiesPageProperty() {
        return modifiesPage;
    }

    public int getProcessSize() {
        return processSize.get();
    }

    public void setProcessSize(int size) {
        processSize.set(size);
    }

    public List<Boolean> getFileBackedPages() {
        return fileBackedPages.get();
    }

    public void setFileBackedPages(List<Boolean> pages) {
        fileBackedPages.set(FXCollections.observableArrayList(pages));
    }

    public List<Integer> getMemoryAccesses() {
        return memoryAccesses.get();
    }

    public void setMemoryAccesses(List<Integer> accesses) {
        memoryAccesses.set(FXCollections.observableArrayList(accesses));
    }

    public List<Boolean> getModifiesPage() {
        return modifiesPage.get();
    }

    public void setModifiesPage(List<Boolean> modifies) {
        modifiesPage.set(FXCollections.observableArrayList(modifies));
    }

    public boolean getLoopAccessList() {
        return loopAccessList.get();
    }

    public void setLoopAccessList(boolean access) {
        loopAccessList.set(access);
    }


    public void setLoopAccessList(Boolean newVal) {
        loopAccessList.set(newVal);
    }

    public void parseAndSetFileBackedPages(String val) {
        List<Boolean> newPages = new ArrayList<>();
        for (int i = 0; i < processSize.get(); i++) {
            newPages.add(false);
        }

        String[] tokens = val.split(",");
        for (String token : tokens) {
            token = token.trim();
            if (token.contains("-")) {
                String[] bounds = token.split("-");
                if (bounds.length == 2) {
                    int start = Integer.parseInt(bounds[0]);
                    int end = Integer.parseInt(bounds[1]);
                    for (int i = start; i <= end && i < newPages.size(); i++) {
                        newPages.set(i, true);
                    }
                }
            } else {
                int index = Integer.parseInt(token);
                if (index >= 0 && index < newPages.size()) {
                    newPages.set(index, true);
                }
            }
        }
        setFileBackedPages(newPages);
    }

    public void removeAccess(int index) {
        if (index >= 0 && index < memoryAccesses.size()) {
            memoryAccesses.remove(index);
            modifiesPage.remove(index);
        }
    }

    public void addAccessEntry(StringProperty stringProperty, BooleanProperty booleanProperty) {
        try {
            int address = Integer.parseInt(stringProperty.get());
            memoryAccesses.add(address);
            modifiesPage.add(booleanProperty.get());
        } catch (NumberFormatException e) {
            // TODO: log invalid address input
        }
    }

    public void clearAccesses() {
        memoryAccesses.clear();
        modifiesPage.clear();
    }

    public void addAccess(int address, boolean modifies) {
        memoryAccesses.add(address);
        modifiesPage.add(modifies);
    }
}
