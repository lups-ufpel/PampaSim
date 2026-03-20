package org.pampasim.dsl.spec;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import javafx.scene.paint.Color;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.core.EventSchedule;
import org.pampasim.core.events.*;
import org.pampasim.entity.schedulers.RespectsQuantum;
import org.pampasim.entity.schedulers.Scheduler;
import org.pampasim.memory.entity.algorithms.PageReplacementAlgorithm;
import org.pampasim.resources.Process;
import org.pampasim.events.ProcessCreationDataEvent;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import org.xml.sax.SAXException;

/// Data to set up a simulation scenario
/// Usually comes from a spec file
@Getter
@XmlRootElement
public class Spec {
    private final Logger LOGGER = LogManager.getLogger(Spec.class);
    // This string-based programming is really awkward, but needed:
    // can't instantiate a SimEntity (Scheduler) without having a simulation ready
    @XmlRootElement
    public record SchedulerInfo(Class<? extends Scheduler> clazz, Optional<Integer> quantum) {};
    @XmlRootElement
    public record ProcessorInfo(ArrayList<Integer> coreCapacities) {};

    @Setter
    private SchedulerInfo schedulerInfo;
    @Setter
    private ArrayList<ProcessorInfo> processors;
    @Setter
    private boolean hasProcManager;
    private EventSchedule eventSchedule;
    private Map<Long, Color> colorMap;
    private static JAXBContext jaxbContext;

    public Spec() {
        this.schedulerInfo = null;
        this.eventSchedule = new EventSchedule();
        this.colorMap = new HashMap<>();
        this.processors = new ArrayList<>();
        this.hasProcManager = false;
    }

    public static Spec loadSpec(InputStream stream) {
        Spec spec = new Spec();
        try {
            if (Spec.jaxbContext == null) {
                Spec.jaxbContext = JAXBContext.newInstance("org.pampasim");
            }
            Unmarshaller u = Spec.jaxbContext.createUnmarshaller();
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(new File("schemas/spec.xsd"));
            u.setSchema(schema);
            Object specObj = u.unmarshal(stream);
            org.pampasim.Spec s = (org.pampasim.Spec)specObj;

            var pInfo = s.getEntities().getProcessor();
            var cores = new ArrayList<Integer>(1);
            cores.add(pInfo.getMillionInstructionsPerSecond().intValue());
            spec.getProcessors().add(new ProcessorInfo(cores));

            var schInfo = s.getEntities().getScheduler();
            //var anyMaybe = schInfo.getAny()
            spec.setSchedulerInfo(schInfo.getFullyQualifiedClassName(), Optional.empty());

            spec.setHasProcManager(true); // don't really know why I left it optional

            s.getStimuli().getEvent().forEach(e -> {
            });
        } catch (JAXBException | SAXException e) {
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
                        colorMap.get(creationData.getCreationId())
                                .toString()
                                .substring(2) // skip the 0x leader
                );
                printer.println();
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Event addProcessArrival(Process.CreationData creationData) {
        var ev = new org.pampasim.events.External.Arrival(null, creationData);
        eventSchedule.schedule(creationData.getArrivalTick(), ev);
        return ev;
    }

    public Event removeProcessArrival(long creationId) {
        return eventSchedule.removeFirstMatch((candidate) -> {
            try {
                org.pampasim.events.External.Arrival ev = (org.pampasim.events.External.Arrival) candidate;
                return ev.getCreationData().getCreationId() == creationId;
            } catch (ClassCastException e) {
                LOGGER.debug("removeProcessArrival couldn't cast event {}", candidate);
                return false;
            }
        });
    }

    public void setSchedulerInfo(String name, Optional<Integer> quantum) {
        try (ScanResult scanResult =
                     new ClassGraph()
                             .enableClassInfo()
                             //.enableAllInfo()
                             .acceptPackages("org.pampasim") // dunno if needed
                             .scan()
        ) {
            ClassInfoList schedulerClasses
                    = scanResult.getSubclasses(Scheduler.class.getName());
            ClassInfo schedulerInfo = schedulerClasses.filter(clazz -> clazz.getName().contains(name)).getFirst();
            if (schedulerInfo == null) { throw new RuntimeException("scheduler " + name + " not found!"); }
            if (schedulerInfo.getInterfaces().filter(iface -> iface.getName().contains(RespectsQuantum.class.getName())).iterator().hasNext() == false) {
                quantum = Optional.empty();
            }
            try {
                setSchedulerInfo(
                    new SchedulerInfo((Class<? extends Scheduler>) schedulerInfo.loadClass(), quantum)
                );
            } catch (ClassCastException e) {
                throw new RuntimeException("type cast error during scheduler class load: " + e);
            }
        }
    }

    public List<String> listAvailableSchedulers() {
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .ignoreClassVisibility()
                .acceptPackages("org.pampasim")
                .scan()
        ) {
            ClassInfoList schedulerClasses =
                    scanResult.getSubclasses(Scheduler.class.getName())
                            .filter(ClassInfo::isStandardClass)
                            .filter(ci -> !ci.isAbstract());

            return schedulerClasses.stream()
                    .map(ClassInfo::getSimpleName)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    public List<String> listAvailablePageSubstitutionAlgorithms() {
        try (ScanResult scanResult = new ClassGraph()
                .enableClassInfo()
                .ignoreClassVisibility()
                .acceptPackages("org.pampasim.memory")
                .scan()
        ) {
            ClassInfoList implClasses = scanResult
                    .getClassInfo(PageReplacementAlgorithm.class.getName())
                    .getClassesImplementing();

            return implClasses.stream()
                    .map(ClassInfo::getSimpleName)
                    .sorted()
                    .collect(Collectors.toList());
        }
    }


    public List<String> listAvailableModules() {
        List<String> modules = new ArrayList<>();

        Set<String> excludedModules = Set.of(
                "core", "tools", "sim-datamodel", "events", "sim"
        );

        // we can't read arbitrary files from inside the uber-jar
        if (false) {
            try {
                File pomFile = new File("../pom.xml");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(pomFile);
                doc.getDocumentElement().normalize();

                NodeList moduleNodes = doc.getElementsByTagName("module");
                for (int i = 0; i < moduleNodes.getLength(); i++) {
                    Node node = moduleNodes.item(i);
                    String moduleName = node.getTextContent().trim();
                    if (!moduleName.isEmpty() && !excludedModules.contains(moduleName)) {
                        modules.add(moduleName);
                    }
                }

                Collections.sort(modules);
                return modules;
            } catch (Exception e) {
                throw new RuntimeException("Error parsing pom.xml to list modules", e);
            }
        } else {
                // so we do the dumb, brittle way for now (tm)
            return List.of("memory");
        }
    }



    @Override
    public String toString() {
        return "Spec with " + this.eventSchedule;
    }
}
