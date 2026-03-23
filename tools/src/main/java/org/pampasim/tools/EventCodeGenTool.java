package org.pampasim.tools;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.xml.sax.SAXException;
import org.pampasim.resources.*;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/// Generates all the event classes from a simulation description file
public class EventCodeGenTool {
    public static Path destinationFolder;
    public static String destinationPackage;
    public static Path pkgPath;
    private static JAXBContext jaxbContext;

    public static void main(String[] args) throws IOException, ClassNotFoundException, JAXBException, SAXException {
        destinationPackage = args[args.length - 2];
        destinationFolder = Paths.get(args[args.length - 1]);
        pkgPath = destinationFolder.resolve(Paths.get(".", destinationPackage.split("\\.")));
        Files.createDirectories(pkgPath);
        jaxbContext = JAXBContext.newInstance("org.pampasim.resources");
        Unmarshaller u = jaxbContext.createUnmarshaller();
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema schema = schemaFactory.newSchema(new File("schemas/simulationDescription.xsd"));
        u.setSchema(schema);

        Map<String, EventGroup> allEventGroups = new HashMap<>();

        for (int i = 0; i < args.length - 2; i++) {
            var file = new File(args[i]);
            Object descObj = u.unmarshal(file);
            SimulationDescription simDesc = (SimulationDescription) descObj;

            var eventGroups = new HashSet<EventGroup>(simDesc.getEvents().getEventGroup());
            var imports = new ArrayList<String>(simDesc.getEvents().getImport());
            var visitedImports = new HashSet<String>();
            while (!imports.isEmpty()) {
                var path = imports.removeLast();
                visitedImports.add(path);
                try {
                    Object extDescObj = u.unmarshal(new File(path));
                    SimulationDescription extDesc = (SimulationDescription) extDescObj;
                    eventGroups.addAll(extDesc.getEvents().getEventGroup());
                    var notYetSeen = extDesc.getEvents().getImport().stream().filter(p -> !visitedImports.contains(p)).toList();
                    imports.addAll(notYetSeen);

                } catch (JAXBException e) {
                    throw new RuntimeException(e);
                }
            };

            for (var group : eventGroups) {
                var groupName = group.getName();
                var payloads = group.getPayloads().getClazz();
                System.out.println("processing event group " + groupName + " that transmits " + payloads);
                writeClasses(group);
                group.getEvent().stream().map(ev -> Map.entry(groupName + "." + ev, group))
                        .forEach(entry -> {
                    allEventGroups.put(entry.getKey(), entry.getValue());
                });
            }
        }
        System.out.println("allEventsGroups: " + allEventGroups.values().stream().map(EventGroup::getName).toList());
        writeModuleInfo(new HashSet<>(allEventGroups.values()));
    }

    private static void writeModuleInfo(Set<EventGroup> eventGroups) throws IOException {
        StringBuilder code = new StringBuilder("""
                module org.pampasim.events {
                    requires org.pampasim.core;
                    requires org.pampasim.resources;
                    requires jakarta.xml.bind;
                    requires lombok;
                    opens org.pampasim.events;
                    exports org.pampasim.events;
                """);
        for (String modName : eventGroups.stream().map(EventGroup::getName).toList()) {
            code.append("\n\texports org.pampasim.events.").append(modName).append(";");
        }
        code.append("\n}\n");
        Files.writeString(destinationFolder.resolve("module-info.java"), code.toString());
    }

    private record GroupInfo (EventGroup eventGroup, String className, String dataType) {};
    private static void writeClasses(EventGroup eventGroup) throws IOException, ClassNotFoundException {
        var dataClassName = eventGroup.getPayloads().getClazz().getFirst().getFullyQualifiedClassName();
        var dataClass = Class.forName(dataClassName);
        var events = eventGroup.getEvent();
        var groupInfo = new GroupInfo(eventGroup,
                dataClass.getCanonicalName()
                        .replace(dataClass.getPackageName(), "")
                        .replace(".", "")
                        + "Event",
                dataClass
                    .getCanonicalName() // important for nested classes
                    .replace(destinationPackage, "")
                );
        String dataMemberName = ((Supplier<String>)() -> {
            var name = dataClass.getSimpleName();
            return name.substring(0,1).toLowerCase() + name.substring(1);
        }).get();
        var dataMemberGetter = "get" + dataClass.getSimpleName();

        /*
        StringBuilder code = new StringBuilder("package " + destinationPackage + ";\n" +
                "import lombok.Getter;\n" +
                "import org.pampasim.core.entity.SimEntity;\n" +
                "import org.pampasim.core.events.*;\n" +
                "import java.lang.reflect.Constructor;\n" +
                "import java.lang.reflect.InvocationTargetException;\n" +
                "public abstract class " +
                groupInfo.className +
                " extends AbstractEvent {\n" +
                "@Getter\n" +
                "private final " + groupInfo.dataType + " " + dataMemberName + ";\n" +
                "public " + groupInfo.className + "(SimEntity source, " + groupInfo.dataType +
                " data) {\nsuper(source);\n" + dataMemberName + " = data;\n}\n" +
                "@Override\n" +
                "public org.pampasim.core.events.Event cloneAs(Class<? extends org.pampasim.core.events.Event> asClass) throws\n" +
                "IncompatibleEventDataException {\n" +
                "try {\n" +
                "Constructor<? extends org.pampasim.core.events.Event> cons = asClass.getConstructor(SimEntity.class, " +
                groupInfo.dataType +
                ".class);\n" +
                "return cons.newInstance(getSource(), getData());\n" +
                "} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException _e) {\n" +
                "throw new IncompatibleEventDataException();\n" +
                "}\n" +
                "}\n" +
                "public Object getData() { return " + dataMemberGetter + "();" + "}\n");
        code.append("}\n");
         */

        try (var codeStream = EventCodeGenTool.class.getResourceAsStream("EventGroupTemplate.java")) {
            assert codeStream != null;
            var code = new String(codeStream.readAllBytes());
            code = code.replaceAll("CLASS_NAME", groupInfo.className);
            code = code.replaceAll("PAYLOAD_CLASS", groupInfo.dataType);
            code = code.replaceAll("DATA_MEMBER_GETTER", dataMemberGetter);
            code = code.replaceAll("DESTINATION_PACKAGE", destinationPackage);
            code = code.replaceAll("DATA_MEMBER_NAME", dataMemberName);
            Files.writeString(pkgPath.resolve(groupInfo.className + ".java"), code);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (var event : events) {
            writeSubClass(groupInfo, event);
        }
    }

    static String subClass(GroupInfo groupInfo, String event) throws IOException {
        System.out.println("processing event " + event);
        var classNameParts = event.split("\\.");
        var className = classNameParts[classNameParts.length-1];
        return  "package " + destinationPackage + "." + groupInfo.eventGroup.getName() + ";\n" +
                "import org.pampasim.core.events.*;\n" +
                "import org.pampasim.core.entity.SimEntity;\n" +
                "import " + destinationPackage + ".*;\n" +
                "public class " + className + " extends " + groupInfo.className + " {\n" +
                "public " + className + "(SimEntity source, " + groupInfo.dataType + " data) {\n" +
                "super(source, data);\n" +
                "}\n" +
                "}\n";
    }

    static void writeSubClass(GroupInfo groupInfo, String event) throws IOException {
        var groupPath = pkgPath.resolve(groupInfo.eventGroup.getName());
        Files.createDirectories(groupPath);
        var classNameParts = event.split("\\.");
        var className = classNameParts[classNameParts.length-1];
        Files.writeString(groupPath.resolve(className + ".java"), subClass(groupInfo, event));
    }
}
