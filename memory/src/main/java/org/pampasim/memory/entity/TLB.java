/*
package org.pampasim.memory.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.events.Memory.*;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.resources.Process;
import org.pampasim.resources.memory.PageTableEntry;
import org.pampasim.resources.memory.ProcessMemoryInfo;

import java.util.*;

public class TLB extends AbstractSimEntity {
    private final boolean clearOnContextSwitch;
    private final int maxEntries;
    private final Queue<Integer> lruQueue;
    private final Map<Integer, PageTableEntry> entries; // virtual page number -> page table entry
    private final Logger LOGGER = LogManager.getLogger(TLB.class);

    PidAllocator.Pid currentProcess;

    public TLB(Simulation simulation, boolean clearOnContextSwitch, int maxEntries) {
        super(simulation);

        this.clearOnContextSwitch = clearOnContextSwitch;
        this.maxEntries = maxEntries;
        this.currentProcess = null;

        lruQueue = new LinkedList<>();
        entries = new HashMap<>();

        simulation.getEventManager().addEventHandler(DeleteTlbEntry.class, this);
        simulation.getEventManager().addEventHandler(TranslateVirtualAddress.class, this);
    }

    public void processEvent(Event event) {
        switch (event) {
            case DeleteTlbEntry e -> handleMemoryDeleteTlbEntry(e);
            case TranslateVirtualAddress e -> handleTranslateVirtualAddress(e);
            default -> throw new IllegalStateException(
                    "[TLB] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleMemoryDeleteTlbEntry(DeleteTlbEntry event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        int addressStart = processMemoryInfo.getVirtualAddressStart();
        int processSize = processMemoryInfo.getSize();

        for (int i = addressStart; i < addressStart + processSize; i++) {
            lruQueue.remove(i);
            entries.remove(i);
        }

        LOGGER.trace("Removida as entradas na TLB do processo de identificador {}", process.getPid());
        scheduleToNextClock(new FreeProcessMemory(this, event.getProcess()));
    }

    private void handleTranslateVirtualAddress(TranslateVirtualAddress event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        List<Integer> pageNoAccessed = processMemoryInfo.getCurrentAccessList();
        int virtualAddressStart = processMemoryInfo.getVirtualAddressStart();

        if (clearOnContextSwitch && process.getPid() != currentProcess) {
            flushTLB();
            LOGGER.trace(
                    "Troca de contexto do processo de id {} para processo de id {}, resetando TLB",
                    currentProcess,
                    process.getPid()
            );
            currentProcess = process.getPid();
        }

        List<Integer> accesses = pageNoAccessed.stream()
                .map(addr -> addr + virtualAddressStart)
                .toList();

        boolean noTranslation = false;
        for (Integer access : accesses) {
            if (!entries.containsKey(access)) {
                noTranslation = true;
                addEntry(access, processMemoryInfo.getPageTable().getEntry(access - virtualAddressStart));
            } else {
                registerAccess(access);
            }
        }

        if (noTranslation) {
            LOGGER.trace(
                    "Um dos endereços acessados nesse tick de execução não esta presente na TLB do processo de identificador {}",
                    process.getPid()
            );
            scheduleToNextClock(new TlbNoTranslation(this, event.getProcess()));
        } else {
            for (Integer accessAddr : accesses) {
                PageTableEntry pageTableEntry = processMemoryInfo.getPageTable()
                        .getEntry(accessAddr - virtualAddressStart);

                pageTableEntry.setReferenced(true);
                processMemoryInfo.registerReference();

                if (pageTableEntry.getFrameAddress() == null) {
                    throw new IllegalStateException("TLB não armazena endereços sem tradução!");
                }

                if (!pageTableEntry.isValid()) {
                    // if there is at least 1 page fault, suspend process and send a PageFault event
                    LOGGER.debug(
                            "Processo de ID {} : Acesso a TLB gerou um Page Fault (Bit válido 0)",
                            process.getPid().toString()
                    );
                    process.setState(Process.State.IO_WAITING);
                    scheduleToNextClock(new PageFault(this, event.getProcess()));
                    return;
                }
            }

            // If no page faults found, all entries were present in memory
            LOGGER.debug(
                    "Processo de ID {} : Acesso a TLB gerou somente Page Hits",
                    process.getPid().toString()
            );
            scheduleToNextClock(new PageHit(this, event.getProcess()));
        }
    }

    private void addEntry(Integer addr, PageTableEntry entry) {
        if (entries.size() >= maxEntries) {
            Integer removedEntryAddr = lruQueue.poll();
            PageTableEntry removedEntry = entries.remove(removedEntryAddr);
            LOGGER.trace(
                    "Removida pagina {} do processo {} da TLB por falta de espaço na mesma",
                    removedEntry.getPageNumber(),
                    removedEntry.getProcess().getPid()
            );
        }
        entries.put(addr, entry);
        registerAccess(addr);
    }

    private void registerAccess(Integer addr) {
        lruQueue.remove(addr);
        lruQueue.add(addr);
    }

    private void flushTLB() {
        lruQueue.clear();
        entries.clear();
    }
}*/