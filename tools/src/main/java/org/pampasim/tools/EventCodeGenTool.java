package org.pampasim.tools;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.xml.sax.SAXException;
import org.pampasim.resources.*;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
        try (var inputSchema = SimulationDescription.class.getResourceAsStream("simulationDescription.xsd")) {
            Schema schema = schemaFactory.newSchema(new StreamSource(inputSchema));
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
            var groupSet = new HashSet<>(allEventGroups.values());
            System.out.println("event groups: " + groupSet.stream().map(EventGroup::getName).toList());
            writeEventManager(groupSet);
            //writeModuleInfo(groupSet);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void writeEventManager(Set<EventGroup> eventGroups) throws IOException {
        try (var codeStream = EventCodeGenTool.class.getResourceAsStream("EventManagerTemplate.java")) {
            assert codeStream != null;
            var code = new String(codeStream.readAllBytes(), StandardCharsets.UTF_8);

            String initPayloadSchemas = eventGroups
                    .stream()
                    .flatMap(g -> g.getPayloads().getClazz().stream())
                    .map(payload -> {
                // FIXME: use resource streams like https://stackoverflow.com/a/17705322
                var payloadSchemaPutCode = """
{
    var schemaOption = SCHEMA_PATH_OPTION.map(schemaPath -> {
        try {
            Schema schema = schemaFactory.newSchema(new File((String)schemaPath));
            return schema;
        } catch (org.xml.sax.SAXException e) {
            throw new RuntimeException(e);
        }
    });
    payloadSchemas.put(PAYLOAD_CLASS, schemaOption);
}""";
                var schemaPathStr = payload.getSchema();
                var schemaPathOptStr = (schemaPathStr == null) ? "Optional.empty()" : "Optional.of(\"" + schemaPathStr + "\")";
                payloadSchemaPutCode = payloadSchemaPutCode.replaceAll("SCHEMA_PATH_OPTION", schemaPathOptStr);
                payloadSchemaPutCode = payloadSchemaPutCode.replaceAll("PAYLOAD_CLASS", payload.getFullyQualifiedClassName().replace('$', '.') + ".class");
                return payloadSchemaPutCode;
            }).collect(Collectors.joining());

            String initEventPayloads = eventGroups
                    .stream()
                    .map(group -> {
                        return group.getEvent().stream().map(eventName -> {
                            var payloadListStr = "List.of(" + group.getPayloads()
                                    .getClazz()
                                    .stream()
                                    .map(p -> p.getFullyQualifiedClassName().replace('$','.') + ".class")
                                    .reduce((lhs, rhs) -> lhs + ", " + rhs).orElse("") + ")";
                            return "eventPayloads.put(" + destinationPackage + "." + group.getName() + "." + eventName + ".class, "
                                        + payloadListStr + ");";
                        }).collect(Collectors.joining());
                    }).collect(Collectors.joining());

            var initCode = initEventPayloads;// + initPayloadSchemas;
            code = code.replaceAll("DESTINATION_PACKAGE", destinationPackage);
            code = code.replaceAll("INITIALIZE_BODY", initCode);
            Files.writeString(pkgPath.resolve("EventManager.java"), code);
        }
    }

    private static void writeModuleInfo(Set<EventGroup> eventGroups) throws IOException {
        StringBuilder code = new StringBuilder("module " + destinationPackage + """
                    {
                    requires org.pampasim.core;
                    requires org.pampasim.resources;
                    requires jakarta.xml.bind;
                    requires lombok;
                    opens org.pampasim.events;
                    exports org.pampasim.events;
                """);
        for (String modName : eventGroups.stream().map(EventGroup::getName).toList()) {
            code.append("\n\texports ").append(destinationPackage).append(".").append(modName).append(";");
        }
        code.append("\n}\n");
        Files.writeString(destinationFolder.resolve("module-info.java"), code.toString());
    }

    // legacy cruft, remove when possible
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
        var name = dataClass.getSimpleName();
        String dataMemberName = name.substring(0,1).toLowerCase() + name.substring(1);
        var dataMemberGetter = "get" + dataClass.getSimpleName();
        var dataMemberSetter = "set" + dataClass.getSimpleName();

        try (var codeStream = EventCodeGenTool.class.getResourceAsStream("EventGroupTemplate.java")) {
            assert codeStream != null;
            var code = new String(codeStream.readAllBytes(), StandardCharsets.UTF_8);
            code = code.replaceAll("CLASS_NAME", groupInfo.className);
            code = code.replaceAll("PAYLOAD_CLASS", groupInfo.dataType);
            code = code.replaceAll("DATA_MEMBER_GETTER", dataMemberGetter);
            code = code.replaceAll("DATA_MEMBER_SETTER", dataMemberSetter);
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
