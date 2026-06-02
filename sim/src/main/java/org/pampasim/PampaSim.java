package org.pampasim;

import jakarta.xml.bind.JAXBElement;
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
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

public class PampaSim extends SimulationBase {
    private final static Logger LOGGER = LogManager.getLogger(PampaSim.class);
    public PampaSim(SimEntity parent) {
        super(parent);
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
        PampaSim sim = new PampaSim(null);
        SchedulerConfig schedConf
                = ((JAXBElement<SchedulerConfig>) s
                .getInnerSpec()
                .getEntities()
                .getAny().stream()
                .filter(obj -> obj instanceof JAXBElement && ((JAXBElement<?>)obj).getDeclaredType() == SchedulerConfig.class)
                .findAny()
                .orElseThrow())
                .getValue();
        Class<? extends Scheduler> schedulerClass = null;
        try {
            schedulerClass = (Class<? extends Scheduler>) Thread.currentThread().getContextClassLoader().loadClass(schedConf.getFullyQualifiedClassName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        if (schedulerClass != null) try {
            Constructor<? extends Scheduler> cons = schedulerClass.getConstructor(Simulation.class);
            var instance = cons.newInstance(sim);
            if (schedConf.getAny() instanceof JAXBElement<?> anyElem) {
                if (anyElem.getName().getLocalPart().equals("quantum")) {
                    var quantum = ((BigInteger)anyElem.getValue()).intValue();
                    org.pampasim.entity.schedulers.RespectsQuantum rq_instance = (org.pampasim.entity.schedulers.RespectsQuantum) instance;
                    rq_instance.setQuantum(quantum);
                }
            };
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No valid constructors for scheduler " + schedulerClass.getName() + ", error: " + e);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("Error trying to instantiate scheduler: " + e);
        }
        try {
            /*new Processor(sim,
                    new ProcessorCore(
                            s.getProcessors()
                                    .getFirst() // Single processor, for now
                                    .coreCapacities()
                                    .getFirst() // Single core, for now
                    )
            );*/
            new Processor(sim, new ProcessorCore(1000));
        } catch (NoSuchElementException e) {
            LOGGER.warn("Possible mistake: no processor set up by spec!");
        }
        new ProcessManager(sim);
        sim.eventsSchedule = new EventSchedule(s.getEventSchedule());
        return sim;
    }

    @Override
    public void incrementWaitingTimes() {
        this.getEntity(Scheduler.class).incrementWaitingTimes();
    }
}