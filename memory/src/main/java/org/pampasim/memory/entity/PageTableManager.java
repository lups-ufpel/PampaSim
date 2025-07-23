package org.pampasim.memory.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.Simulation;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.core.events.Event;
import org.pampasim.events.Memory.*;
import org.pampasim.memory.MemoryConfig;
import org.pampasim.resources.Process;
import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.memory.ProcessMemoryInfo;
import org.pampasim.resources.memory.ProcessPageTable;

import java.util.*;

public class PageTableManager extends AbstractSimEntity {

    private final Logger LOGGER = LogManager.getLogger(PageTableManager.class);
    // a map of all page tables for easy access later for algorithms that need to consult all process tables (for example, page substitution algorithms with a global policy)
    private final Map<Process, ProcessPageTable> pageTableMap;
    private final int referencedBitReset;
    private int referenceCounter = 0;
    public PageTableManager(Simulation simulation, int referencedBitReset) {
        super(simulation);

        this.pageTableMap = new HashMap<>();
        this.buffer = new PriorityQueue<>(Comparator.comparingInt(this::getEventPriority));
        this.referencedBitReset = referencedBitReset;

        simulation.getEventManager().addEventHandler(Allocate.class, this);
        simulation.getEventManager().addEventHandler(DeletePageTableEntry.class, this);
        simulation.getEventManager().addEventHandler(TlbNoTranslation.class, this);
    }

    public void processEvent(Event event) {
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
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);

        ProcessPageTable pageTable = new ProcessPageTable(process);
        processMemoryInfo.setPageTable(pageTable);
        pageTableMap.put(process, pageTable);

        LOGGER.debug("Processo de ID {} : Entradas na tabela da páginas criada com sucesso", process.getPid().toString());
        scheduleToNextClock(new AllocateFinished(this, process));
    }

    private void handleMemoryDeletePageTableEntry(DeletePageTableEntry event) {
        Process process = event.getProcess();

        process.getModuleInfo(ProcessMemoryInfo.class).setPageTable(null);
        pageTableMap.remove(process);

        LOGGER.debug("Processo de ID {} : Entrada na tabela da páginas removida com sucesso", process.getPid().toString());
        scheduleToNextClock(new DeleteTlbEntry(this, process));
    }

    private void handleMemoryTlbNoTranslation(TlbNoTranslation event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        Integer access = processMemoryInfo.getCurrentAccess();
        Boolean modifyFlag = processMemoryInfo.getCurrentModifyPageFlag();
        ProcessPageTable pageTable = processMemoryInfo.getPageTable();
        PageTableEntry pageTableEntry = null;

        if (access == null) {
            LOGGER.error("Process ID {}: No access address found", process.getPid());
            scheduleToNextClock(new FreeProcessMemory(this, process));
            return;
        }

        int pageNo = MemoryConfig.extractPageNumber(access);
        try {
            pageTableEntry = pageTable.getEntry(pageNo);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Process ID {}: Illegal access! Segmentation fault for address {}", process.getPid(), access);
            scheduleToNextClock(new FreeProcessMemory(this, process));
            return;
        }

        if (pageTableEntry.getFrameNumber() == null || !pageTableEntry.isValid()) {
            // if there is a page fault, suspend process and send a PageFault event
            if (pageTableEntry.getFrameNumber() == null) {
                LOGGER.debug("Process ID {}: Page table access generated Page Fault (No translation)", process.getPid());
            } else {
                LOGGER.debug("Process ID {}: Page table access generated Page Fault (Valid bit 0)", process.getPid());
            }

            process.setState(Process.State.IO_WAITING);
            scheduleToNextClock(new PageFault(this, process));
            return;
        }

        // If no page fault found, entry was present in memory

        // Set dirty bit if this is a write operation
        if (modifyFlag != null && modifyFlag) {
            pageTableEntry.setDirty(true);
        }

        pageTableEntry.setReferenced(true); //register reference only for page hits to compute the working set, otherwise there are problems with double-counting
        referenceCounter++;

        LOGGER.debug("Process ID {}: Page table access generated Page Hit", process.getPid());
        if (referenceCounter >= referencedBitReset) {
            pageTableMap.values().forEach(processPageTable -> processPageTable.getProcessMemoryInfo().computeWorkingSet());
            referenceCounter = 0;
        }



        scheduleToNextClock(new PageHit(this, process));
    }

    @Override
    public void managedRun() {
        while (!buffer.isEmpty()) {
            processEvent(buffer.poll()); // Order: DeletePageTableEntry -> Allocate
        }
    }

    private int getEventPriority(Event event) {
        return switch (event) {
            case DeletePageTableEntry _e -> 1;   // Highest priority
            case Allocate _e -> 2;
            default -> Integer.MAX_VALUE;
        };
    }

}