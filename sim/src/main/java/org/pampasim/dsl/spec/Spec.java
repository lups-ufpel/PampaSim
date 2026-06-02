package org.pampasim.dsl.spec;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import jakarta.xml.bind.*;
import javafx.scene.paint.Color;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.pampasim.ObjectFactory;
import org.pampasim.events.ProcessCreationDataPayload;
import org.pampasim.SchedulerConfig;
import org.pampasim.VersionInfo;
import org.pampasim.core.EventSchedule;
import org.pampasim.core.entity.SimEntity;
import org.pampasim.core.events.*;
import org.pampasim.events.EventManager;
import org.pampasim.entity.schedulers.RespectsQuantum;
import org.pampasim.entity.schedulers.Scheduler;
import org.pampasim.events.External.Arrival;
import org.pampasim.memory.MemoryManagement;
import org.pampasim.memory.entity.algorithms.PageReplacementAlgorithm;
import org.pampasim.resources.Process;
import org.pampasim.resources.memory.MemoryProcessCreationData;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Nonnull;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import jakarta.xml.bind.annotation.XmlRootElement;
import org.xml.sax.SAXException;

/// Data to set up a simulation scenario
/// Usually comes from a spec file
@Getter
@XmlRootElement
public class Spec {
    private static final Logger LOGGER = LogManager.getLogger(Spec.class);
    private org.pampasim.Spec innerSpec;

    private EventSchedule eventSchedule;
    private IdentityHashMap<Event, Color> arrivalColorMap;

    private static JAXBContext jaxbContext = null;
    private static Schema specSchema = null;
    private static ObjectFactory objFact = null;

    /// Instantiates a new, empty spec. Save with saveSpec()
    public Spec() {
        this.eventSchedule = new EventSchedule();
        this.arrivalColorMap = new IdentityHashMap<>();
        this.innerSpec = new org.pampasim.Spec();
    }

    private static JAXBContext initJaxbContext() throws JAXBException {
        Spec.jaxbContext = JAXBContext.newInstance("org.pampasim:org.pampasim.resources:org.pampasim.resources.memory:org.pampasim.events");
        return Spec.jaxbContext;
    }

    private static JAXBContext getJaxbContext() throws JAXBException {
        return (Spec.jaxbContext == null)? initJaxbContext() : Spec.jaxbContext;
    }

    private static Schema initSpecSchema() throws JAXBException, SAXException {
        JAXBContext ctx = getJaxbContext();
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        var schemaSourceLines = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(
                                Spec.class.getResourceAsStream("/org/pampasim/spec.xsd")),
                        StandardCharsets.UTF_8))
                .lines();
        var rtrwBeg = Pattern.compile("<!-- runtime rewrite begin -->").asPredicate();
        var rtrwEnd = Pattern.compile("<!-- runtime rewrite end -->").asPredicate();
        var importPat = Pattern.compile("namespace=\"(?<namespace>.*)\" schemaLocation=\"(?<location>.*)\"");
        var rewriting = false;
        var schemaSourceAcc = new StringBuilder();
        for (var line : schemaSourceLines.toList()) {
            String out = line;
            if (rewriting) {
                java.net.URL url = null;
                var matcher = importPat.matcher(line);
                if (matcher.find()) {
                    var namedGroups = matcher.namedGroups();
                    var namespace = matcher.group(namedGroups.get("namespace"));
                    var location = matcher.group(namedGroups.get("location"));
                    var absoluteLocation = location.substring(location.indexOf("/org/pampasim"));
                    url = Spec.class.getResource(absoluteLocation);
                    out = "<xs:import namespace=\"" + namespace + "\" schemaLocation=\"" + url + "\"/>";
                    LOGGER.trace("Spec URL rewrite for namespace \"{}\", location \"{}\": {}", namespace, location, out);
                } else {
                    LOGGER.trace("non-import tag line ignored: {}", line);
                }
            }
            if (!rewriting && rtrwBeg.test(line)) {
                rewriting = true;
                continue;
            }
            else if (rewriting && rtrwEnd.test(line)) {
                rewriting = false;
                continue;
            }
            schemaSourceAcc.append(out);
        }
        LOGGER.trace("spec schema:\n{}", schemaSourceAcc.toString());
        specSchema = schemaFactory.newSchema(
                new StreamSource(
                        new ByteArrayInputStream(schemaSourceAcc.toString().getBytes(StandardCharsets.UTF_8))));

        return specSchema;
    }

    private static Schema getSpecSchema() throws JAXBException, SAXException {
        return (specSchema == null)? initSpecSchema() : specSchema;
    }

    public static Spec loadSpec(InputStream stream, boolean versionOverride) {
        Spec spec = new Spec();
        try {
            JAXBContext ctx = getJaxbContext();
            Unmarshaller u = ctx.createUnmarshaller();
            Schema schema = getSpecSchema();
            u.setSchema(schema);
            Object specObj = u.unmarshal(stream);
            org.pampasim.Spec s = (org.pampasim.Spec)specObj;

            var specVersion = s.getVersion();

            int major = VersionInfo.getMajor();
            int minor = VersionInfo.getMinor();
            int patch = VersionInfo.getPatch();

            if (versionOverride) {
                if (objFact == null) {
                    objFact = new ObjectFactory();
                }
                var versionVal = objFact.createSpecVersion();
                versionVal.setMajor(BigInteger.valueOf(major));
                versionVal.setMinor(BigInteger.valueOf(minor));
                versionVal.setPatch(BigInteger.valueOf(patch));
                s.setVersion(versionVal);
            } else {
                var majorMismatch = specVersion.getMajor().intValue() != major;
                var minorMismatch = specVersion.getMinor().intValue() != minor;
                var patchMismatch = specVersion.getPatch().intValue() != patch;
                // TODO / FIXME: proper exception type for this throw
                if (majorMismatch || minorMismatch || patchMismatch) {
                    throw new RuntimeException("Specification version mismatch");
                }
            }

            spec.innerSpec = s; // I'm surprised this is allowed

            var schInfo = ((JAXBElement<SchedulerConfig>) s.getEntities().getAny().stream()
                    .filter(obj -> obj instanceof JAXBElement && ((JAXBElement<?>)obj).getDeclaredType() == SchedulerConfig.class)
                    .findAny()
                    .orElseThrow())
                    .getValue();
            var quantumOpt = Optional.ofNullable(schInfo.getAny())
                    .flatMap(anyElem -> {
                        if (anyElem instanceof JAXBElement<?> elem) {
                            if (elem.getName().getLocalPart().equals("quantum")) {
                                var quantumInt = ((BigInteger)elem.getValue()).intValue();
                                return Optional.of(quantumInt);
                            }
                        }
                        return Optional.empty();
                    });
            spec.setSchedulerInfo(schInfo.getFullyQualifiedClassName(), quantumOpt);

            /* introduces line order semantics for intra tick ordering
             * which is poorly documented in the schema
             */
            var tickEventCounts = new ArrayList<Integer>();

            s.getStimuli().getEvent().forEach(e -> {
                var curTick = e.getTick().intValue();
                while (curTick >= tickEventCounts.size()) {
                    tickEventCounts.add(0);
                }
                var payloads = e.getAny();
                Class<?> eventClass = null;
                try {
                    eventClass = Spec.class.getClassLoader().loadClass(e.getFullyQualifiedClassName());

                } catch (ClassNotFoundException ex) {
                    LOGGER.error("couldn't find class {} for event {}: {}", e.getFullyQualifiedClassName(), e, ex);
                    return;
                }
                for (Object o : payloads) {
                    var possiblePayloads = EventManager.getEventPayloads().get(eventClass);
                    var bodge = false;
                    if (possiblePayloads.contains(Process.CreationData.class)) {
                        possiblePayloads = new ArrayList<>(possiblePayloads);
                        possiblePayloads.add(ProcessCreationDataPayload.class);
                        bodge = true; // TODO / FIXME: no clue how to translate these properly without restructuring the modules
                    }
                    // workaround for lambda binding
                    final Object predicateCopy = o;
                    var classMatch = possiblePayloads.stream()
                            .filter(candidate -> candidate == predicateCopy.getClass())
                            .findFirst();
                    if (classMatch.isEmpty() && !bodge) {
                        LOGGER.error("payload {} doesn't match event {}, which has payloads {}", o, e, possiblePayloads);
                        throw new RuntimeException("Error loading spec file");
                    } else if (classMatch.isPresent()) {
                        LOGGER.trace("got any object {}", o);
                        var payloadClass = classMatch.get();
                        ProcessCreationDataPayload pcdp = null; // bodge
                        Map<Class<?>, Object> moduleCreationData = new HashMap<>();
                        if (bodge) {
                            payloadClass = Process.CreationData.class;
                            var modulePayloads = payloads.stream().filter(p -> !(p.getClass() == ProcessCreationDataPayload.class));

                            // hardcoded associations for now
                            modulePayloads.forEach(payload -> {
                                if (payload.getClass() == MemoryProcessCreationData.class) {
                                    Class<?> clazz = MemoryManagement.class;
                                    moduleCreationData.put(clazz, payload);
                                }
                            });

                            pcdp = (ProcessCreationDataPayload) o;
                            var tec = Optional.ofNullable(tickEventCounts.get(curTick)).orElse(0);
                            o = new Process.CreationData(
                                    e.getTick().intValue(),
                                    pcdp.getDurationTicks().intValue(),
                                    pcdp.getStartPriority().intValue(),
                                    tec,
                                    moduleCreationData);
                        }
                        try {
                            var constructor = eventClass.getConstructor(SimEntity.class, payloadClass);
                            var eventInstance = constructor.newInstance(null, o);
                            spec.getEventSchedule().schedule(curTick, (Event) eventInstance);
                            tickEventCounts.set(curTick, tickEventCounts.get(curTick) + 1);
                            if (bodge) {
                                spec.getArrivalColorMap().put((Event)eventInstance, Color.web(pcdp.getDisplayColor()));
                            }
                        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                                 InvocationTargetException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            });
        } catch (JAXBException | SAXException e) {
            throw new RuntimeException(e);
        }
        return spec;
    }
    public static Spec loadSpec(InputStream stream) {
        return loadSpec(stream, false);
    }

    public void saveSpec(Path path) {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)){
            PrintWriter printer = new PrintWriter(writer);

            JAXBContext ctx = getJaxbContext();
            Marshaller marshaller = ctx.createMarshaller();
            Schema schema = getSpecSchema();
            marshaller.setSchema(schema);
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(this.innerSpec, printer);
        } catch (IOException | JAXBException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    public Event addProcessArrival(Process.CreationData creationData, Color c) {
        var ev = new Arrival(null, creationData);

        // FIXME: bodge
        org.pampasim.events.Event e = new org.pampasim.events.Event();
        e.setFullyQualifiedClassName(Arrival.class.getCanonicalName());
        e.setTick(BigInteger.valueOf(creationData.arrivalTick()));
        e.setIntraTickOrder(BigInteger.valueOf(ev.getIntraTickOrder()));
        var objFact = new org.pampasim.events.ObjectFactory();
        var pcdPayload = objFact.createProcessCreationDataPayload();
        pcdPayload.setStartPriority(BigInteger.valueOf(creationData.startPriority()));
        pcdPayload.setDurationTicks(BigInteger.valueOf(creationData.durationTicks()));
        var clrStr = convertColor(c);
        pcdPayload.setDisplayColor(clrStr);
        e.getAny().add(pcdPayload);

        creationData.moduleCreationData().forEach((k,v) -> {
            e.getAny().add(v);
        });

        innerSpec.getStimuli().getEvent().add(e);
        eventSchedule.schedule(creationData.arrivalTick(), ev);
        this.arrivalColorMap.put(ev, Color.web(clrStr));
        return ev;
    }

    private String convertColor(Color c) {
        int r = (int) Math.round(c.getRed() * 255);
        int g = (int) Math.round(c.getGreen() * 255);
        int b = (int) Math.round(c.getBlue() * 255);
        return String.format("%02x%02x%02x", r, g, b);
    }

    public Event removeProcessArrival(@Nonnull Process.CreationData creationData) {
        var arrivalEvent = eventSchedule.removeFirstMatch((candidate) -> {
            try {
                Arrival ev = (Arrival) candidate;
                return ev.getCreationData().equals(creationData);
            } catch (ClassCastException e) {
                LOGGER.debug("removeProcessArrival couldn't cast event {}", candidate);
                return false;
            }
        });

        // FIXME: bodge
        org.pampasim.events.Event e = new org.pampasim.events.Event();
        e.setFullyQualifiedClassName(Arrival.class.getCanonicalName());
        e.setTick(BigInteger.valueOf(creationData.arrivalTick()));
        e.setIntraTickOrder(BigInteger.valueOf(arrivalEvent.getIntraTickOrder()));
        var eventObjFact = new org.pampasim.events.ObjectFactory();
        var pcdPayload = eventObjFact.createProcessCreationDataPayload();
        pcdPayload.setStartPriority(BigInteger.valueOf(creationData.startPriority()));
        pcdPayload.setDurationTicks(BigInteger.valueOf(creationData.durationTicks()));
        pcdPayload.setDisplayColor(convertColor(arrivalColorMap.get(arrivalEvent)));
        e.getAny().add(pcdPayload);

        var eventList = innerSpec.getStimuli().getEvent();
        var found = eventList.removeIf(sev -> sev.equals(e));
        if (!found) {
            LOGGER.error("None of the arrival events match {}, {}\n=>\n{}", e, eventList, eventList.stream().map(sev -> sev.equals(e)).toList());
            assert false;
        }
        return arrivalEvent;
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
            if (!schedulerInfo.getInterfaces().filter(i -> i.getName().contains(RespectsQuantum.class.getName())).iterator().hasNext()) {
                quantum = Optional.empty();
            }
            try {
                Class<? extends Scheduler> schedulerClass = (Class<? extends Scheduler>) schedulerInfo.loadClass();
                SchedulerConfig schedConfig
                        = ((JAXBElement<SchedulerConfig>) innerSpec.getEntities().getAny().stream()
                        .filter(obj -> obj instanceof JAXBElement && ((JAXBElement<?>)obj).getDeclaredType() == SchedulerConfig.class)
                        .findAny()
                        .orElseThrow())
                        .getValue();
                schedConfig.setFullyQualifiedClassName(schedulerClass.getCanonicalName());
                var objFact = new ObjectFactory();
                quantum.ifPresent(quantumInt ->
                        schedConfig.setAny(
                                objFact.createQuantum(BigInteger.valueOf(quantumInt))
                            )
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
