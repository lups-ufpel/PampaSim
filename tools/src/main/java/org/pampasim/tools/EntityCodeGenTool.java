package org.pampasim.tools;

/*
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.pampasim.core.dsl.metadata.Entity;
import org.pampasim.core.dsl.metadata.Event;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/// Generates a specialized EventManager for a given
/// description file, along with the Entities used within,
/// using accompanying EntityImpl classes
public class EntityCodeGenTool {
    public record GlobalSections (String eventElements) {
        public static GlobalSections of(Stream<Event> events) {
            StringBuilder eventElements = new StringBuilder();
            for (String evName : events.map(Event::getName).toList()) {
                eventElements.append(evName).append(",\n");
            }
            return new GlobalSections(eventElements.toString());
        }

        //public String patchEventType(String eventTypeCode) {
//
        //}
        public String patchEventManager(String eventManagerCode) {
            return null;
        }
    };
    public record EntitySections(GlobalSections globals, String stateElements, String eventRegistration, String processEvent) {
        public static EntitySections of(String classCode, GlobalSections globals, Entity entity) {
            Matcher simulationNameRe = Pattern.compile("Simulation\\s+(\\w+)").matcher(classCode);
            var m = simulationNameRe.find();
            assert m;
            String simulationVarName = simulationNameRe.group(1);

            return new EntitySections(
                    globals,
                    generateStateElements(entity),
                    generateEventRegistrations(simulationVarName, entity),
                    generateProcessEvent(simulationVarName, entity)
            );
        }
        public String patchClass(String classCode) {
            String acc = classCode;
            acc = acc.replaceAll("//\\s*codegen\\s+events *\\R", globals.eventElements());
            acc = acc.replaceAll("//\\s*codegen\\s+states *\\R", stateElements);
            acc = acc.replaceAll("//\\s*codegen\\s+register\\s+handlers\\s*\\R", eventRegistration);
            acc = acc.replaceAll("//\\s*codegen\\s+processEvent\\s*\\R", processEvent);
            return acc;
        }
        public static String generateStateElements(Entity e) {
            StringBuilder acc = new StringBuilder();
            return acc.toString();
        }
        public static String generateEventRegistrations(String simulationVarName, Entity e) {
            StringBuilder acc = new StringBuilder();
            for (Event event : e.allAcceptedEvents()) {
                acc.append(simulationVarName)
                        .append(".getEventManager().addEventHandler(")
                        .append("EventType.")
                        .append(event.getName())
                        .append(");\n");
            };
            return acc.toString();
        }
        public static String generateProcessEvent(String simulationVarName, Entity e) {
            StringBuilder acc = new StringBuilder("""
                    @Override
                    public void processEvent(PampaSimEvent event) {
                    }
                    """);
            return acc.toString();
        }
    };

    protected static final String pkg = "src/main/java/org/pampasim";
    protected static final String generatedPkg = "target/generated-sources/entity-codegen-tool/org/pampasim";

    public static void main(String[] args) throws IOException {
        String fileName = args[0];
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

        GlobalSections globals = GlobalSections.of(parser.getEvents().values().stream());
        {
            FileInputStream srcFile;
            try {
                srcFile = new FileInputStream(pkg + "/core/EventManagerTemplate.java");
            } catch (FileNotFoundException fnfe) {
                System.out.println("No implementation file for the EventManager, Abort!");
                assert false;
                return; // here so the IDE static analysis doesn't break
            }
            FileOutputStream dstFile = new FileOutputStream(generatedPkg + "/PampaSimEventManager.java");
            var out = globals.patchEventManager(new String(srcFile.readAllBytes()));

        }

        for (Entity e : parser.getEntities().values()) {
            // FIXME: use the Path API
            FileInputStream srcFile;
            try {
                srcFile = new FileInputStream(pkg + "/SimEntityImpl/" + e.getName() + ".java");
            } catch (FileNotFoundException fnfe) {
                System.out.println("No implementation file for " + e + ", skipping...");
                continue;
            }
            FileOutputStream dstFile = new FileOutputStream(pkg + "/SimEntityImpl/" + e.getName() + ".java");
            System.out.println("Processing " + e.getName() + ": " + srcFile + " -> " + dstFile);
            String classCode = new String(srcFile.readAllBytes());
            EntitySections genSections = EntitySections.of(classCode, globals, e);
            dstFile.write(genSections.patchClass(classCode).getBytes(StandardCharsets.UTF_8));
        }
    }


}

*/