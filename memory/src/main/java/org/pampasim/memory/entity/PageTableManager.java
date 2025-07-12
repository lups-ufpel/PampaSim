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
    private final Map<Long, ProcessPageTable> pageTableMap; // TODO: with the frame map in the physical memory, this likely isn't needed

    public PageTableManager(Simulation simulation) {
        super(simulation);

        this.pageTableMap = new HashMap<>();
        this.buffer = new PriorityQueue<>(Comparator.comparingInt(this::getEventPriority));

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

        ProcessPageTable pageTable = new ProcessPageTable(process, processMemoryInfo.getSize(), processMemoryInfo.getFileBackedPages());
        processMemoryInfo.setPageTable(pageTable);
        pageTableMap.put(process.getPid().getId(), pageTable);

        LOGGER.debug("Processo de ID {} : Entradas na tabela da páginas criada com sucesso", process.getPid().toString());
        scheduleToNextClock(new AllocateFinished(this, process));
    }

    private void handleMemoryDeletePageTableEntry(DeletePageTableEntry event) {
        Process process = event.getProcess();

        process.getModuleInfo(ProcessMemoryInfo.class).setPageTable(null);
        pageTableMap.remove(process.getPid().getId());

        LOGGER.debug("Processo de ID {} : Entrada na tabela da páginas removida com sucesso", process.getPid().toString());
        scheduleToNextClock(new DeleteTlbEntry(this, process));
    }

    private void handleMemoryTlbNoTranslation(TlbNoTranslation event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        ArrayList<Integer> accessList = processMemoryInfo.getCurrentAccessList();
        ArrayList<Boolean> modifyFlags = processMemoryInfo.getCurrentModifyPageFlags();
        ProcessPageTable pageTable = processMemoryInfo.getPageTable();
        PageTableEntry pageTableEntry;

        // Verify both lists have the same size
        if (modifyFlags != null && accessList.size() != modifyFlags.size()) {
            LOGGER.error("Process ID {}: Access list and modify flags size mismatch", process.getPid());
            scheduleToNextClock(new FreeProcessMemory(this, process));
            return;
        }

        for (int i = 0; i < accessList.size(); i++) {
            int access = accessList.get(i);
            int pageNo = MemoryConfig.extractPageNumber(access);
            try {
                pageTableEntry = pageTable.getEntry(pageNo);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Process ID {}: Illegal access! Segmentation fault for address {}", process.getPid(), access);
                scheduleToNextClock(new FreeProcessMemory(this, process));
                return;
            }

            pageTableEntry.setReferenced(true);
            processMemoryInfo.registerReference();

            // Set dirty bit if this is a write operation
            if (modifyFlags != null && modifyFlags.get(i)) {
                pageTableEntry.setDirty(true);
            }

            if (pageTableEntry.getFrameAddress() == null || !pageTableEntry.isValid()) {
                // if there is at least 1 page fault, suspend process and send a PageFault event
                if (pageTableEntry.getFrameAddress() == null) {
                    LOGGER.debug("Process ID {}: Page table access generated Page Fault (No translation)", process.getPid());
                } else {
                    LOGGER.debug("Process ID {}: Page table access generated Page Fault (Valid bit 0)", process.getPid());
                }

                process.setState(Process.State.IO_WAITING);
                scheduleToNextClock(new PageFault(this, process));
                return;
            }
        }

        // If no page faults found, all entries were present in memory
        LOGGER.debug("Process ID {}: Page table access generated only Page Hits", process.getPid());
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
