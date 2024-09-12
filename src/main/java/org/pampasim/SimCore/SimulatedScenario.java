package org.pampasim.SimCore;

import lombok.Getter;
import lombok.Setter;
import org.pampasim.SimEntity.ProcessManager;
import org.pampasim.SimEntity.Processor;
import org.pampasim.SimEntity.Scheduler;

import java.util.List;

@Getter
@Setter
public class SimulatedScenario {

    public List<Processor> processors;
    public Scheduler scheduler;
    public ProcessManager processManager;
    public final Simulation simulation;

    public SimulatedScenario() {
        this.simulation = new PampaSim();
    }
}
