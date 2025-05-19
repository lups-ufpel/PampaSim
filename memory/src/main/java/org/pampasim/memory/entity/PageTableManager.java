package org.pampasim.memory.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.events.Memory.*;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.memory.resources.PageTableEntry;
import org.pampasim.memory.resources.ProcessPageTable;
import org.pampasim.resources.Process;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PageTableManager extends AbstractSimEntity {

    private final Logger LOGGER = LogManager.getLogger(PageTableManager.class);
    private final Map<Long, ProcessPageTable> pageTableMap;


    public PageTableManager(Simulation simulation) {
        super(simulation);
        pageTableMap = new HashMap<>();

        //TODO: Add the events which this entity handles
        //simulation.getEventManager().addEventHandler(ProcessArrival.class, this);
        //simulation.getEventManager().addEventHandler(ProcessReady.class, this);
        //simulation.getEventManager().addEventHandler(ProcessRunPaused.class, this);
    }

    public void processEvent(Event event) {
        //TODO: always handle process end events before allocation events
        switch (event) {
            case Allocate e -> handleMemoryAllocate(e);
            case DeletePageTableEntry e -> handleMemoryDeletePageTableEntry(e);
            case TlbNoTranslation e -> handleMemoryTlbNoTranslation(e);
            default -> throw new IllegalStateException(
                    "[PageTableManager] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleMemoryAllocate(Allocate event) {
        Process process = event.getProcess();
        ProcessPageTable pageTable = new ProcessPageTable(process.getSize());
        pageTableMap.put(process.getPid().getId(), pageTable);
        LOGGER.debug("Processo de ID {} : Entrada na tabela da páginas criada com sucesso", process.getPid().toString());
        scheduleToNextClock(new AllocateFinished(this, event.getProcess()));
    }

    private void handleMemoryDeletePageTableEntry(DeletePageTableEntry event) {
        Process process = event.getProcess();
        pageTableMap.remove(process.getPid().getId());
        LOGGER.debug("Processo de ID {} : Entrada na tabela da páginas removida com sucesso", process.getPid().toString());
        scheduleToNextClock(new FreeProcessMemory(this, event.getProcess()));
    }

    private void handleMemoryTlbNoTranslation(TlbNoTranslation event) {
        //TODO: the process will store which of it's pages its trying to access, that info is needed for this to work
        //TODO: can result in a MemoryPageHit or a MemoryPageFault event
        //TODO: Stub
        scheduleToNextClock(new PageHit(this, event.getProcess()));
        scheduleToNextClock(new PageFault(this, event.getProcess()));
    }

}
