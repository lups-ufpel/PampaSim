package org.pampasim.SimCore;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.model.Graph;
import guru.nidi.graphviz.model.Node;
import lombok.Getter;
import org.pampasim.SimCoreRefactor.Event;
import org.pampasim.SimCoreRefactor.EventManager;
import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimEntity.SimEntity;
import org.pampasim.Utils.GraphVisualizeable;
import static guru.nidi.graphviz.model.Factory.*;

import java.util.ArrayList;
import java.util.List;

public class PampaSim implements Simulation {
    protected final List<PampaSimEntity> entityList;
    protected final FutureQueue future;
    private final ArrayList<Event> eventsOnNextClock;
    private final List<PampaSimEvent> processedEvents;
    protected double clock;
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
    public void send(PampaSimEvent event) {
        future.addEvent(event);
        future.stream().forEach(System.out::println);
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
                final PampaSimEvent first = future.first();
                processEvent(first);
                future.remove(first);
            }
            cpuClock +=1;
            return true;
        }
    }
    protected void executeRunnableEntities() {
        for (PampaSimEntity pampaSimEntity : entityList) {
            if(pampaSimEntity.getState() == SimEntity.State.RUNNABLE) {
                pampaSimEntity.run();
            }
        }
    }
    protected void processEvent(final PampaSimEvent evt) {
        System.out.println("[PampaSim] Processando evento: " + evt.getEventID() + " no tempo " + cpuClock);
        processEventByType(evt);
        processedEvents.add(evt);
    }
    private void processEventByType(final PampaSimEvent evt) {
        switch (evt.getType()) {
            case NULL -> throw new IllegalArgumentException("Event has null type.");
            case CREATE -> processCreateEvent(evt);
            case SEND -> processSendEvent(evt);
            default -> System.out.println("[PampaSim] Tipo de evento desconhecido: " + evt.getType());
        }
    }
    private void processCreateEvent(final PampaSimEvent evt) {
        final PampaSimEntity entity = (PampaSimEntity) evt.getData();
        entity.start();
        System.out.println("[PampaSim] Evento de criação processado para a entidade: "
                + entity.getClass().getSimpleName());
    }

    private void printProcessedEvents() {
        System.out.println("Eventos processados na ordem de ocorrência:");
        processedEvents.forEach(System.out::println);
    }

    private void processSendEvent(final PampaSimEvent evt) {
        final PampaSimEntity dest = evt.getDestination();
        dest.acceptEvent(new PampaSimEvent(evt));
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
