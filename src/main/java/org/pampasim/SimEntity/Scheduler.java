package org.pampasim.SimEntity;

import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.PampaSimEventID;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimResources.Process;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class Scheduler extends PampaSimEntity {
    PriorityQueue<Process> readyList;
    List<Process> terminatedList;
    final long quantum;

    public Scheduler(Simulation simulation) {
        super(simulation);
        readyList = new PriorityQueue<>(Comparator.comparingInt(Process::getPriority));
        terminatedList = new ArrayList<>();
        quantum = 3;
    }

    @Override
    public void processEvent(PampaSimEvent evt) {
        System.out.println("[Scheduler] Evento recebido: " + evt.getEventID());
        switch(evt.getEventID()) {
            case PampaSimEventID.READY_PROCESS -> ready((List<Process>) evt.getData());
            case PampaSimEventID.TERMINATE_PROCESS -> finish((Process) evt.getData());
            case PampaSimEventID.RUN_PROCESS_ACK -> handleRunProcessAck((Process) evt.getData());
            default -> System.out.println("[Scheduler] Evento desconhecido: " + evt.getEventID());
        }
    }

    private void handleRunProcessAck(Process process) {
        if(process.isFinished()) {
            send(this, getSimulation().getClock()  + 0, PampaSimEventID.TERMINATE_PROCESS, process);
        }
        else if(process.getCurrentExecutionTime() > quantum) {
            System.out.println("[Scheduler] Preempção do processo " + process.name);
            process.interrupt();
            readyList.add(process);
            scheduleNextProcess();
        } else {
            send(getSimulation().getEntity(Processor.class), getSimulation().getClock() + 0, PampaSimEventID.RUN_PROCESS , process);
        }
    }

    public void finish(Process process) {
        terminatedList.add(process);
        System.out.println("[Scheduler] processo " + process.name + " finalizado.");
        Processor processor = getSimulation().getEntity(Processor.class);
        processor.getCore().setStatus(ProcessorCore.Status.FREE);
        if(!readyList.isEmpty()) {
            scheduleNextProcess();
        }
    }

    public void ready(List<Process> process) {
        readyList.addAll(process);
        Processor processor = getSimulation().getEntity(Processor.class);
        if(processor.isFree()) {
            scheduleNextProcess();
        }
    }

    private void scheduleNextProcess() {
        if (!readyList.isEmpty()) {
            var readyWaitingProcess = readyList.poll();
            var processor = getSimulation().getEntity(Processor.class);
            readyWaitingProcess.setState(Process.State.RUNNING);
            send(processor, getSimulation().getClock() + 0, PampaSimEventID.RUN_PROCESS, readyWaitingProcess);
            processor.getCore().setStatus(ProcessorCore.Status.BUSY);
        }
    }

    private void send(final PampaSimEntity dest, double delay, PampaSimEventID eventID, final Object data) {
        System.out.println("[Scheduler] Enviando evento: "
                + eventID
                + " para "
                + dest.getClass().getSimpleName()
                + " com delay de " + delay);
        schedule(new PampaSimEvent(PampaSimEvent.Type.SEND, delay, this, dest, eventID, data));
    }
}
