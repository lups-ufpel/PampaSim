package org.pampasim.memory.entity;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.core.utils.PidAllocator;
import org.pampasim.events.Memory.*;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.events.Process.Kill;
import org.pampasim.events.Process.Ready;
import org.pampasim.events.Process.Run;
import org.pampasim.events.Process.Schedule;
import org.pampasim.resources.memory.PageFrameController;
import org.pampasim.resources.Process;
import org.pampasim.resources.memory.ProcessMemoryInfo;

import java.util.ArrayList;
import java.util.OptionalInt;

public class VirtualMemory extends AbstractSimEntity {

    private final Logger LOGGER = LogManager.getLogger(VirtualMemory.class);
    PageFrameController virtualAddressRange;

    public VirtualMemory(Simulation simulation, int virtualAddressRangeSize) {
        super(simulation);

        virtualAddressRange = new PageFrameController(virtualAddressRangeSize);

        //TODO: Add the events which this entity handles
        //simulation.getEventManager().addEventHandler(ProcessArrival.class, this);
        //simulation.getEventManager().addEventHandler(ProcessReady.class, this);
        //simulation.getEventManager().addEventHandler(ProcessRunPaused.class, this);
    }

    @Override
    public void processEvent(Event event) {
        //TODO: always handle process end events before allocation events
        switch (event) {
            case org.pampasim.events.Process.Allocate e -> handleProcessAllocate(e);
            case org.pampasim.events.Process.End e -> handleProcessEnd(e);
            case org.pampasim.events.Process.Load e -> handleProcessLoad(e);
            case org.pampasim.events.Process.IoOperation e -> handleProcessIoOperation(e);

            case AllocateFinished e -> handleMemoryAllocateFinished(e);
            case FreeProcessMemoryFinished e -> handleMemoryFreeProcessMemoryFinished(e);
            case DiskOperationFinished e -> handleMemoryDiskOperationFinished(e);
            case ProcessReady e -> handleMemoryProcessReady(e);
            default -> throw new IllegalStateException(
                    "[VirtualMemory] Evento do tipo " + event.getClass().getSimpleName()
                            + " não pode ser tratado, evento serial: " + event.getSerial()
            );
        }
    }

    private void handleProcessAllocate(org.pampasim.events.Process.Allocate event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        OptionalInt startAddressOpt = virtualAddressRange.findFirstContiguousFreeRange(processMemoryInfo.getSize());

        if (startAddressOpt.isEmpty()) {
            scheduleToNextClock(new Kill(this, event.getProcess())); // not enough free addresses to allocate for the new process
            LOGGER.debug("Processo de ID {} : Falha na alocação, não foi encontrado uma faixa de endereços", process.getPid().toString());
            return;
        }

        int startAddress = startAddressOpt.getAsInt();
        int endAddress = startAddress + processMemoryInfo.getSize() - 1;
        virtualAddressRange.allocatePageFrames(process.getPid(), startAddress, endAddress);
        processMemoryInfo.setVirtualAddressStart(startAddress);
        LOGGER.debug("Processo de ID {} : Alocado com sucesso nos endereços {} a {}", process.getPid().toString(), startAddress, endAddress);
        scheduleToNextClock(new org.pampasim.events.Process.Allocate(this, event.getProcess()));

    }

    private void handleProcessEnd(org.pampasim.events.Process.End event) {
        Process process = event.getProcess();
        virtualAddressRange.freeProcessPageFrame(process.getPid());
        LOGGER.debug("Processo de ID {} : Liberada a faixa de endereços virtuais", process.getPid().toString());
        scheduleToNextClock(new DeletePageTableEntry(this, event.getProcess()));
    }

    private void handleProcessLoad(org.pampasim.events.Process.Load event) {
        Process process = event.getProcess();
        ProcessMemoryInfo processMemoryInfo = process.getModuleInfo(ProcessMemoryInfo.class);
        ArrayList<Integer> accessList = processMemoryInfo.getCurrentAccessList();

        try {
            accessList.forEach(pageNo -> checkForIllegalAccess(processMemoryInfo.getVirtualAddressStart() + pageNo, process.getPid()));
        } catch (SecurityException e) {
            LOGGER.error("Process of Pid {} attempted to access a virtual address out of it's allocated range! Interrupting Process", process.getPid());
            process.setState(Process.State.TERMINATED);
            scheduleToNextClock(new Kill(this, process));
            return;
        }

        scheduleToNextClock(new TranslateVirtualAddress(this, process));
    }

    private void checkForIllegalAccess(Integer address, PidAllocator.Pid pid) {
        if (virtualAddressRange.getPageFrameOwner(address) != pid.getId()) {
            throw new SecurityException("Access denied");
        }
    }

    private void handleProcessIoOperation(org.pampasim.events.Process.IoOperation event) {
        // Nothing more than a hand off required
        // This event is sent when the processor receives a Run event (after its pages are already in the main memory), and then checks if there is an IO operation.
        // If yes, forward exec and then send the IoOperation event to be handled by the memory
        scheduleToNextClock(new IoOperation(this, event.getProcess()));
    }

    private void handleMemoryAllocateFinished(AllocateFinished event) {
        // Nothing more than a hand off required
        scheduleToNextClock(new Ready(this, event.getProcess()));
    }

    private void handleMemoryFreeProcessMemoryFinished(FreeProcessMemoryFinished event) {
        // Nothing more than a hand off required
        scheduleToNextClock(new Kill(this, event.getProcess()));
    }

    private void handleMemoryDiskOperationFinished(DiskOperationFinished event) {
        // Nothing more than a hand off required
        scheduleToNextClock(new Schedule(this, event.getProcess()));
    }

    private void handleMemoryProcessReady(ProcessReady event) {
        // Nothing more than a hand off required
        scheduleToNextClock(new Run(this, event.getProcess()));
    }

}
