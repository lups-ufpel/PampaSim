package org.pampasim.SimEntity;

import org.pampasim.SimCore.PampaSimEvent;
import org.pampasim.SimCore.PampaSimEventID;
import org.pampasim.SimCore.Simulation;
import org.pampasim.SimResources.Process;
import org.pampasim.SimResources.ProcessorCore;

public class Processor extends PampaSimEntity {
    private final ProcessorCore core;

    public Processor(Simulation simulation, ProcessorCore core) {
        super(simulation);
        this.core = core;
    }

    @Override
    public void processEvent(PampaSimEvent evt) {
        if (evt.getEventID() == PampaSimEventID.RUN_PROCESS) {
            handleExecutionRequest((Process) evt.getData());
        } else {
            System.out.println("[Processador] Evento desconhecido: " +
                    evt.getEventID());
        }
    }

    private void handleExecutionRequest(Process process) {
        core.setStatus(ProcessorCore.Status.BUSY);
        System.out.println("[Processador] Execução de processo de identificador: " +
                process.name);
        core.execute(process);
        send(getSimulation().getEntity(Scheduler.class), getSimulation().getClock() + 1, PampaSimEventID.RUN_PROCESS_ACK, process); // Aumentar o tempo em 1 unidade
    }

    private void send(final PampaSimEntity dest, double delay, PampaSimEventID eventID, final Object data) {
        System.out.println("[Processor] Enviando evento: " + eventID + " para " + dest.getClass().getSimpleName() + " com delay de " + delay);
        schedule(new PampaSimEvent(PampaSimEvent.Type.SEND, delay, this, dest, eventID, data));
    }
    public boolean isFree() {
        return this.core.isFree();
    }
    public ProcessorCore getCore(){
        return core;
    }
}
