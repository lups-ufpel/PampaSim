package org.pampasim.tools;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.pampasim.core.dsl.EntityDSLLexer;
import org.pampasim.core.dsl.EntityDSLParser;
import org.pampasim.core.dsl.metadata.Event;
import org.pampasim.core.dsl.metadata.EventGroup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;

/// Generates all the event classes from a simulation description file
public class EventCodeGenTool {
    public static Path destinationFolder;
    public static String destinationPackage;
    public static Path pkgPath;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        destinationPackage = args[args.length - 2];
        destinationFolder = Paths.get(args[args.length - 1]);
        pkgPath = destinationFolder.resolve(Paths.get(".", destinationPackage.split("\\.")));
        Files.createDirectories(pkgPath);

        List<EventGroup> allEventGroups = new ArrayList<>();

        for (int i = 0; i < args.length - 2; i++) {
            var fileName = args[i];
            CharStream stream = null;
            try {
                stream = CharStreams.fromFileName(fileName);
            } catch (IOException e) {
                System.err.println(fileName + " not found!");
                assert (false);
            }
            var lexer = new EntityDSLLexer(stream);
            var tokenStream = new CommonTokenStream(lexer);
            var parser = new EntityDSLParser(tokenStream);
            parser.setBuildParseTree(true);
            EntityDSLParser.DescriptionFileContext tree
                    = parser.descriptionFile();
            System.out.println(parser.getEvents());
            System.out.println(parser.getEntities());

            for (var entry : parser.getEventGroups().entrySet()) {
                var groupName = entry.getKey();
                var dataClass = entry.getValue().dataClass();
                System.out.println("processing event group " + groupName + " that transmits " + dataClass);
                writeClasses(entry.getValue());
            }
            allEventGroups.addAll(parser.getEventGroups().values());
        }
        writeModuleInfo(allEventGroups);
    }

    private static void writeModuleInfo(List<EventGroup> eventGroups) throws IOException {
        String code = """
module org.pampasim.events {
    requires org.pampasim.core;
    requires org.pampasim.resources;
    requires lombok;
    exports org.pampasim.events;
                """;
        for (String modName : eventGroups.stream().map(EventGroup::name).toList()) {
            code += "\n\texports org.pampasim.events." + modName + ";";
        }
        code += "\n}\n";
        Files.writeString(pkgPath.resolve("module-info.java"), code);
    }

    private record GroupInfo (EventGroup eventGroup, String className, String dataType) {};
    private static void writeClasses(EventGroup eventGroup) throws IOException {
        var dataClass = eventGroup.dataClass();
        var events = eventGroup.events();
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

        Files.writeString(pkgPath.resolve(groupInfo.className + ".java"), code.toString());

        for (var event : events) {
            writeSubClass(groupInfo, event);
        }
    }

    static String subClass(GroupInfo groupInfo, Event event) throws IOException {
        System.out.println("processing event " + event);
        var classNameParts = event.getName().split("\\.");
        var className = classNameParts[classNameParts.length-1];
        return  "package " + destinationPackage + "." + groupInfo.eventGroup.name() + ";\n" +
                "import org.pampasim.core.events.*;\n" +
                "import org.pampasim.core.entity.SimEntity;\n" +
                "import " + destinationPackage + ".*;\n" +
                "public class " + className + " extends " + groupInfo.className + " {\n" +
                "public " + className + "(SimEntity source, " + groupInfo.dataType + " data) {\n" +
                "super(source, data);\n" +
                "}\n" +
                "}\n";
    }

    static void writeSubClass(GroupInfo groupInfo, Event event) throws IOException {
        var groupPath = pkgPath.resolve(groupInfo.eventGroup.name());
        Files.createDirectories(groupPath);
        var classNameParts = event.getName().split("\\.");
        var className = classNameParts[classNameParts.length-1];
        Files.writeString(groupPath.resolve(className + ".java"), subClass(groupInfo, event));
    }
}
