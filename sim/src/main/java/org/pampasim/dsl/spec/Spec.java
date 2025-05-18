package org.pampasim.dsl.spec;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.pampasim.core.EventSchedule;
import org.pampasim.dsl.SpecFileLexer;
import org.pampasim.dsl.SpecFileParser;
import org.pampasim.core.events.*;
import org.pampasim.entity.schedulers.RespectsQuantum;
import org.pampasim.entity.schedulers.Scheduler;
import org.pampasim.resources.Process;
import org.pampasim.events.ProcessCreationDataEvent;
import org.pampasim.core.utils.PidAllocator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/// Data to set up a simulation scenario
/// Usually comes from a spec file
@Getter
public class Spec {
    // This string-based programming is really awkward, but needed:
    // can't instantiate a SimEntity (Scheduler) without having a simulation ready
    public record SchedulerInfo(Class<? extends Scheduler> clazz, Optional<Integer> quantum) {};
    @Setter
    private SchedulerInfo schedulerInfo;
    public record ProcessorInfo(ArrayList<Integer> coreCapacities) {};
    @Setter
    private ArrayList<ProcessorInfo> processors;
    @Setter
    private boolean hasProcManager;
    private PidAllocator pidAlloc; // and this is a bodge to just make the PIDs work for now
    private EventSchedule eventSchedule;
    private Map<Long, Color> colorMap;

    public Spec() {
        this.schedulerInfo = null;
        this.pidAlloc = new PidAllocator();
        this.eventSchedule = new EventSchedule();
        this.colorMap = new HashMap<>();
        this.processors = new ArrayList<>();
        this.hasProcManager = false;
    }

    public static Spec loadSpec(Path path) {
        Spec spec;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            CharStream stream = CharStreams.fromReader(reader);
            var lexer = new SpecFileLexer(stream);
            var tokStream = new CommonTokenStream(lexer);
            var specParser = new SpecFileParser(tokStream);
            var specVisitor = new SpecVisitor();
            spec = specVisitor.visitSpecFile(specParser.specFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return spec;
    }

    public void saveSpec(Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
            PrintWriter printer = new PrintWriter(writer);

            for (ProcessorInfo processorInfo : processors) {
                printer.print("processor");
                for (var coreCapacity : processorInfo.coreCapacities) {
                    printer.format(" core mips %d count 1", coreCapacity);
                }
                printer.println(";");
            }

            if (schedulerInfo != null) {
                printer.format("scheduler %s",
                        schedulerInfo.clazz.getCanonicalName()
                                .replace(schedulerInfo.clazz.getPackageName(), "")
                                .substring(1) // remove leading dot
                );
                if (Arrays.stream(schedulerInfo.clazz.getInterfaces()).anyMatch(i -> i == RespectsQuantum.class)) {
                    printer.format(", quantum %d", schedulerInfo.quantum.orElse(1));
                }
                printer.println(";");
            }
            if (hasProcManager) {
                printer.println("procmanager;");
            }

            var allEvents = eventSchedule.values().stream().map(Collection::stream).reduce(Stream::concat);
            allEvents.orElseThrow().forEach(event -> {
                ProcessCreationDataEvent procEvent = (ProcessCreationDataEvent) event;
                Process.CreationData creationData = procEvent.getCreationData();
                printer.format("proc start %d duration %d priority %d clr #%s;",
                        creationData.getArrivalTick(),
                        creationData.getDurationTicks(),
                        creationData.getStartPriority(),
                        this.getColorMap().get(creationData.getCreationId())
                                .toString().substring(2)
                );
                printer.println();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Event addProcessArrival(Process.CreationData creationData, Color clr) {
        // color isn't a member of the Process class
        // for separation of concerns reasons? between the UI and the Sim
        // either way, that means we can't set the color here
        var ev = new org.pampasim.events.External.Arrival(null, creationData);
        eventSchedule.schedule(creationData.getArrivalTick(), ev);
        if (clr != null) {
            this.getColorMap().put(creationData.getCreationId(), clr);
        }
        return ev;
    }

    public void setSchedulerInfo(String name, Optional<Integer> quantum) {
        try (ScanResult scanResult =
                     new ClassGraph()
                             .verbose()
                             //.enableClassInfo()
                             .enableAllInfo()
                             .acceptPackages("org.pampasim") // dunno if needed
                             .scan()
        ) {
            ClassInfoList schedulerClasses
                    = scanResult.getSubclasses(Scheduler.class.getName());
            ClassInfo schedulerInfo = schedulerClasses.filter(clazz -> clazz.getName().contains(name)).getFirst();
            if (schedulerInfo == null) { throw new RuntimeException("scheduler " + name + " not found!"); }
            try {
                setSchedulerInfo(
                    new SchedulerInfo((Class<? extends Scheduler>) schedulerInfo.loadClass(), quantum)
                );
            } catch (ClassCastException e) {
                throw new RuntimeException("type cast error during scheduler class load: " + e);
            }
        }
    }

    @Override
    public String toString() {
        return "Spec with " + this.eventSchedule;
    }
}
