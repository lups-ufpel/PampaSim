package org.pampasim;

import jakarta.xml.bind.JAXBElement;
import lombok.experimental.StandardException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.EventSchedule;
import org.pampasim.core.Simulation;
import org.pampasim.core.SimulationBase;
import org.pampasim.core.entity.*;
import org.pampasim.core.events.Event;
import org.pampasim.dsl.spec.Spec;
import org.pampasim.entity.ProcessManager;
import org.pampasim.entity.Processor;
import org.pampasim.entity.schedulers.Scheduler;
import org.pampasim.memory.MemoryManagement;
import org.pampasim.resources.ProcessorCore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

public class PampaSim extends SimulationBase {
    private final static Logger LOGGER = LogManager.getLogger(PampaSim.class);
    public PampaSim() {
        super();
        this.setEventManager(new InterimEventManager(this));
    }

    @Override
    public void acceptEvent(Event evt) {
        if (!(evt instanceof org.pampasim.events.Process.Kill)) {
            super.acceptEvent(evt);
        } else {
            blackHoleEvent(evt);
        }
    }

    public void blackHoleEvent(Event event) {
        switch (event) {
            case org.pampasim.events.ProcessEvent processEvent:
                LOGGER.debug("Processo com Pid {}  finalizou sua execução e foi terminado com sucesso",
                        processEvent.getProcess().getPid()
                );
                break;
            default: LOGGER.debug("black hole got event {}", event); break;
        }
    }

    public static PampaSim fromSpec(Spec s) {
        PampaSim sim = new PampaSim();
        var entities = s.getInnerSpec().getEntities().getEntity();
        record EntityTuple (
                EntityConfig conf,
                Class<? extends SimEntity> clazz,
                Constructor<? extends SimEntity> constructor)
        {};
        @StandardException
        class EntityLoadException extends RuntimeException {}
        @StandardException
        class SimulationInitializationException extends RuntimeException {}

        var classLoader = Thread.currentThread().getContextClassLoader();
        var entityTuples = entities.stream().map(entityConfig -> {
            Class<? extends SimEntity> clazz;
            Constructor<? extends SimEntity> constructor;

            try {
                var uncastClass = classLoader.loadClass(entityConfig.getFullyQualifiedClassName());
                if (!SimEntity.class.isAssignableFrom(uncastClass)) {
                    throw new RuntimeException(new Error("Named class does not extend SimEntity base class"));
                }
                clazz = uncastClass.asSubclass(SimEntity.class);
                constructor = clazz.getConstructor();
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                throw new EntityLoadException("Couldn't load entities from spec file", e);
            }

            return new EntityTuple(entityConfig, clazz, constructor);
        }).collect(Collectors.toCollection(ArrayList::new));

        var addIfMissingList = List.of(Processor.class, ProcessManager.class);
        var errorIfMissingList = List.of(Scheduler.class);

        for (var missing : addIfMissingList) {
            if (entityTuples.stream().noneMatch(t -> missing.isAssignableFrom(t.clazz))) {
                try {
                    entityTuples.add(new EntityTuple(null, missing, missing.getConstructor()));
                    LOGGER.debug("Added missing {} entity during spec load", missing.getSimpleName());
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        for (var fatalMissing : errorIfMissingList) {
            if (entityTuples.stream().noneMatch(t -> fatalMissing.isAssignableFrom(t.clazz))) {
                throw new SimulationInitializationException("No " + fatalMissing.getSimpleName() + " specified");
            }
        }

        for (var entityTuple : entityTuples) {
            SimEntity instance = null;
            try {
                instance = entityTuple.constructor.newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new EntityLoadException(e);
            }
            if (SpecEntity.class.isAssignableFrom(entityTuple.clazz)) {
                try {
                    ((SpecEntity) instance).applySpecData(entityTuple.conf);
                } catch (SpecEntityDataMismatch e) {
                    throw new EntityLoadException(e);
                }
            }
            instance.bind(sim);
        }
        sim.eventsSchedule = new EventSchedule(s.getEventSchedule());
        return sim;
    }

    @Override
    public void incrementWaitingTimes() {
        this.getEntity(Scheduler.class).incrementWaitingTimes();
    }
}