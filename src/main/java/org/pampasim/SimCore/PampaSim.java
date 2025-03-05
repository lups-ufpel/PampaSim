package org.pampasim.SimCore;

import lombok.Getter;
import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimEntity.SimEntity;

import java.util.ArrayList;
import java.util.List;

public class PampaSim implements Simulation {
    private final List<PampaSimEntity> entityList;
    private final FutureQueue future;
    private final ArrayList<PampaSimEvent> eventsOnNextClock;
    private final List<org.pampasim.SimCore.PampaSimEvent> processedEvents;
    @Getter
    private final EventManager eventManager;
    @Getter
    private double cpuClock;
    @Getter
    private double simulationClock;

    public PampaSim() {
        this.entityList = new ArrayList<>();
        this.eventManager = new EventManager();
        this.eventsOnNextClock = new ArrayList<>();
        this.processedEvents = new ArrayList<>();
        this.future = new FutureQueue();
        this.cpuClock = 0;
        this.simulationClock = 0;
    }

    @Override
    public void addEntity(PampaSimEntity entity) {
        entityList.add(entity);
    }

    @Override
    public void scheduleToNextClock(org.pampasim.SimCore.PampaSimEvent event) {
        future.addEvent(event);
    }
    public boolean runClockAndProcessEvents() {
        // REFACTOR: changed this function to make use of the event manager. It'll iterate through all future events on queue
        // and send them to the buffer of each entity that handles the event
        executeRunnableEntities();
        if(eventsOnNextClock.isEmpty()) {
            simulationClock += 1;
            return false;
        } else {
            eventsOnNextClock.forEach(eventManager::handleEvent);
            simulationClock += 1;
            return true;
        }
    }
    public boolean runClockAndProcessEventsSync() {
        executeRunnableEntities();
        if(future.isEmpty()) {
            return false;
        } else {
            while(!future.isEmpty() && future.first().delay() == cpuClock) {
                final org.pampasim.SimCore.PampaSimEvent first = future.first();
                processEvent(first);
                future.remove(first);
            }
            cpuClock +=1;
            return true;
        }
    }
    private void executeRunnableEntities() {
        for (PampaSimEntity pampaSimEntity : entityList) {
            if(pampaSimEntity.getState() == SimEntity.State.RUNNABLE) {
                pampaSimEntity.processEventsinBuffer();
            }
        }
    }
    protected void processEvent(final org.pampasim.SimCore.PampaSimEvent evt) {
        System.out.println("[PampaSim] Processando evento: " + evt.getEventID() + " no tempo " + cpuClock);
        processEventByType(evt);
        processedEvents.add(evt);
    }
    private void processEventByType(final org.pampasim.SimCore.PampaSimEvent evt) {
        switch (evt.getType()) {
            case NULL -> throw new IllegalArgumentException("Event has null type.");
            case CREATE -> processCreateEvent(evt);
            case SEND -> processSendEvent(evt);
            default -> System.out.println("[PampaSim] Tipo de evento desconhecido: " + evt.getType());
        }
    }
    private void processCreateEvent(final org.pampasim.SimCore.PampaSimEvent evt) {
        final PampaSimEntity entity = (PampaSimEntity) evt.getData();
        entity.start();
        System.out.println("[PampaSim] Evento de criação processado para a entidade: "
                + entity.getClass().getSimpleName());
    }

    private void printProcessedEvents() {
        System.out.println("Eventos processados na ordem de ocorrência:");
        for (org.pampasim.SimCore.PampaSimEvent event : processedEvents) {
            System.out.println("Evento " + event.getEventID() + " Tipo: " + event.getType() + " Tempo: " + event.delay());
        }
    }

    private void processSendEvent(final org.pampasim.SimCore.PampaSimEvent evt) {
        final PampaSimEntity dest = evt.getDestination();
        dest.acceptEvent(new org.pampasim.SimCore.PampaSimEvent(evt));
        dest.setState(SimEntity.State.RUNNABLE);
        System.out.println("[PampaSim] Evento enviado para o destino: " + dest.getClass().getSimpleName());
    }

    public <T extends PampaSimEntity> T getEntity(Class<T> entityClass) {
        return entityList.stream()
                .filter(entityClass::isInstance)
                .map(entityClass::cast)
                .findFirst()
                .orElse(null);
    }
}
