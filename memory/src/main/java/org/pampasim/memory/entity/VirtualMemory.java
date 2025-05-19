package org.pampasim.memory.entity;

import org.pampasim.core.Simulation;
import org.pampasim.core.events.*;
import org.pampasim.events.Memory.*;
import org.pampasim.core.events.*;
import org.pampasim.core.entity.AbstractSimEntity;
import org.pampasim.memory.resources.PageFrameController;

public class VirtualMemory extends AbstractSimEntity {

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
        switch (event) {
            case org.pampasim.events.Process.Allocate e -> handleProcessAllocate(e);
            case org.pampasim.events.Process.End e -> handleProcessEnd(e);
            case org.pampasim.events.Process.Dispatch e -> handleProcessDispatch(e);
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
        //TODO: Stub
        // TODO: could also result in a ProcessKill event if there aren't enough pages available in the virtual memory for the process
        scheduleToNextClock(new Allocate(this, event.getProcess()));
        scheduleToNextClock(new org.pampasim.events.Process.Kill(this, event.getProcess()));
    }

    private void handleProcessEnd(org.pampasim.events.Process.End event) {
        //TODO: Stub
        scheduleToNextClock(new DeletePageTableEntry(this, event.getProcess()));
    }

    private void handleProcessDispatch(org.pampasim.events.Process.Dispatch event) {
        //TODO: Stub
        scheduleToNextClock(new TranslateVirtualAddress(this, event.getProcess()));
    }

    private void handleProcessIoOperation(org.pampasim.events.Process.IoOperation event) {
        //TODO: Stub
        scheduleToNextClock(new IoOperation(this, event.getProcess()));
    }

    private void handleMemoryAllocateFinished(AllocateFinished event) {
        //TODO: Stub
        scheduleToNextClock(new org.pampasim.events.Process.Ready(this, event.getProcess()));
    }

    private void handleMemoryFreeProcessMemoryFinished(FreeProcessMemoryFinished event) {
        //TODO: Stub
        scheduleToNextClock(new org.pampasim.events.Process.Kill(this, event.getProcess()));
    }

    private void handleMemoryDiskOperationFinished(DiskOperationFinished event) {
        //TODO: Stub
        scheduleToNextClock(new org.pampasim.events.Process.Schedule(this, event.getProcess()));
    }

    private void handleMemoryProcessReady(ProcessReady event) {
        //TODO: Stub
        //TODO: could also result in a ProcessSchedule event if there was an IO operation needed
        scheduleToNextClock(new org.pampasim.events.Process.Run(this, event.getProcess()));
        scheduleToNextClock(new org.pampasim.events.Process.Schedule(this, event.getProcess()));
    }

}
