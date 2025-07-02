package org.pampasim.memory.viewmodel;

import de.saxsys.mvvmfx.ViewModel;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.events.Event;
import org.pampasim.memory.MemoryManagement;

public class MemoryTabViewModel implements ViewModel {
    private static final Logger LOGGER = LogManager.getLogger(MemoryTabViewModel.class);

    private MemoryManagement memoryManagement;

    public MemoryTabViewModel(MemoryManagement memoryManagement) {
        this.memoryManagement = memoryManagement;
        setupSnoopers();
    }

    public void setupSnoopers() {
        memoryManagement.getEventManager().addSnooper(org.pampasim.events.ProcessEvent.class,
                this::handleProcessEvent);

    }

    public void handleProcessEvent(Event uncastEvent) {
        LOGGER.debug("MemoryTabViewModel observed event {}", uncastEvent);
    }

    public void setMemoryManagement(MemoryManagement memoryManagement) {
        this.memoryManagement = memoryManagement;
        setupSnoopers();
    }
}
