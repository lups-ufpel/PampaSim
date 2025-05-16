package org.pampasim.tools;

import lombok.SneakyThrows;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.pampasim.core.dsl.EntityDSLLexer;
import org.pampasim.core.dsl.EntityDSLParser;
import org.pampasim.core.dsl.metadata.Event;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

/// Generates all the event classes from a simulation description file
public class EventCodeGenTool {
    public static Path destinationFolder;
    public static String destinationPackage;
    public static Path pkgPath;
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String fileName = args[0];
        destinationPackage = args[1];
        destinationFolder = Paths.get(args[2]);
        pkgPath = destinationFolder.resolve(Paths.get(".", destinationPackage.split("\\.")));
        Files.createDirectories(pkgPath);

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
            var dataClass = entry.getKey();
            System.out.println("processing event group that transmits " + dataClass);
            writeClasses(entry.getKey(), entry.getValue());
        }
    }

    private static void writeClasses(Class<?> dataClass, Set<Event> events) throws IOException {
        var dataMemberName = dataClass.getSimpleName().toLowerCase();
        var dataMemberGetter = "get" + dataClass.getSimpleName();
        var className = dataClass.getSimpleName() + "Event";

        StringBuilder code = new StringBuilder("package " + destinationPackage + ";\n" +
                "import lombok.Getter;\n" +
                "import org.pampasim.core.entity.SimEntity;\n" +
                "import org.pampasim.core.events.*;\n" +
                "import java.lang.reflect.Constructor;\n" +
                "import java.lang.reflect.InvocationTargetException;\n" +
                "public abstract class " +
                className +
                " extends AbstractEvent {\n" +
                "@Getter\n" +
                "private final " + dataClass.getName() + " " + dataMemberName + ";\n" +
                "public " + className + "(SimEntity source, " + dataClass.getName() +
                " data) {\nsuper(source);\n" + dataMemberName + " = data;\n}\n" +
                "@Override\n" +
                "public org.pampasim.core.events.Event cloneAs(Class<? extends org.pampasim.core.events.Event> asClass) throws\n" +
                "IncompatibleEventDataException {\n" +
                "try {\n" +
                "Constructor<? extends org.pampasim.core.events.Event> cons = asClass.getConstructor(SimEntity.class, " +
                dataClass.getName() +
                ".class);\n" +
                "return cons.newInstance(getSource(), getData());\n" +
                "} catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException _e) {\n" +
                "throw new IncompatibleEventDataException();\n" +
                "}\n" +
                "}\n" +
                "public Object getData() { return " + dataMemberGetter + "(); }");
        for (var event : events) {
            code.append(subClass(event));
        }
        code.append("}\n");

        Files.writeString(pkgPath.resolve(className + ".java"), code.toString());
    }

    static String subClass(Event event) throws IOException {
        System.out.println("processing event " + event);
        var dataClassSimpleName = event.getDataClass().getSimpleName();
        var dataClassName = event.getDataClass().getName();
        var className = event.getName();
        var superClassName = event.getDataClass().getSimpleName() + "Event";
        return "public static class " + className + " extends " + superClassName + " {\n" +
                "public " + className + "(SimEntity source, " + dataClassName + " data) {\n" +
                "super(source, data);\n" +
                "}\n" +
                "}\n";
    }
}
