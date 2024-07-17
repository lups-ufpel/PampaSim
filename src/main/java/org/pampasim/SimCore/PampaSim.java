package org.pampasim.SimCore;

import org.pampasim.SimEntity.PampaSimEntity;
import org.pampasim.SimEntity.SimEntity;

import java.util.ArrayList;
import java.util.List;

public class PampaSim implements Simulation {
    private final List<PampaSimEntity> entityList;
    private final FutureQueue future;
    private final List<PampaSimEvent> processedEvents;
    private double clock;

    public PampaSim() {
        this.entityList = new ArrayList<>();
        this.processedEvents = new ArrayList<>();
        this.future = new FutureQueue();
        this.clock = 0;
    }

    @Override
    public void addEntity(PampaSimEntity entity) {
        entityList.add(entity);
    }

    @Override
    public double getClock() {
        return clock;
    }

    @Override
    public void send(PampaSimEvent event) {
        future.addEvent(event);
        future.stream().forEach(pampaSimEvent -> {
            System.out.println("evento de id: " + pampaSimEvent.getEventID());
        });
    }
    @Override
    public void start() {
        while(runClockAndProcessEvents()) {
        }
        printProcessedEvents();
    }
    public boolean runClockAndProcessEvents() {
        executeRunnableEntities();
        if(future.isEmpty()) {
            return false;
        } else {
            final PampaSimEvent first = future.first();
            clock = first.delay();
            processEvent(first);
            future.remove(first);
            return true;
        }
    }
    public boolean runClockAndProcessEventsSync() {
        executeRunnableEntities();
        if(future.isEmpty()) {
            return false;
        } else {
            while(!future.isEmpty() && future.first().delay() == clock) {
                final PampaSimEvent first = future.first();
                processEvent(first);
                future.remove(first);
            }
            clock +=1;
            return true;
        }
    }
    private void executeRunnableEntities() {
        for (PampaSimEntity pampaSimEntity : entityList) {
            if(pampaSimEntity.getState() == SimEntity.State.RUNNABLE) {
                pampaSimEntity.run();
            }
        }
    }
    protected void processEvent(final PampaSimEvent evt) {
        System.out.println("[PampaSim] Processando evento: " + evt.getEventID() + " no tempo " + clock);
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
        for (PampaSimEvent event : processedEvents) {
            System.out.println("Evento " + event.getEventID() + " Tipo: " + event.getType() + " Tempo: " + event.delay());
        }
    }

    private void processSendEvent(final PampaSimEvent evt) {
        final PampaSimEntity dest = evt.getDestination();
        dest.setEventBuffer(new PampaSimEvent(evt));
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
