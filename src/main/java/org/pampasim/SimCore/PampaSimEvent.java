package org.pampasim.SimCore;

import org.pampasim.SimEntity.PampaSimEntity;

public class PampaSimEvent implements EventInfo, Comparable<PampaSimEvent> {
    private Simulation simulation;
    private final Type type;
    private final double delay;
    private double endWaitingTime;
    private PampaSimEntity source;
    private PampaSimEntity destination;
    private final PampaSimEventID eventID;
    private final Object data;
    private static long serialCounter = 0; // Usado para gerar seriais únicos
    private final long serial;

    public PampaSimEvent(final Type type, final double delay, final PampaSimEntity source,
                         final PampaSimEntity destination,
                         PampaSimEventID eventID, final Object data) {
        if (delay < 0) {
            throw new IllegalArgumentException("delay must be greater than zero");
        }
        this.type = type;
        this.source = source;
        this.destination = destination;
        this.simulation = source.getSimulation();
        this.delay = delay;
        this.eventID = eventID;
        this.data = data;
        this.serial = generateSerial(); // Gera um serial único
    }

    public PampaSimEvent(final PampaSimEvent source) {
        this(source.getType(), source.delay(), source.getSource(), source.getDestination(), source.getEventID(), source.getData());
    }

    @Override
    public int compareTo(PampaSimEvent other) {
        // Primeiro, compara os tempos dos eventos.
        int res = Double.compare(delay, other.delay);
        if (res != 0) {
            return res;  // Eventos com tempos menores têm prioridade.
        }

        // Se os tempos forem iguais, compara as tags dos eventos.
        res = Integer.compare(this.eventID.ordinal(), other.eventID.ordinal());
        if (res != 0) {
            return res;  // Eventos com tags menores têm prioridade.
        }
        // Se as tags também forem iguais, compara os seriais dos eventos.
        return Long.compare(this.serial, other.serial);  // Eventos com seriais menores têm prioridade.
    }
    @Override
    public <T extends EventInfo> EventListener<T> getListener() {
        return null;
    }

    public enum Type { NULL, SEND, HOLD_DONE, CREATE }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public Type getType() {
        return type;
    }

    public double delay() {
        return delay;
    }

    public double getEndWaitingTime() {
        return endWaitingTime;
    }

    public void setEndWaitingTime(double endWaitingTime) {
        this.endWaitingTime = endWaitingTime;
    }

    public PampaSimEntity getSource() {
        return source;
    }

    public void setSource(PampaSimEntity source) {
        this.source = source;
    }

    public PampaSimEntity getDestination() {
        return destination;
    }

    public void setDestination(PampaSimEntity destination) {
        this.destination = destination;
    }

    public PampaSimEventID getEventID() {
        return eventID;
    }

    public Object getData() {
        return data;
    }

    public long getSerial() {
        return serial;
    }

    private static synchronized long generateSerial() {
        return ++serialCounter;  // Incrementa e retorna o serial único
    }
}
