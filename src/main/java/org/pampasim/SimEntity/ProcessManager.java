package org.pampasim.SimEntity;

import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.PampaSimEventID;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimResources.Process;
import org.pampasim.Utils.PidAllocator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProcessManager extends PampaSimEntity {

    private List<Process> processSubmittedList;
    private final PidAllocator pidAllocator;

    public ProcessManager(Simulation simulation) {
        super(simulation);
        processSubmittedList = new ArrayList<>();
        pidAllocator = new PidAllocator();
    }
    public void submitProcess(Process process) {
        process.setPid(pidAllocator.assignPid());
        process.create();
        processSubmittedList.add(process);
        System.out.println("[ProcessManager] Submetendo processo: " + process.getPid());
    }
    public void createBatchProcesses() {
        HashMap<Integer, List<Process>> batchProcessMap = new HashMap<>();
        for (Process process : processSubmittedList) {
            var arrival = process.getArrivalTime();
            batchProcessMap.computeIfAbsent(arrival, k -> new ArrayList<>()).add(process);
        }

        batchProcessMap.forEach((key, value) -> {
            schedule(createBatchProcessSubmittedEvent(key, value));
        });
    }

    private PampaSimEvent createBatchProcessSubmittedEvent(Integer delay, List<Process> processSubmittedList) {
        PampaSimEntity source = this;
        PampaSimEntity dest = getSimulation().getEntity(Scheduler.class);
        System.out.println("[ProcessManager] Evento de criação de processo criado: " +
                "com delay de " + delay);
        return new PampaSimEvent(PampaSimEvent.Type.SEND, delay, source, dest, PampaSimEventID.READY_PROCESS, processSubmittedList);
    }

    @Override
    public void processEvent(PampaSimEvent event) {
        System.out.println("[ProcessManager] Evento recebido: " + event.getEventID());
    }
    private void send(final PampaSimEntity dest, double delay, PampaSimEventID eventID, final Object data) {
        System.out.println("[ProcessManager] Enviando evento: "
                + eventID + " para " + dest.getClass().getSimpleName() + " com delay de " + delay);
        schedule(new PampaSimEvent(PampaSimEvent.Type.SEND, delay, this, dest, eventID, data));
    }
}