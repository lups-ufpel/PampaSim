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
    int pageSize;
    int maxPagesPerProcess;

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

        ProcessPageTable pageTable = new ProcessPageTable(process, processMemoryInfo.getSize());
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
        ProcessPageTable pageTable = processMemoryInfo.getPageTable();
        PageTableEntry pageTableEntry;

        for (int access : accessList) {
            int pageNo = MemoryConfig.extractPageNumber(access);
            try {
                pageTableEntry = pageTable.getEntry(pageNo);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Processo de ID {} : Acesso ilegal! Exceção de Segmentação para o endereço {}", process.getPid(), access);
                scheduleToNextClock(new FreeProcessMemory(this, process));
                return;
            }

            pageTableEntry.setReferenced(true);
            processMemoryInfo.registerReference();

            if (pageTableEntry.getFrameAddress() == null || !pageTableEntry.isValid()) {
                // if there is at least 1 page fault, suspend process and send a PageFault event
                if (pageTableEntry.getFrameAddress() == null) {
                    LOGGER.debug("Processo de ID {} : Acesso a tabela de páginas gerou um Page Fault (Sem tradução)", process.getPid().toString());
                } else {
                    LOGGER.debug("Processo de ID {} : Acesso a tabela de páginas gerou um Page Fault (Bit válido 0)", process.getPid().toString());
                }

                process.setState(Process.State.IO_WAITING);
                scheduleToNextClock(new PageFault(this, process));
                return;
            }
        }

        // If no page faults found, all entries were present in memory
        LOGGER.debug("Processo de ID {} : Acesso a tabela de páginas gerou somente Page Hits", process.getPid().toString());
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
